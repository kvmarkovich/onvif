package ua.dp.markos.onvif.util;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.net.util.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import ua.dp.markos.onvif.model.UsernameToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncryptUtils {

    private static String created() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.format(new Date());
    }

    private static String wsdlPasswordDigest(String nonce, String createTime, String password) {
        // WS-Security defines password digest as Base64 ( SHA1 ( nonce + created + password ) )
        MessageDigest digest = DigestUtils.getSha1Digest();
        digest.update((nonce + createTime + password).getBytes());
        return new String(Base64.encodeBase64(digest.digest()), StandardCharsets.UTF_8);
    }

    public static UsernameToken generate(String username, String password) {
        String rawNonce = RandomStringUtils.randomAlphanumeric(16);
        String nonce = new String(Base64.encodeBase64(rawNonce.getBytes()), StandardCharsets.UTF_8);
        String create = created();
        String passwordDigest = wsdlPasswordDigest(rawNonce, create, password);
        return UsernameToken.builder()
                .username(username)
                .password(passwordDigest)
                .nonce(nonce)
                .created(create)
                .build();
    }

}
