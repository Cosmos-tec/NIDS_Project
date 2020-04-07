import java.sql.*;

public class SqlQuery {
    private PreparedStatement sqlFind;
    private PreparedStatement sqlFind1;
    private Connection connection;

    public SqlQuery() throws Exception {
        // connect to addressbook database
        connect();

        // locate person
        sqlFind = connection.prepareStatement(
                "SELECT srcIP,  dstIP, srcPort, dstPort, ts, attackType, attackInfo FROM attackinfo");
        sqlFind1 = connection.prepareStatement(
                "SELECT discription, hexPacket FROM description");
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

    public ResultSet findAlertInfo()  {
        try {
            ResultSet resultSet = sqlFind.executeQuery();
            //System.out.println("Result: " + result);

            // if no records found, return immediately
            if ( !resultSet.next() )
                return null;

            return resultSet;

        } catch (SQLException sqlException) {
            return null;
        }
    }

    public ResultSet findAlertDescription() {
        try {
            ResultSet resultSet = sqlFind1.executeQuery();
            //System.out.println("Result: " + result);

            // if no records found, return immediately
            if ( !resultSet.next() )
                return null;

            return resultSet;

        } catch (SQLException sqlException) {
            return null;
        }
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
