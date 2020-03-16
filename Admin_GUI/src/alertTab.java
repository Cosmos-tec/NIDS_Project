import javax.swing.*;
import java.awt.*;

public class alertTab extends JInternalFrame implements Runnable {

    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    alertTab(Component aTab) {
        Component alert = aTab;
        System.out.println(alert);
    }

    public void run() {

    }
}
