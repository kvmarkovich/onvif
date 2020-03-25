package ua.dp.markos.onvif.model;

import lombok.Data;

@Data
public class VideoInfo {

    private String videoEncoding;
    private Integer videoWidth;
    private Integer videoHeight;
    /**
     * 帧率(F/S)
     */
    private Integer frameRateLimit;
    /**
     * 码率(Kb/S)
     */
    private Integer bitrateLimit;
    private String streamUri;
}

