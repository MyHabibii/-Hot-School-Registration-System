package hotschool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author bilaa
 */
public class DBConector {

    // instance variables
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    // no argument constructor; initialize variables
    public DBConector() {
        connection = null;
        statement = null;
        resultSet = null;
    }

    // method to establish a connection to the database
    public boolean connect(String url, String user, String pass) {
        // default return value
        boolean connected = false;

        // load JDBC driver
        initJdbcDriver(url);

        try {
            // connect to DB
            connection = DriverManager.getConnection(url, user, pass);

            // create statement
            statement = connection.createStatement();

            // change default return value
            connected = true;
        } catch (SQLException e) {
            System.err.println(e.getSQLState() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return connected;
    }

    // method to close the connection to the database and clean up the resources
    public void disconnect() {
        // empty catch blocks ignore any exceptions
        try {
            resultSet.close();
        } catch (Exception e) {
        }

        try {
            statement.close();
        } catch (Exception e) {
        }

        try {
            connection.close();
        } catch (Exception e) {
        }
    }

    // method to execute a query like the SELECT command
    public ResultSet query(String sql) {
        //default return value
        resultSet = null;

        try {
            // validate connection
            if (connection == null || connection.isClosed()) {
                System.err.println("No connection established yet.");
                return resultSet;
            }
            // execute SQL
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println(e.getSQLState() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return resultSet;
    }

    // method to execute an update for the CREATE, INSERT INTO, UPDATE commands
    // it returns the number of rows updated, or 0 for success, otherwise it 
    // returns -1 for failure
    public int update(String sql) {
        // default return value
        int resultSet = -1;

        try {
            // validate connection
            if (connection == null || connection.isClosed()) {
                System.err.println("No connection established yet.");
                return resultSet;
            }
            //execute SQL
            resultSet = statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getSQLState() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return resultSet;
    }

    // register the JDBC driver based on URL
    private void initJdbcDriver(String url) {
        try {
            if (url.contains("jdbc:mysql")) {
                Class.forName("com.mysql.jdbc.Driver");
            }   
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
