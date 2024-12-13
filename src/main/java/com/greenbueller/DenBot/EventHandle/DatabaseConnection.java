package com.greenbueller.DenBot.EventHandle;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static String USERID="", PASSWORD="", CONNECTIONSTRING = "";
    static Dotenv dotenv = null;
    public static Connection getConnection() {
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.err.println("Failed to load Dotenv config: " + e.getMessage());
        }

        if (dotenv != null && dotenv.get("USERID") != null) {
            USERID = dotenv.get("USERID");
        } else {
            USERID = System.getenv("USERID");
            if (USERID == null || USERID.isEmpty()) {
                throw new IllegalArgumentException("Database UserID not found in environment variables or .env file");
            }
        }

        if (dotenv != null && dotenv.get("PASSWORD") != null) {
            PASSWORD = dotenv.get("PASSWORD");
        } else {
            PASSWORD = System.getenv("PASSWORD");
            if (PASSWORD == null || PASSWORD.isEmpty()) {
                throw new IllegalArgumentException("Database Password not found in environment variables or .env file");
            }
        }

        if (dotenv != null && dotenv.get("CONNECTIONSTRING") != null) {
            CONNECTIONSTRING = dotenv.get("CONNECTIONSTRING");
        } else {
            CONNECTIONSTRING = System.getenv("CONNECTIONSTRING");
            if (CONNECTIONSTRING == null || CONNECTIONSTRING.isEmpty()) {
                CONNECTIONSTRING = "jdbc:mysql://localhost:3306/reputation?useSSL=false";
            }
        }

        // Establish the Oracle Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Unable to find MySQL Driver. See the following error: " + e);
            return null;
        }

        Connection connection;

        // Attempt to make connection
        try {
            connection = DriverManager.getConnection(CONNECTIONSTRING, USERID, PASSWORD);
        }
        catch (SQLException e) {
            System.out.println("Connection failed. See the following: " + e);
            return null;
        }
        return connection;
    }
}