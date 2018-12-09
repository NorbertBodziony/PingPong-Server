import java.sql.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

public class DataBase {
    public static String driver = "com.mysql.cj.jdbc.Driver";
    public static String hostname = "localhost";
    public static String dbName = "test";
    public static String url = "jdbc:mysql://localhost:3306/db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public static String user = "root";
    public static String password = "";

    static {
        if (!loadDriver())
            throw new RuntimeException("cannot load driver");
    }

    public static void main(String[] args) throws SQLException {

        try {
            Connection connection = connect();

            DatabaseMetaData dbm = connection.getMetaData();

            ResultSet tables = dbm.getTables(null, null, "Player", null);
            if (tables.next()) {
                tables = dbm.getTables(null, null, "Game", null);
                if(tables.next())
                {
                    System.out.println("database exist");
                }
                else
                {
                  String  sql = ("Create table Game(id INT PRIMARY KEY,Player1 int,Player2 int,result INT,FOREIGN KEY (Player1) REFERENCES Player(id),FOREIGN KEY (Player2) REFERENCES Player(id))");
                    PreparedStatement  pstmt = connection.prepareStatement(sql);
                    pstmt.executeUpdate();
                }
            }
            else {
                String sql = ("Create table Player(id INT PRIMARY KEY,name varchar(20))");
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();
                sql = ("Create table Game(id INT PRIMARY KEY,Player1 int,Player2 int,result varchar(20),FOREIGN KEY (Player1) REFERENCES Player(id),FOREIGN KEY (Player2) REFERENCES Player(id))");
                pstmt = connection.prepareStatement(sql);
                pstmt.executeUpdate();
            }

            InsertPlayer(connection,"hello12");
           // InsertGame(connection,1,2,-1);

          connection.close();

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
            System.out.println(e);
            return null;
        }
    }

    public static void InsertPlayer(Connection con,String name) throws SQLException {


        String sq2 = ("SELECT id FROM player WHERE name = ?  ");
        //sq2=sq2+"'"+name+"'";
        PreparedStatement st = con.prepareStatement(sq2);
        st.setObject(1,name);

        ResultSet rs = st.executeQuery();

        int i;
        if(rs.next())
       {
           //System.out.println(rs.getInt(1));
           //System.out.println(i);
           System.out.println("objcet exist");
       }
       else {

           String sql = ("INSERT INTO Player (id,name) VALUES(?,?)");
           PreparedStatement pstmt = con.prepareStatement(sql);
           pstmt.setString(2, name);
           Statement st2 = con.createStatement();
            sq2 = ("SELECT max(id) FROM Player");
            rs = st.executeQuery(sq2);
           rs.next();
           i = rs.getInt(1);
           System.out.println(i);
           pstmt.setInt(1, i + 1);
           pstmt.executeUpdate();
       }
    }
    public static void InsertGame(Connection con, String player1,String player2,String result) throws SQLException {
        Statement st = con.createStatement();
        String sq = ("SELECT max(id) FROM game");
        ResultSet rs = st.executeQuery(sq);
        rs.next();
        int max = rs.getInt(1);

        String s1 = ("SELECT id FROM player where name= ?");
        PreparedStatement p1 = con.prepareStatement(s1);
        p1.setString(1,player1);
        ResultSet rs1= p1.executeQuery();
        rs1.next();
        int nr1=rs1.getInt(1);
        String s2 = ("SELECT id FROM player where name= ?");
        PreparedStatement p2 = con.prepareStatement(s1);
        p1.setString(1,player1);
        ResultSet rs2= p1.executeQuery();
        rs2.next();
        int nr2=rs2.getInt(1);
        String sql = ("INSERT INTO game (id,Player1,Player2,result) VALUES(?,?,?,?)");
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1,max+1);
        pstmt.setInt(2,nr1);
        pstmt.setInt(3,nr2);
        pstmt.setString(4,result);
        pstmt.executeUpdate();
    }


}