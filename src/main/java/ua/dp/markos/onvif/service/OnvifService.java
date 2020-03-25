package ua.dp.markos.onvif.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.dp.markos.onvif.discovery.SingleIPCDiscovery;
import ua.dp.markos.onvif.model.OnvifDeviceInfo;
import ua.dp.markos.onvif.model.ProfileInfo;
import ua.dp.markos.onvif.model.UsernameToken;
import ua.dp.markos.onvif.model.VideoInfo;
import ua.dp.markos.onvif.util.EncryptUtils;
import ua.dp.markos.onvif.util.HttpAuths;
import ua.dp.markos.onvif.util.OkHttpUtils;
import ua.dp.markos.onvif.util.RegexUtils;

import java.util.List;

public class OnvifService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OnvifService.class);


    private static OkHttpUtils okHttpUtils = new OkHttpUtils();


    public static List<ProfileInfo> getVideoInfo(OnvifDeviceInfo onvifDeviceInfo) throws Exception {
        if (StringUtils.isEmpty(onvifDeviceInfo.getIp()) || !RegexUtils.isIp(onvifDeviceInfo.getIp())) {
            throw new Exception("The IP of the onvif device is invalid");
        }
        if (StringUtils.isEmpty(onvifDeviceInfo.getUsername()) || StringUtils.isEmpty(onvifDeviceInfo.getPassword())) {
            throw new Exception("Username or password of onvif device is empty");
        }

        try {
            SingleIPCDiscovery.fillOnvifAddress(onvifDeviceInfo);
            UsernameToken usernameToken = EncryptUtils.generate(onvifDeviceInfo.getUsername(), onvifDeviceInfo.getPassword());

            String mediaUrl = CapabilitiesService.getMediaUrl(onvifDeviceInfo, usernameToken);
            List<ProfileInfo> profiles = CapabilitiesService.getProfiles(mediaUrl, usernameToken);
            for (ProfileInfo profile : profiles) {
                String streamUri = CapabilitiesService.getStreamUri(mediaUrl, profile.getToken(), usernameToken);
                if (streamUri.startsWith("rtsp://")) {
                    StringBuilder fillStreamUrl = new StringBuilder(streamUri.substring(0, 7))
                            .append(onvifDeviceInfo.getUsername())
                            .append(":")
                            .append(onvifDeviceInfo.getPassword())
                            .append("@")
                            .append(streamUri.substring(7));
                    VideoInfo videoInfo = profile.getVideoInfo();
                    videoInfo.setStreamUri(fillStreamUrl.toString());
                    profile.setVideoInfo(videoInfo);
                }
            }

            return profiles;
        } catch (Exception e) {
            LOGGER.error("Error communicating with camera [" + onvifDeviceInfo + "], ", e);
            throw new Exception("An error occurred during interactive communication with the camera, please check whether the network is connected, whether the camera supports the Onvif protocol, or whether the camera is working properly");
        }

    }

    public static byte[] snap(OnvifDeviceInfo onvifDeviceInfo) throws Exception {
        if (StringUtils.isEmpty(onvifDeviceInfo.getIp()) || !RegexUtils.isIp(onvifDeviceInfo.getIp())) {
            throw new Exception("Ip is not legal");
        }
        if (StringUtils.isEmpty(onvifDeviceInfo.getUsername()) || StringUtils.isEmpty(onvifDeviceInfo.getPassword())) {
            throw new Exception("Username and password are invalid");
        }

        byte[] img = new byte[0];
        SingleIPCDiscovery.fillOnvifAddress(onvifDeviceInfo);
        UsernameToken usernameToken = EncryptUtils.generate(onvifDeviceInfo.getUsername(), onvifDeviceInfo.getPassword());

        String mediaUrl = CapabilitiesService.getMediaUrl(onvifDeviceInfo, usernameToken);

        List<ProfileInfo> profiles = CapabilitiesService.getProfiles(mediaUrl, usernameToken);
        String authorization = HttpAuths.baseAuthGenerate(onvifDeviceInfo.getUsername(), onvifDeviceInfo.getPassword());
        for (int i = 0; i < profiles.size(); i++) {
            String snapshotUri = CapabilitiesService.getSnapshotUri(mediaUrl, profiles.get(i).getToken(), usernameToken);
            try {
                img = okHttpUtils.getFile(snapshotUri, authorization);
            } catch (Exception e) {
                LOGGER.error("Error getting image based on snapshot uri [" + snapshotUri + "]", e);
                if (i == profiles.size() - 1) {
                    throw new Exception(e);
                }
            }

            if (img.length > 0) {
                break;
            }
        }
        return img;
    }

}
