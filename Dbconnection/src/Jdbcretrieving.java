package com.example;
import java.sql.*;
public class Jdbcretrieving {


    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test", "root", "Divya@12");

            String query = "SELECT * FROM students";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " " +
                        rs.getString("name") + " " +
                        rs.getInt("age"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
