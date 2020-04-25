import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IDS {
    private Scanner input = new Scanner(System.in);
    private String userInput = "";

    public IDS() {
        List<PcapIf> allNIC = new ArrayList<PcapIf>(); //Store all network adapter
        StringBuilder errbuf = new StringBuilder(); // Store error message
        int r = Pcap.findAllDevs(allNIC, errbuf); // Identify all network adapter
        /* Error Checking for readable Network adapter */
        if (r != Pcap.OK || allNIC.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s",
                    errbuf.toString());
            return;
        }
        System.out.println("Network devices found:");
        int i = 0;
        for (PcapIf device : allNIC) {
            String description = (device.getDescription() != null) ? device
                    .getDescription() : "No description available";
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(),
                    description);
        }

        System.out.print("> ");
        userInput = input.nextLine();
        int result = Integer.parseInt(userInput);
        PcapIf device = allNIC.get(result); // Get Selected device
        System.out.printf("\nChoosing '%s' on your behalf:\n",
                (device.getDescription() != null) ? device.getDescription()
                        : device.getName());

        i = 0;
        System.out.printf("#%d TCP Attack \n",i++);
        System.out.printf("#%d UDP Attack \n",i++);
        System.out.printf("#%d ICMP Attack \n",i);
        System.out.print("> ");
        userInput = input.nextLine();
        result = Integer.parseInt(userInput);

        if(result == 0) {
            Thread Tcp = new Thread(new TCP(device));
            Tcp.start();
        }
    }

    public static void main(String[] args) {
        try {
            //Alert alert = new Alert();
            IDS ids = new IDS();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
