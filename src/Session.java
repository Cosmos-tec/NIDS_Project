import com.sun.deploy.net.HttpRequest;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.application.Html;
import org.jnetpcap.protocol.application.HtmlParser;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Session implements Runnable {
    private String sessionID;
    private Http http = new Http();
    private boolean state;

    public Session(PcapPacket packet) {
        http = packet.getHeader(new Http());
        if(http != null)
            state = urlScriptTag();
        session ();
    }

    public void run() {
        if(state)
            System.out.println("XSS Cross site scripting detected");
        //Alert/NotifyGUI/Store to database

        try {
            htmlInjection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean urlScriptTag() {
        String request = http.fieldValue(Http.Request.RequestMethod);
        String url = http.fieldValue(Http.Request.RequestUrl);
        String data = http.fieldValue(Http.Response.ResponseCode);
        if(request != null) {
            //System.out.println(http.toHexdump());
            if (request.equals("GET") || request.equals("POST")) {
                //System.out.println(http);
                return url.contains("%3Cscript");
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

    String session () {
        if(http != null)
        {
            //System.out.println(http);
        }
//        boolean cookie = http.hasField(Http.Request.Cookie);
//        if(cookie){
//            System.out.println(http.fieldValue(Http.Request.Cookie));
//        }
        return "";
    }
}
