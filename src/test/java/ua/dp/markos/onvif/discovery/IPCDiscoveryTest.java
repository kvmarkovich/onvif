package ua.dp.markos.onvif.discovery;

import org.junit.jupiter.api.Test;
import ua.dp.markos.onvif.model.OnvifDeviceInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IPCDiscoveryTest {

    @Test
    void testDiscovery() throws Exception {
        List<OnvifDeviceInfo> onvifDeviceInfos = IPCDiscovery.discovery();
        assertNotNull(onvifDeviceInfos);
    }

}