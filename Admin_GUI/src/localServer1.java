import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class localServer1 extends Thread implements Runnable {
    private SocketChannel socket;
    DefaultTableModel model1;
    JTextPane textPane;

    public localServer1(SocketChannel socketChannel, DefaultTableModel model, JTextPane txtPane) {
        System.out.println("Localserver1 thread");
        this.socket = socketChannel;
        this.model1 = model;
        this.textPane = txtPane;
    }

    public void run () {
        while(true) {
            try {
                String incomingData = receivedData(socket);
                //System.out.println(incomingData);
                if (incomingData.equals("TCP-SYN FLOOD")) {
                    JOptionPane.showMessageDialog(null, "Alert! " + incomingData + " detected");
                    new Thread(new alertTab(model1, textPane));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String receivedData(SocketChannel socketChannel) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(5000);
            String message = "";
            while (socketChannel.read(byteBuffer) > 0) {
                char byteRead = 0x00;
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    byteRead = (char) byteBuffer.get();
                    if (byteRead == 0x00) {
                        break;
                    }
                    message += byteRead;
                }
                if (byteRead == 0x00) {
                    break;
                }
                byteBuffer.clear();
            }
            return message;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
