import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        TCP ids = new TCP();
        for(int ii=0; ii <= 10; ii++) {
            try {
                ids.testSendPacketUsingJBuffer();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}
