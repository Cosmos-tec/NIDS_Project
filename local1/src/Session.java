import com.sun.deploy.net.HttpRequest;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.application.Html;
import org.jnetpcap.protocol.application.HtmlParser;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Session implements Runnable {
    private String sessionID;
    private Http http = new Http();
    private boolean state;
    private UserEntry ue = new UserEntry();
    private Tcp tcp;
    private PcapPacket pcap;
    private Ip4 ip = new Ip4();

    public Session(PcapPacket packet) {
        tcp = packet.getHeader(new Tcp());
        pcap = packet;
        http = packet.getHeader(new Http());
        ip = packet.getHeader(new Ip4());
        if(http != null)
            state = urlScriptTag();

    }

    public void run() {
        if(state) {
            byte[] sIP = new byte[4];
            sIP = ip.source();
            System.out.println("XSS Cross site scripting detected");
            ue.setSrcPort(Integer.toString(tcp.source()));
            ue.setDstPort(Integer.toString(tcp.destination()));
            ue.setAttacker(org.jnetpcap.packet.format.FormatUtils.ip(sIP));
            ue.setProtocol("HTTP");
            ue.setAttackType("XSS-Cross-site-scripting");
            ue.setAttackInfo("Reflected-XSS");
            ue.setAttackDescription("Attacker injected a HTML tag during a request");
            ue.setHexDump(pcap.toHexdump());
            Handler.sendMessage(TCP.sc, "XSS-Cross site scripting");
            try {
                new Alert().insertDB(ue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Alert/NotifyGUI/Store to database

    }

    public boolean urlScriptTag() {
        String request = http.fieldValue(Http.Request.RequestMethod);
        String url = http.fieldValue(Http.Request.RequestUrl);
        if(request != null) {
            //System.out.println(http.toHexdump());
            if (request.equals("GET") || request.equals("POST")) {
                //System.out.println(http);
                return url.toLowerCase().contains("script") || url.toLowerCase().contains("cookie");
            }
        }
        return false;
    }

    void htmlInjection() throws IOException {
        String request = http.fieldValue(Http.Request.RequestUrl);
        if(request != null) {
            if (request.contains("script")) {
                System.out.println("Alert Script url injection");
            }
        }
    }
}
