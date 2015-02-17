package com.automarking.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    public ResultSet dbquery(String s) {

        Connection con = null;
        ResultSet rs = null;
        try {
            String driverName = "oracle.jdbc.driver.OracleDriver";
            Class.forName(driverName);

            String serverName = "127.0.0.1";
            String portNumber = "1521";
            String sid = "XE";
            String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber
                    + ":" + sid;
            String username = "ajay";
            String password = "password";
            con = DriverManager.getConnection(url, username, password);

            Statement st = con.createStatement();
            rs = st.executeQuery(s);
            st.close();

        } catch (ClassNotFoundException e) {
            System.out.println("class not found" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL connection problem" + e.getMessage());
        }
        return rs;
    }

}
