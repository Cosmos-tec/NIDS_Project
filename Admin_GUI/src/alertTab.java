import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class alertTab extends JInternalFrame implements Runnable {
    private JPanel mainPanel;
    private String[] columnNames = {"No.", "TimeStamp", "Source Ip",
            "Destination Ip", "Protocol", "Size"};
    private DefaultTableModel model = new DefaultTableModel();
    //private Frame mainFrame = MainFrame.getFrames()[0];
    alertTab(JPanel aTab) {
        mainPanel = aTab;
       // mainPanel.setLayout(new GroupLayout(mainPanel));
        table();
        //mainFrame.repaint();
        //System.out.println(MainFrame.getFrames()[0]);
    }

    public void run() {

    }

    void table() {
        JTable table = new JTable();
        model.setColumnIdentifiers(columnNames);
        table.setModel(model);
        mainPanel.add(table);
    }

    void login() {

    }

    void graph() {

    }

    void attacks() {

    }
}
