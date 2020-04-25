import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Alert implements UserDataAccess {

    private  Connection connection;
    private  PreparedStatement sqlAttackInfo;
    private  PreparedStatement sqlDescription;
    private  PreparedStatement sqlProtocol;
    private  PreparedStatement sqlPacketInfo;
    private  PreparedStatement sqlattactType;

    // set up PreparedStatements to access database
    public Alert() throws Exception
    {
        // connect to project database

        connect();

        sqlAttackInfo = connection.prepareStatement(
                "INSERT INTO attackinfo (srcIP, dstIP, srcPort, dstPort, protocol, attackType, attackInfo)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
        sqlDescription = connection.prepareStatement(
                "INSERT INTO description ( discription, hexPacket)" +
                        "VALUES (?, ?)");

        /*sqlAttackInfo = connection.prepareStatement(
                "INSERT INTO attackinfo ( userID, protocol, attackType ) " +
                        "VALUES ( ? , ? , ? )" );

        sqlProtocol = connection.prepareStatement(
                "INSERT INTO protocol (Aid,ptype,pport) " +
                        "VALUES ( ? , ? , ?)" );

        sqlPacketInfo = connection.prepareStatement(
                "INSERT INTO packetInfo (srcIP,size,srcPort,dstPort,protocol) " +
                        "VALUES ( ? , ? , ? , ? , ? )" ); */

    }

    public  void insertDB(UserEntry uE) throws DataAccessException {
        try {
            int result;

            sqlAttackInfo.setString(1, uE.getAttacker());
            sqlAttackInfo.setString(2, TCP.hostIP);
            sqlAttackInfo.setString(3, uE.getSrcPort());
            sqlAttackInfo.setString(4, uE.getDstPort());
            sqlAttackInfo.setString(5, uE.getProtocol());
            sqlAttackInfo.setString(6, uE.getAttackType());
            sqlAttackInfo.setString(7, uE.getAttackInfo());
            result = sqlAttackInfo.executeUpdate();
            //System.out.println("Result: " + result);

            if(result == 0) {
                System.out.println("Something went wrong");
                connection.rollback();
            }

            sqlDescription.setString(1, uE.getAttackDescription());
            sqlDescription.setString(2, uE.getHexDump());
            result = sqlDescription.executeUpdate();

            if(result == 0) {
                System.out.println("Something went wrong");
                connection.rollback();
            }
            // get Aid from attackinfo table on the data base
            //sqlProtocol.setString(1, );
            /*
            sqlPacketInfo.setString(1, uE.getAttacker());
            sqlPacketInfo.setInt(2, uE.getDataSize());
            sqlPacketInfo.setString(3, uE.getSrcPort());
            sqlPacketInfo.setString(4, uE.getDstPort());
            sqlPacketInfo.setString(5, uE.getProtocol());
            result = sqlPacketInfo.executeUpdate();

            if(result == 0) {
                System.out.println("Something went wrong");
                connection.rollback();
            } */

            connection.commit();

        } catch (SQLException sqlException) {
            try {
                connection.rollback(); // rollback update
            }

            // handle exception rolling back transaction
            catch ( SQLException exception ) {
                throw new DataAccessException( exception );
            }
        }
    }

    private void connect() throws Exception
    {
        // Cloudscape database driver class name
        String driver = "com.mysql.cj.jdbc.Driver";

        // URL to connect to projectdatabase database
        String url = "jdbc:mysql://localhost:3306/nids?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT";

        // load database driver class
        Class.forName( driver );

        // connect to database
        connection = DriverManager.getConnection( url,"root","root" );

        connection.setAutoCommit( false );
    }

    public void close() {
        try {
            sqlPacketInfo.close();
            sqlProtocol.close();
            sqlAttackInfo.close();
            connection.close();
        }  // end try

        // detect problems closing statements and connection
        catch ( SQLException sqlException ) {
            sqlException.printStackTrace();
        }
    }
}
