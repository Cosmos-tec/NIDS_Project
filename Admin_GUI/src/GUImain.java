import javax.swing.*;

public class GUImain {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
            new MainFrame();
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}


