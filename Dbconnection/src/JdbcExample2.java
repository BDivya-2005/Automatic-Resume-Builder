package com.example;

import java.sql.*;

public class JdbcExample2 {
    public static void main(String[] args) {
        try {

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/test", "root", "Divya@12");

            // SQL query to insert a record
            String query = "INSERT INTO students(id, name, age,course) VALUES(4, 'ardhaya', 23,'j')";


            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);

            System.out.println("Data Inserted!");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
