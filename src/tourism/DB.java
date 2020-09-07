package tourism;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
    private Connection conn;
    public DB(){
        conn = null;
    }
    
    public void connect(){
        try {
            // db parameters
            String url = "jdbc:sqlite:./mydb.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            //System.out.println("Connection to SQLite has been established.");

            //if first time, create the table
            String cmd = "CREATE TABLE IF NOT EXISTS stats ( "+
            "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "timestamp TEXT,"+
            "website TEXT," +
            "place TEXT,"+
            "checkin TEXT,"+
            "checkout TEXT,"+            
            "hotels INTEGER,"+
            "price REAL,"+
            "rating REAL,"+
            "reviews REAL"+            
            ")";

            Statement stmt  = conn.createStatement();
            stmt.executeUpdate(cmd);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
    }
    
    public void insert_stats(String website, String place, String checkin, String checkout, int hotels, double price, double rating, double reviews){
        if(conn==null){
            this.connect();
        }
        
        String sql = "INSERT INTO stats(timestamp, website, place, checkin, checkout, hotels, price, rating, reviews) VALUES(datetime('now'),?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, website);
            pstmt.setString(2, place);
            pstmt.setString(3, checkin);
            pstmt.setString(4, checkout);
            pstmt.setInt(5, hotels);
            pstmt.setDouble(6, price);
            pstmt.setDouble(7, rating);
            pstmt.setDouble(8, reviews);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void get_stats(String place, String checkin, String checkout){
        String sql = "SELECT SUM(hotels) AS hotels_s, AVG(price) AS price_a, AVG(rating) AS rating_a, AVG(reviews) AS reviews_a FROM stats WHERE place='" + place + "' AND checkin='" + checkin + "' AND checkout='" + checkout + "'";
        
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("hotels \tAvg Price \t Avg Rating \t Avg Reviews");
            while (rs.next()) {
                System.out.println(rs.getInt("hotels_s") +  "\t" + 
                                   rs.getDouble("price_a") + "\t" +
                                   rs.getDouble("rating_a") + "\t" + 
                                   rs.getDouble("reviews_a")
                                    );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void close(){
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
