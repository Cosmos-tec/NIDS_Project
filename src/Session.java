import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Tcp;

public class Session {
    private String sessionID;
    Tcp tcp = new Tcp();

    public Session(PcapPacket packet) {
        System.out.println(packet);
        PcapPacket p = packet;
        getSessionPacket(p);
    }

    public PcapPacket getSessionPacket(PcapPacket p) {
        if(tcp.source() == 80 || tcp.source() == 443)
        {

        }
        return null;
    }
}
