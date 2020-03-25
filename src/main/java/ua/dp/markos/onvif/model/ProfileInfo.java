package ua.dp.markos.onvif.model;

import lombok.Data;

@Data
public class ProfileInfo {

    private String name;
    private String token;

    private VideoInfo videoInfo;
}
