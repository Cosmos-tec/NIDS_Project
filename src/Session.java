import com.sun.deploy.net.HttpRequest;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.JField;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.io.UnsupportedEncodingException;

public class Session implements Runnable {
    private String sessionID;
    Tcp tcp = new Tcp();
    Http http = new Http();

    public Session(PcapPacket packet) {
        http = packet.getHeader(new Http());
    }

    public void run() {
        String request = http.fieldValue(Http.Request.RequestMethod);
        if(request.equals("GET"))
        {
            boolean illegalChar = http.toHexdump().startsWith("<");
            System.out.println(illegalChar);
        }
        System.out.println(request);

    }
}
