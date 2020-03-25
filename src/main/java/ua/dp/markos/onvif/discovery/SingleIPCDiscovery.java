package ua.dp.markos.onvif.discovery;

import ua.dp.markos.onvif.model.OnvifDeviceInfo;
import ua.dp.markos.onvif.util.RegexUtils;
import ua.dp.markos.onvif.util.XMLUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.UUID;


public class SingleIPCDiscovery {

    private final static Logger LOGGER = LoggerFactory.getLogger(SingleIPCDiscovery.class);

    /**
     * How long wait for data receiving
     */
    private static final int TIMEOUT = 3000;
    /**
     * Maximum retry amount
     */
    private static final int MAXNUM_RETRY_COUNT = 2;
    private static final String SOAP_CONTENT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<e:Envelope xmlns:e=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:w=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"" +
                    "    xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\" xmlns:dn=\"http://www.onvif.org/ver10/network/wsdl\">" +
                    "   <e:Header>" +
                    "       <w:MessageID>" +
                    "           uuid:" + UUID.randomUUID().toString() +
                    "       </w:MessageID>" +
                    "       <w:To e:mustUnderstand=\"true\">urn:schemas-xmlsoap-org:ws:2005:04:discovery</w:To>" +
                    "       <w:Action a:mustUnderstand=\"true\">http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</w:Action>" +
                    "   </e:Header>" +
                    "   <e:Body>" +
                    "   <d:Probe>" +
                    "       <d:Types>dn:NetworkVideoTransmitter</d:Types>" +
                    "   </d:Probe>" +
                    "   </e:Body>" +
                    "</e:Envelope>";

    private static String getOnvifAddressByIp(String ip) throws Exception {
        byte[] buf = new byte[10240];
        Random random = new Random();
        DatagramSocket ds = new DatagramSocket(random.nextInt(65535 - 1024 + 1) + 1024);
        InetAddress ipcIp = InetAddress.getByName(ip);
        DatagramPacket dpSend= new DatagramPacket(SOAP_CONTENT.getBytes(), SOAP_CONTENT.length(), ipcIp,3702);
        DatagramPacket dpReceive = new DatagramPacket(buf, 10240);
        ds.setSoTimeout(TIMEOUT);
        int tries = 0;
        boolean receivedResponse = false;
        while(!receivedResponse && tries< MAXNUM_RETRY_COUNT){
            ds.send(dpSend);
            try{
                ds.receive(dpReceive);
                if(!dpReceive.getAddress().equals(ipcIp)){
                    throw new IOException("Received packet from an umknown source");
                }
                receivedResponse = true;
            }catch(InterruptedIOException e){
                tries += 1;
                LOGGER.debug("Time out, " + (MAXNUM_RETRY_COUNT - tries) + " more tries...");
            }
        }
        String receiveContent = "";
        if(receivedResponse){
            receiveContent = new String(dpReceive.getData(),0,dpReceive.getLength());
        }else{
            LOGGER.debug("No response -- give up.");
        }
        ds.close();

        return receiveContent;
    }

    public static void fillOnvifAddress(OnvifDeviceInfo onvifDeviceInfo) throws Exception {
        if (StringUtils.isEmpty(onvifDeviceInfo.getIp()) || !RegexUtils.isIp(onvifDeviceInfo.getIp())) {
            throw new RuntimeException("onvif invalid IP-address");
        }

        String onvifAddress = null;
        String xmlContent = getOnvifAddressByIp(onvifDeviceInfo.getIp());
        String onvifAddressAll = XMLUtils.parseOnvifAddress(xmlContent);
        onvifAddress = onvifAddressAll.trim().split(" ")[0];

        onvifDeviceInfo.setOnvifAddress(onvifAddress);
//        return onvifDeviceInfo;
    }

    public static String probeOnvifByIp(String ip) throws Exception {
        if (StringUtils.isEmpty(ip) || !RegexUtils.isIp(ip)) {
            throw new Exception("Wrong IP--" + ip);
        }

        return XMLUtils.parseOnvifAddress(getOnvifAddressByIp(ip));
    }


}
