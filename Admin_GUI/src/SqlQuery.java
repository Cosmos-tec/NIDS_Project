import java.sql.*;

public class SqlQuery {
    private PreparedStatement sqlFind;
    private Connection connection;

    public SqlQuery() throws Exception {
        // connect to addressbook database
        connect();

        // locate person
        sqlFind = connection.prepareStatement(
                "SELECT attackType, ts, srcIP, srcPort, dstIP, dstPort, attackInfo FROM attackinfo");
    }

    private void connect() throws Exception {
        // Cloudscape database driver class name
        String driver = "com.mysql.cj.jdbc.Driver";

        // URL to connect to projectdatabase database
        String url = "jdbc:mysql://localhost:3306/nids?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT";

        // load database driver class
        Class.forName(driver);

        // connect to database
        connection = DriverManager.getConnection(url, "root", "root");

        connection.setAutoCommit(false);
    }

    public void close() {
        // close database connection
        try {
            sqlFind.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    protected void finalize()
    {
        close();
    }
}
