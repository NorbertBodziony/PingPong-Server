import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

public class DataBase {
    public static String driver = "com.mysql.jdbc.Driver";
    public static String hostname = "localhost";
    public static String dbName = "db";
    public static String url = "jdbc:mysql://" + hostname + "/" + dbName;
    public static String user = "root";
    public static String password = "";

    static {
        if (!loadDriver())
            throw new RuntimeException("cannot load driver");
    }

    public static void main(String[] args) throws SQLException {

        try {
            Connection connection = connect();
            Statement statement = createStatement(connection);



            closeConnection(connection, statement);

        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }
    public static boolean loadDriver() {
        try {
            Class.forName(driver);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    public static Connection connect() {
        try {
            return DriverManager.getConnection(url, user, password);
        }catch (SQLException e) {
            return null;
        }
    }

    public static void closeConnection(Connection connection, Statement s){
        try {
            if(!s.isClosed())
                s.close();
            if(!connection.isClosed())
                connection.close();
        }catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public static Statement createStatement(Connection connection) {
        try {
            return  connection.createStatement();
        } catch (SQLException e) {
            return null;
        }
    }
}