import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageHandler {
    public static String data = "";

    public static String receivedData(SocketChannel socketChannel) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(500);
            String message = "";
            socketChannel.configureBlocking(false);
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

    public static void sendMessage (SocketChannel socketChannel, UserEntry ue) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(ue.all().length() + 1);
            buffer.put(ue.all().getBytes());
            buffer.put((byte) 0x00);
            buffer.flip();
            while(buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static String getMessage() {
        data = receivedData(TCP.sc);
//        if(!receivedData(TCP.sc).equals(""))
//            data = receivedData(TCP.sc);
//            else
//                data = "Play";
        return data;
    }
}
