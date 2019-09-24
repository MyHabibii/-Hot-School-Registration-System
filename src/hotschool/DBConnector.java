package hotschool;

import static hotschool.HotSchool.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class connects to the database
 *
 * @author bilaa
 */
public class DBConnector {

    // Database information
    private static String dbURL = "jdbc:mysql://localhost:3306/hotsummer";
    private static String driverName = "com.mysql.jdbc.Driver";
    private static String user = "root";
    private static String password = "Muhaarib21";

    // Method that is trying to get connection
    public static Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                connection = DriverManager.getConnection(dbURL, user, password);
            } catch (SQLException e) {
                System.out.println("Error getting connection!");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Not able to find driver");
        }
        return connection;
    }
}
