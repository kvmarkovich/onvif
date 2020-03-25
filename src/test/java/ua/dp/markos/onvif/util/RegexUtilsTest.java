package ua.dp.markos.onvif.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegexUtilsTest {

    @Test
    void getIpPortFromUrl() {
        String ipPortFromUrl = RegexUtils.getIpPortFromUrl("http://192.168.31.2:8000/onvif-http/snapshot?Profile_2");
        assertEquals("192.168.31.2:8000", ipPortFromUrl);
    }

    @Test
    void extractIpFromString() {
        String ipFromString = RegexUtils.extractIpFromString("http://192.168.31.2:8000/onvif/device_service/192.13.14.15/fewwq32");
        assertEquals("192.168.31.2", ipFromString);
    }
}