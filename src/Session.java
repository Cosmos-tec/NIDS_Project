import com.sun.deploy.net.HttpRequest;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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
        if(checkRequestIsValid()) {
            byte[] sIP;
            sIP = ip.source();
            ue.setSrcPort(Integer.toString(tcp.source()));
            ue.setDstPort(Integer.toString(tcp.destination()));
            ue.setAttacker(org.jnetpcap.packet.format.FormatUtils.ip(sIP));
            ue.setProtocol("HTTP");
            ue.setAttackType("Session-Hijacking");
            ue.setAttackInfo("Duplicate-SessID");
            ue.setAttackDescription("Attacker is using atemptting to use a dead/live session of a legit user");
            ue.setHexDump(pcap.toHexdump());
            Handler.sendMessage(TCP.sc, "Session Hijacking");
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

    boolean checkRequestIsValid() {
        String cookie = http.fieldValue(Http.Request.Cookie);
        String user_agent = http.fieldValue(Http.Request.User_Agent);
        String request = http.fieldValue(Http.Request.RequestMethod);
        if(http != null) {
            if(request != null) {
                if (request.equals("GET")) {
                    if (user_agent != null) {
                        if (user_agent.contains("curl") || user_agent.contains("wget")) {
                            String[] sessionID = cookie.trim().split(";");
                            if (sessionID[1].contains("PHPSESSID")) {
                                System.out.println("Session Hijacking");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
