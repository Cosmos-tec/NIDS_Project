import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.annotate.Protocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import java.util.ArrayList;
import java.util.List;

public class ICMP {

    public ICMP() {
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r != Pcap.OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s",
                    errbuf.toString());
            return;
        }
        System.out.println("Network devices found:");
        int i = 0;
        for (PcapIf device : alldevs) {
            String description = (device.getDescription() != null) ? device
                    .getDescription() : "No description available";
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(),
                    description);
        }
        PcapIf device = alldevs.get(1); // Get first device in list
        System.out.printf("\nChoosing '%s' on your behalf:\n",
                (device.getDescription() != null) ? device.getDescription()
                        : device.getName());
        int snaplen = 64 * 1024; // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000; // 10 seconds in millis
        Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
        if (pcap == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
            public void nextPacket(PcapPacket packet, String user) {
                byte[] data = packet.getByteArray(0, packet.size()); // the package data
                byte[] sIP = new byte[4];
                byte[] dIP = new byte[4];
                Ip4 ip = new Ip4();
                if (!packet.hasHeader(ip)) {
                    return; // Not IP packet
                }
                sIP = ip.source();
                dIP = ip.destination();
                /* Use jNetPcap format utilities */
                String sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                String destinationIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);

//                System.out.println("srcIP=" + sourceIP +
//                        " dstIP=" + destinationIP +
//                        " caplen=" + packet.getCaptureHeader().caplen());

                System.out.println(packet);
                if(destinationIP.equalsIgnoreCase("192.1.0.6")) {
                    //System.out.println("My PC");
                    System.out.println(packet);
                    //setPacket(packet);
//                    Tcp tcp = packet.getHeader(new Tcp());
//                    System.out.println(tcp);
//                    Icmp icmp = packet.getHeader(new Icmp());
//                    System.out.println(icmp);
//                    System.out.println(packet);
                }

                //System.out.println(packet);
            }
        };
        // capture first 10 packages
        pcap.loop(Pcap.LOOP_INFINATE/*100*/, jpacketHandler, "jNetPcap");
        pcap.close();
    }

//    public boolean setPacket(final PcapPacket packet) {
//        boolean unkown = false;
//        PcapPacket _packet = packet;
//        Ethernet eth = packet.getHeader(new Ethernet());
//        Ip4 ip = packet.getHeader(new Ip4());
//        Tcp tcp = packet.getHeader(new Tcp());
//        if (tcp == null) {
//            Udp udp = packet.getHeader(new Udp());
//            if (udp == null) {
//                Icmp icmp = packet.getHeader(new Icmp());
//                if (icmp == null) {
//                    unkown = true;
//                    setProtocol(Protocol.OTHER);
//                } else {
//                    setProtocol(Protocol.ICMP);
//                }
//            } else {
//                setProtocol(Protocol.UDP);
//            }
//        } else {
//            setProtocol(Protocol.TCP);
//        }
//        return !unkown;
//    }
}
