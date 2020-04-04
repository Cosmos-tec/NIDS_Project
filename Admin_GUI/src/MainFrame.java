import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class MainFrame extends JFrame {
    private DefaultTableModel filterModel = new DefaultTableModel();
    private JPanel topPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    JPanel alertPanel = new JPanel();
    private JTextField protocol = new JTextField(20);
    SocketChannel socketChannel;
    JTabbedPane tabbedPane = new JTabbedPane();
    Action playAction, stopAction;
    private JDesktopPane desktop;
    private String[] columnNames = {"No.", "TimeStamp", "Source Ip",
    "Destination Ip", "Protocol", "Size"};
    JScrollPane scrollPane;
    JTable table = new JTable();
    DefaultTableModel model = new DefaultTableModel();

    public MainFrame() {
        super( "IDS ADMIN GUI" );
        setJMenuBar(createMenu());
        desktop = new JDesktopPane();

        Container c = getContentPane();
        c.add( createToolBar(), BorderLayout.NORTH);
        c.add(createTab(), BorderLayout.CENTER);
        model.setColumnIdentifiers(columnNames);
        table.setModel(model);
        createFilter();
        Toolkit toolkit = getToolkit();
        Dimension dimension = toolkit.getScreenSize();

        // center window on screen
        setBounds( 100, 100, dimension.width - 200,
                dimension.height - 200 );

        setVisible( true );
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(5000));
            //serverSocketChannel.configureBlocking(false);
            String incomingData = "";
            boolean running = true;
            while (running) {
                addWindowListener(
                        new WindowAdapter() {
                            public void windowClosing( WindowEvent event )
                            {
                                shutDown();
                            }
                        }
                );
                System.out.println("Waiting for request ...");
                socketChannel = serverSocketChannel.accept();
                System.out.println("Connected to Client");
                incomingData = receivedData(socketChannel);
                while (incomingData != null && socketChannel.isConnected()) {
                    if(!incomingData.equals(""))
                    updateLiveTab(incomingData);
                    JScrollBar sb = scrollPane.getVerticalScrollBar();
                    sb.setValue( sb.getMaximum() );
                    incomingData = receivedData(socketChannel);
//                    if(incomingData.equals("Alert")) {
//                        new alertTab(tabbedPane.getTabComponentAt(4));
//                    }
                }
                running = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void shutDown()
    {
        System.exit( 0 );   // terminate program
    }

    public JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file,edit,device,help;

        file = new JMenu("File");
        edit = new JMenu("Edit");
        device = new JMenu("Device");
        help = new JMenu("Help");

        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(device);
        menuBar.add(help);

        return menuBar;
    }

    public JToolBar createToolBar () {
        JToolBar toolBar = new JToolBar();
        playAction = new playAction();
        stopAction = new stopAction();
        toolBar.add(playAction);
        toolBar.add( new JToolBar.Separator() );
        toolBar.add(stopAction);

        return toolBar;
    }

    public JTabbedPane createTab() {

        scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ImageIcon icon = new ImageIcon(getClass().getResource( "images/test.gif" ) );
        JComponent panel1 = new JSplitPane(SwingConstants.HORIZONTAL,topPanel,bottomPanel);
        topPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new GridLayout());
        topPanel.add(scrollPane);

        tabbedPane.addTab("    Live Traffic    ", icon, panel1,
                "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = new JTextArea("Panel #2");
        tabbedPane.addTab("    Resources       ", icon, panel2,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent panel3 = new JTextArea("Panel #3");
        tabbedPane.addTab("    Stored Data     ", icon, panel3,
                "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        JComponent panel4 = new JTextArea("Panel #1");
        tabbedPane.addTab("    Graph            ", icon, panel4,
                "Does nothing");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_1);

        JComponent panel5 = alertPanel;
        tabbedPane.addTab("    Alert            ", icon, panel5,
                "Does nothing at all");
        tabbedPane.setMnemonicAt(4, KeyEvent.VK_4);
        createAlertTab(panel5);
        //Thread alert = new Thread(new alertTab(panel5));
        //alert.start();
        return tabbedPane;
    }

    private class playAction extends AbstractAction {

        // set up action's name, icon, descriptions and mnemonic
        public playAction()
        {
            putValue( NAME, "Play" );
            putValue( SMALL_ICON, new ImageIcon(
                    getClass().getResource( "images/play.png" ) ) );
            putValue( SHORT_DESCRIPTION, "Play" );
            putValue( LONG_DESCRIPTION,
                    "Start/Resume live network traffic Capture " );
            putValue( MNEMONIC_KEY, new Integer( 'P' ) );
        }

        // display window in which user can input entry
        public void actionPerformed( ActionEvent e )
        {
            //Start live Capture
            sendMessage(socketChannel,"Play");
        }
    }

    private class stopAction extends AbstractAction {

        // set up action's name, icon, descriptions and mnemonic
        public stopAction()
        {
            putValue( NAME, "Stop" );
            putValue( SMALL_ICON, new ImageIcon(
                    getClass().getResource( "images/stop.png" ) ) );
            putValue( SHORT_DESCRIPTION, "Stop" );
            putValue( LONG_DESCRIPTION,
                    "Stop processing all incoming data and clear tab" );
            putValue( MNEMONIC_KEY, new Integer( 'S' ) );
        }

        // display window in which user can input entry
        public void actionPerformed( ActionEvent e )
        {
            //kill live Capture
            sendMessage(socketChannel,"Stop");
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

    private void sendMessage (SocketChannel socketChannel, String command) {
        try{
            ByteBuffer buffer = ByteBuffer.allocate(command.length() + 1);
            buffer.put(command.getBytes());
            buffer.put((byte) 0x00);
            buffer.flip();
            while(buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateLiveTab(String data) {
        String[] row = data.split(" ");
        model.addRow(row);
    }

    public void createFilter() {
        filterModel.setColumnIdentifiers(columnNames);
        JTable filterTable = new JTable(filterModel);
        JLabel  namelabel= new JLabel("Protocol: ");
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        controlPanel.add(namelabel);
        controlPanel.add(protocol);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.LINE_START);
        bottomPanel.add(new JScrollPane(filterTable),BorderLayout.CENTER);

        protocol.addKeyListener(new CustomKeyListener());
    }

    class CustomKeyListener implements KeyListener{
        public void keyTyped(KeyEvent e) {
        }
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                int rowCount = model.getRowCount();
                String[] value = new String[6];
                for(int ii=0; ii < rowCount; ii++) {
                    if(model.getValueAt(ii,4).equals(protocol.getText())) {
                        for (int i=0; i < 6; i++) {
                            value[i] = model.getValueAt(ii, i).toString();
                        }
                        filterModel.addRow(value);
                    }
                }
                JOptionPane.showMessageDialog(null, "Filtering for: " + protocol.getText());
            }
        }
        public void keyReleased(KeyEvent e) {
        }
    }

    void createAlertTab(JComponent aTab) {
        String[] columnNames = {"Attack Type", "TimeStamp", "Source Ip","Port","Info"};
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        JTable table = new JTable(model);
        alertPanel.setLayout(new GridLayout());
        alertPanel.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        alertPanel.add(new JTextArea("Hexdump of malicious packet."));
    }
}
