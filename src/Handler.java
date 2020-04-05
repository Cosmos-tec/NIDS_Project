import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.sigtran.Sctp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Handler {

    private Tcp tcp = new Tcp();
    private Udp udp = new Udp();
    private Ip4 ip = new Ip4();
    private Sctp sctp = new Sctp();
    private Http http = new Http();
    int pCount = 1;

    public Handler() {

    }

    boolean confirmAckPacket(ArrayList<PcapPacket> sPacket, String source) {
        //check that a the syn packet has successful received an ack.
        int ii = 0;
        for (PcapPacket jHeaders : sPacket) {
            tcp = sPacket.get(ii).getHeader(new Tcp());
            if (tcp.flags_ACK() && !source.equalsIgnoreCase(TCP.hostIP) && !tcp.flags_SYN()) {
                System.out.println("Acknowledgement received: " + source + tcp);
                System.out.println("\n Number of packet: " + sPacket.size() + "\n");
                return true;
            }
            ii++;
        }
        return false;
    }

    String noSyn(ArrayList<PcapPacket> sPacket) throws DataAccessException {
        //if syn request is greater than 1 syn packet then store IP in yellow
        //and if the request is abnormal in a time span of 5 packet then store IP in Red
        byte[] sIP = new byte[4];
        ip = sPacket.get(0).getHeader(new Ip4());
        sIP = ip.source();
        int count_syn = 0;
        int ii = 0;
        String testIp = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
        for (PcapPacket jHeaders : sPacket) {
            tcp = sPacket.get(ii).getHeader(new Tcp());
            ip = sPacket.get(ii).getHeader(new Ip4());
            sIP = ip.source();
            String sourceIp = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
            if (!sourceIp.equalsIgnoreCase(TCP.hostIP) && !tcp.flags_ACK()) {
                if (tcp.flags_SYN() && sourceIp.equalsIgnoreCase(testIp))
                    //System.out.println("Syn Count: " + count_syn);
                    count_syn++;
            }
        }
        try {
            if (count_syn > 5) {
                System.out.println("Red Warning for IP: " + testIp);
                users(testIp, "Red");
                attackInfo(sPacket.get(0), "Red");
                //Alert.start();
                count_syn = 0;
                sendMessage(TCP.sc, "TCP-SYN FLOOD");
                return "Red";
            } else if (count_syn > 1) {
                System.out.println("Yellow Warning for IP: " + testIp);
                users(testIp, "Yellow");
                attackInfo(sPacket.get(0), "Yellow");
                //Alert.start();
                count_syn = 0;
                sendMessage(TCP.sc, "TCP-SYN FLOOD");
                return "Yellow";
            } else {
                count_syn = 0;
                return "Green";
            }
        } catch (ClassCastException c) {
            System.out.println("Alert Thread Error");
        }
        return "";
    }

    public static ArrayList users(String testIp, String threatLevel) {
        //Diff between a legitimate user and a malicous user by order of alert level
        //return true or false based diff result
        ArrayList<String> all_bad_IP = new ArrayList<>();
        ArrayList<String> threat = new ArrayList<>();
        all_bad_IP.add(testIp);
        threat.add(threatLevel);
        return all_bad_IP;
    }

    void attackInfo(PcapPacket packet, String threatLevel) throws DataAccessException {
        UserEntry ue = new UserEntry();
        tcp = packet.getHeader(new Tcp());
        ip = packet.getHeader(new Ip4());
        byte[] sIP = new byte[4];
        sIP = ip.source();
        String sourceIp = org.jnetpcap.packet.format.FormatUtils.ip(sIP);

        // Populate with an example attack packet
        ue.setSrcPort(Integer.toString(tcp.source()));
        ue.setDstPort(Integer.toString(tcp.destination()));
        ue.setDataSize(tcp.size());
        ue.setAttacker(sourceIp);
        ue.setProtocol("TCP");
        ue.setAttackType("DDOS");
        ue.setThreatLevel(threatLevel);
        ue.setAttackInfo("SYN-FLOOD");
        ue.setAttackDescription("Attacker is attempting to flood your server " +
                "with syn packet in other to perform a dos attack.");

        //Alert.insertDB(ue);
        try {
            Alert alert = new Alert();
            alert.insertDB(ue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(SocketChannel socketChannel, PcapPacket packet) {
        UserEntry ue = new UserEntry();
        byte[] data = packet.getByteArray(0, packet.size()); // the package data
        byte[] sIP = new byte[4];
        byte[] dIP = new byte[4];

        if (!packet.hasHeader(ip)) {
            return; // Not IP packet
        }
        sIP = ip.source();
        dIP = ip.destination();
        Date timestamp = new Date(packet.getCaptureHeader().timestampInMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dateFormatted = formatter.format(timestamp);

        ue.setpCount(pCount++);
        ue.setNo(dateFormatted);
        ue.setSrcIP(org.jnetpcap.packet.format.FormatUtils.ip(sIP));
        ue.setDstIP(org.jnetpcap.packet.format.FormatUtils.ip(dIP));
        ue.setProtocol(getProtocol(packet));
        ue.setSize(Integer.toString(packet.size()));
        MessageHandler.sendMessage(socketChannel, ue);
    }

    public String getProtocol(PcapPacket packet) {
        tcp = packet.getHeader(new Tcp());
        udp = packet.getHeader(new Udp());
        http = packet.getHeader(new Http());
        sctp = packet.getHeader(new Sctp());

        if(tcp != null)
        {
            switch (tcp.source()) {
                case 80:
                    return "HTTP";
                case 443:
                    return "HTTPS";
                case 22:
                    return "SSH";
                case 21 | 22:
                    return "FTP";
                default:
                    return "TCP";
            }
        } else if (udp != null) {
            return "UDP";
        } else if (http != null) {
            return "HTTP";
        } else if (sctp != null) {
            return "SCTP";
        }
        return "";
    }

    public static void sendMessage (SocketChannel socketChannel, String s) {
        try{
            ByteBuffer buffer = ByteBuffer.allocate(s.length() + 1);
            buffer.put(s.getBytes());
            buffer.put((byte) 0x00);
            buffer.flip();
            while(buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
