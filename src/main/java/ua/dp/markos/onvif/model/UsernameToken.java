package ua.dp.markos.onvif.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsernameToken {
    private String username;
    private String password;
    private String nonce;
    private String created;
}
