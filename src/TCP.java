import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;

public class TCP extends Handler implements Runnable {

    private Date date = new Date();
    public static String hostIP = /*"192.168.1.6";*/"192.168.1.159";
    private String userIP = "";
    private ArrayList<PcapPacket> sPacket = new ArrayList<>();
    public static SocketChannel sc;
    private Thread session;

    public TCP(PcapIf device) {
        super();
        GUI();
        StringBuilder errbuf = new StringBuilder(); // For any error msgs
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
                sendData(sc, packet);
                byte[] data = packet.getByteArray(0, packet.size()); // the package data
                byte[] sIP = new byte[4];
                byte[] dIP = new byte[4];
                long time = date.getTime();
                Timestamp ts = new Timestamp(time);
                Ip4 ip = new Ip4();
                Tcp tcp = new Tcp();
                Http http = new Http();
                session = new Thread(new Session(packet));

                if (!packet.hasHeader(ip)) {
                    return; // Not IP packet
                }
                sIP = ip.source();
                dIP = ip.destination();
                /* Use jNetPcap format utilities */
                String sourceIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
                String destinationIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);

                tcp = packet.getHeader(new Tcp());
                http = packet.getHeader(new Http());
                if(http != null)
                    session.start();
                if (tcp != null) {
                    switch (tcp.destination()) {
                        case 80:
                            //Check that multiple syn request isn't coming from the same IP and the time between each request isn't unusual
                            if (tcp.flags_SYN()) {
                                //count_syn++;
                                sPacket.add(packet);
                                userIP = sourceIP;
                            }
                            break;
                        case 443:
                            //new Session(packet);
//                            int payloadstart = tcp.getOffset() + tcp.size();
//                            JBuffer buffer = new JBuffer(64 * 1024);
//                            buffer.peer(packet, payloadstart, packet.size() - payloadstart);
//                            String payload = buffer.toHexdump(packet.size(), false, true, true);
//                            System.out.println(payload);
                            break;
                        case 22:
                            System.out.println("SSH");
                            break;
                    }
                    if (sPacket.size() >= 10 && !userIP.equalsIgnoreCase(hostIP)) {
                        if (tcp.source() == 80)
                            sPacket.add(packet);
                    }
                }
                if (sPacket.size() >= 10) {
                    try {
                        noSyn(sPacket);
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                    }
                    confirmAckPacket(sPacket, userIP);
                    for (int ii = 0; ii < sPacket.size(); ii++) {
                        sPacket.remove(ii--);
                    }
                }
            }
        };
        while(sc.isConnected())
        {
            if(MessageHandler.getMessage().equals("Play"))
                pcap.loop(Pcap.MODE_NON_BLOCKING/*100*/, jpacketHandler, "jNetPcap");
            //System.out.println("Loop infinite");
            else if(MessageHandler.getMessage().equals("Stop"))
                pcap.close();
        }
        //pcap.close();
    }

    public void run() {
        System.out.println("Executing run Method");
    }



    public void GUI() {
        SocketAddress address = new InetSocketAddress("127.0.0.1", 5000);
        try {
            SocketChannel socketChannel = SocketChannel.open(address);
            System.out.println("Connected to Admin GUI");
            sc = socketChannel;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
