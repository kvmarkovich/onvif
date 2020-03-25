package ua.dp.markos.onvif.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpAuthsTest {

    @Test
    void baseAuthGenerate() {
        String expected = "Basic YWRtaW46YWRtaW4=";
        String actual = HttpAuths.baseAuthGenerate("admin", "admin");
        assertEquals(expected, actual);
    }
}