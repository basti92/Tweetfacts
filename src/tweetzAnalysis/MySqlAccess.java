package tweetzAnalysis;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.apache.commons.dbutils.DbUtils;
import twitter4j.Status;

/**
 * Created by Sebastian Ruf on 28.03.14.
 */


// BEISPIEL CODE

public class MySqlAccess {

    private static Connection conn = null;
    private static Statement stmt = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public static void connectToDatabase() {
        try {

        //DB-Treiber laden (optional)
        Class.forName("com.mysql.jdbc.Driver");

        //DB-Verbindungseinstellungen
        final String dbUrl = "jdbc:mysql://localhost/";
        final String dbUsername = "root";
        final String dbPassword = "9mySQLsDR#";

            //Datenbankverbindung aufbauen
        conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            //SQL-Befehlobjekt erstellen
        stmt = conn.createStatement();


            //System.out.println("Creating database...");


            //String sql = "CREATE DATABASE IF NOT EXISTS test";
            //stmt.executeUpdate(sql);

            //stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Test.Personen (ID INT NOT NULL, Vorname VARCHAR(45) NOT NULL," +
            //       " Name VARCHAR(45) NOT NULL, Geburtstag DATE NULL, PRIMARY KEY (ID))");


            //stmt.executeUpdate("INSERT INTO Tweets.tweet (idTweet, Date, Message, Language, Author, Follower, Location, Query) VALUES (5, '1991-05-28', 'tweetmessage', 'en', 'Ich', 500, 'Klafu', 'no')");
            //stmt.executeUpdate("INSERT INTO Test.Personen (ID, Vorname, Name, Geburtstag) VALUES (5, 'Martin', 'Maier', '1991-05-28')");
            //insertPerson(stmt, 2, "Peter","Meier", "1991-05-28");
        }
        catch(SQLException ex){
            ex.getErrorCode();
            ex.getSQLState();
            System.out.println("FehlerSQL-Connection");
        }
        catch (Exception ex) {
            System.out.println("Fehler :(");
            ex.printStackTrace();

        }

    }

    public static void saveTweets(long id, java.sql.Date date, String message, String lang, String user,
                                  int follower, String location, String query){

       try{
            if (lang == null)lang=" ";
            if (location == null)location=" ";
           //if ()



           stmt.executeUpdate("INSERT INTO Tweets.tweet (idTweet, Date, Message, Language, Author, " +
                   "Follower, Location, Query) VALUES ("+id+", '"+date+"', '"+message+"', '"+lang+"', '"+user+"', "+follower+", '"+location+"', '"+query+"')");



          }
        catch(SQLException ex){
            ex.getErrorCode();
            ex.getSQLState();
            System.out.println("FehlerSQL-SaveTweets");
        }
        catch (Exception ex) {
            System.out.println("Fehler :(");
            ex.printStackTrace();

        }
    }


/*    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void readDataBase() throws Exception {
        try {
            // this will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // setup the connection with the DB.
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/tweets?"
                            + "user=sqluser&password=9mySQLsDR#");

            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            resultSet = statement
                    .executeQuery("");
            writeResultSet(resultSet);

            // preparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  FEEDBACK.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
            // "myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
            // parameters start with 1
            preparedStatement.setString(1, "Test");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestWebpage");
            preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
            preparedStatement.setString(5, "TestSummary");
            preparedStatement.setString(6, "TestComment");
            preparedStatement.executeUpdate();

            preparedStatement = connect
                    .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

            // remove again the insert comment
            preparedStatement = connect
                    .prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();

            resultSet = statement
                    .executeQuery("select * from FEEDBACK.COMMENTS");
            writeMetaData(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        // now get some metadata from the database
        System.out.println("The columns in the table are: ");
        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // resultSet is initialised before the first data set
        while (resultSet.next()) {
            // it is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g., resultSet.getSTring(2);
            String user = resultSet.getString("myuser");
            String website = resultSet.getString("webpage");
            String summary = resultSet.getString("summary");
            Date date = resultSet.getDate("datum");
            String comment = resultSet.getString("comments");
            System.out.println("User: " + user);
            System.out.println("Website: " + website);
            System.out.println("Summary: " + summary);
            System.out.println("Date: " + date);
            System.out.println("Comment: " + comment);
        }*/


    // you need to close all three to make sure
    public static void close() {
    DbUtils.closeQuietly(conn, stmt, resultSet);
    }


//    private void close(Closeable c) {
//        try {
//            if (c != null) {
//                c.close();
//            }
//        } catch (Exception e) {
//            // don't throw now as it might leave following closables in undefined state
//        }
//    }
}
