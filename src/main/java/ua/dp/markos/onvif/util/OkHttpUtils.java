package ua.dp.markos.onvif.util;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OkHttpUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(OkHttpUtils.class);

    public String okHttp3XmlPost(String url, String body) throws Exception {

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(null, body))
                .addHeader("Content-Type", "application/soap+xml")
                .build();

        Response response = null;

        try {
            response = OkHttpClientSingleton.getInstance().newCall(request).execute();
        } catch (Exception e) {
            LOGGER.debug("Connection exception" + url, e);
            throw e;
        }

        LOGGER.debug("Connect url = ["+ url +"], send ["+ body +"], return ["+ response.toString () + "]");

        LOGGER.debug("response code = " + response.code());
        if (response.isSuccessful()) {
            String s = response.body().string();
            return s;
        } else if (response.code() >= 400 && response.code() < 500) {
            throw new Exception("Device authentication failed (wrong username or password)");
        } else {
            LOGGER.debug("Unsuccessful return:  "+ response.body().string());
            throw new Exception("Unsuccessful return");
        }
    }

    public byte[] getFile(String url, String authorization) throws Exception {
        if (authorization == null) {
            authorization = "NOAuthorization";
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", authorization)
                .build();
        Response response = null;
        try {
            response = OkHttpClientSingleton.getInstance().newCall(request).execute();
        } catch (Exception e) {
            LOGGER.debug("Connection exception" + url, e);
            throw new RuntimeException(e);
        }

        if (response.isSuccessful()) {
            if (response .body() != null) {
                return response.body().bytes();
            }
            return new byte[0];
        } else {
            return new byte[0];
        }
    }
}

