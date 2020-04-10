import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JMemoryPacket;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TCP {

    public TCP() {
        try {
            testSendPacketUsingJBuffer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void testSendPacketUsingJBuffer() throws UnknownHostException {
        JPacket packet =
                new JMemoryPacket(JProtocol.ETHERNET_ID,
                        "0016b6c13cb10021 5db0456c08004500 "
                                + "00340e8e40008006 9c54c0a80165d822 "
                                + "b5b1c1cf005020ce 4303000000008002 "
                                + "2000d94300000204 05b4010303020101 " + "0402");

        InetAddress dst = InetAddress.getByName("192.168.1.159");
        InetAddress src = InetAddress.getByName("192.168.10.100");

        Ip4 ip = packet.getHeader(new Ip4());
        Tcp tcp = packet.getHeader(new Tcp());

        ip.destination(dst.getAddress());
        ip.source(src.getAddress());

        ip.checksum(ip.calculateChecksum());
        tcp.checksum(tcp.calculateChecksum());
        packet.scan(Ethernet.ID);

        System.out.println(packet);
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        /*List all device and select NIC #1*/
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;

        }
        PcapIf device = alldevs.get(2); /*2*/// We know we have atleast 1 device

        //Open network interface
        int snaplen = 64 * 1024; // Capture all packets, no trucation
        int flags = Pcap.MODE_NON_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000; // 10 seconds in millis
        Pcap pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        System.out.println("Device ->" + device.getName());

        try {
            if (pcap.sendPacket(packet) != Pcap.OK) {
                System.err.println(pcap.getErr());
            }
        } finally {
            //close interface
            pcap.close();
        }
    }
}
