package ua.dp.markos.onvif.model;

import lombok.Data;

@Data
public class OnvifDeviceInfo {
    private String ip;
    private String onvifAddress;
    private String username;
    private String password;

}
