package ua.dp.markos.onvif.util;

import java.io.IOException;
import java.net.*;

public class NetworkUtils {

    public static InetAddress getLocalHostLANAddress() throws IOException {
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            InetAddress localAddress = datagramSocket.getLocalAddress();
            if (localAddress.getHostAddress().equals("0.0.0.0")) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("google.com", 80));
                    localAddress = socket.getLocalAddress();
                }
            }
            return localAddress;
        }
    }
}
