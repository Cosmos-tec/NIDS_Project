import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.sql.ResultSet;
import java.sql.SQLException;

public class alertTab extends SqlQuery implements Runnable {
    private Document doc;
    private String data = "";
    private SqlQuery db = new SqlQuery();
    private ResultSet result;
    private JTextPane txtPane;

    public alertTab(DefaultTableModel model, JTextPane textPane) throws Exception {

        if(model.getRowCount() > 0)
            for(int ii=model.getRowCount()-1; ii > -1; ii--)
                model.removeRow(ii);
        txtPane = textPane;
        doc = textPane.getStyledDocument();
        result = db.findAlertInfo();
        data = result.getString(6);
        data += " " + result.getString(5).replaceAll(" ","-");
        data += " " + result.getString(1)+":"+result.getString(3);
        data += " " + result.getString(2)+":"+result.getString(4);
        data += " " + result.getString(7);
        String[] row = data.split(" ");
        model.addRow(row);

        while (result.next()) {
            data = result.getString(6);
            data += " " + result.getString(5).replaceAll(" ","-");
            data += " " + result.getString(1)+":"+result.getString(3);
            data += " " + result.getString(2)+":"+result.getString(4);
            data += " " + result.getString(7);
            row = data.split(" ");
            model.addRow(row);
        }
        addRowListener(model);
    }

    public void run() {
        System.out.println("End of Run");
    }

    public void addRowListener(DefaultTableModel model) {
        model.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                try {
                //System.out.println(e.getFirstRow() + " " + model.getValueAt(0,0) );
                //result.setFetchSize(2);
                    ResultSet result = db.findAlertDescription();
                    String[] str = new String[5];
                    //int countResult = result.getInt(1);
                    doc = txtPane.getStyledDocument();
                    if (e.getFirstRow() != 0) {
                        for (int ii = 0; ii < e.getFirstRow(); ii++) {
                            if(result.next()) {
                                str[ii] = result.getString(2);
                            }
                            //System.out.println("sql: " + result.getRow() + " selectedRow: " + e.getFirstRow());
                        }
                    } else {
                        doc.remove(0, doc.getLength());
                        doc.insertString(doc.getLength(), result.getString(2), null);
                    }
                    for (String s : str) {
                        if(s != null) {
                            doc.remove(0, doc.getLength());
                            doc.insertString(doc.getLength(), str[e.getFirstRow()-1], null);
                        }
                        System.out.println(s);
                    }
                } catch (BadLocationException | SQLException sql) {
                    sql.printStackTrace();
                }
            }
        });
    }
}
