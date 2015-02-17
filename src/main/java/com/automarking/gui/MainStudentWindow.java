package com.automarking.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 */
public class MainStudentWindow extends JFrame implements ActionListener {
    Container cn = null;
    JLabel title, luser;
    JButton btakeTest, bviewAnswers, bviewProfile, bchangePassword, blogOut;
    String SID;

    /**
     *
     */
    public MainStudentWindow(String sID) {
        dispose();
        cn = getContentPane();
        cn.setLayout(null);

        SID = sID;
        title = new JLabel("Student Login", JLabel.CENTER);
        luser = new JLabel("Student : " + SID, JLabel.RIGHT);
        title.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));
        btakeTest = new JButton("Take Test");
        bviewProfile = new JButton("View student profile");
        bviewAnswers = new JButton("View submitted answers");
        bchangePassword = new JButton("Change password");
        blogOut = new JButton("Logout");

        title.setBounds(40, 30, 200, 60);
        luser.setBounds(250, 50, 100, 20);
        btakeTest.setBounds(50, 100, 300, 30);
        bviewProfile.setBounds(50, 150, 300, 30);
        bviewAnswers.setBounds(50, 200, 300, 30);
        bchangePassword.setBounds(50, 300, 150, 30);
        blogOut.setBounds(200, 300, 150, 30);

        bviewAnswers.addActionListener(this);
        btakeTest.addActionListener(this);
        bchangePassword.addActionListener(this);
        blogOut.addActionListener(this);

        cn.add(title);
        cn.add(luser);
        cn.add(btakeTest);
        cn.add(bviewProfile);
        cn.add(bviewAnswers);
        cn.add(blogOut);
        cn.add(bchangePassword);

        setBounds(300, 100, 400, 400);
        setVisible(true);
        setResizable(false);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == bviewAnswers) {

        } else if (e.getSource() == btakeTest) {
            new QuestionSelection(SID);
            dispose();

        } else if (e.getSource() == bchangePassword) {
            JPanel panel = new JPanel();
            JPasswordField oldpass = new JPasswordField(10);
            panel.add(new JLabel("Enter current password"));
            panel.add(oldpass);
            JOptionPane.showConfirmDialog(
                    null, panel, "",
                    JOptionPane.PLAIN_MESSAGE
            );

            panel.removeAll();
            ResultSet rs = null;
            if (oldpass.getText().trim().length() == 0
                    || oldpass.getText() == null) {
                ;
            } else {

                try {
                    rs = query(
                            "select password from student where sid='" + SID
                                    + "'"
                    );
                    rs.next();
                    System.out.println(rs.getString("password"));
                    String password = rs.getString("password");
                    password.equals(oldpass.getText());
                    if (password.equals(oldpass.getText())) {
                        JPasswordField jt1 = new JPasswordField(10), jt2 = new JPasswordField(
                                10
                        );

                        panel.add(new JLabel("Enter new password"));
                        panel.add(jt1);

                        panel.add(new JLabel("Re-enter new password"));
                        panel.add(jt2);
                        JOptionPane.showConfirmDialog(
                                null, panel, "",
                                JOptionPane.PLAIN_MESSAGE
                        );
                        if (jt1.getText().equals(jt2.getText())) {
                            rs = query(
                                    "update student set password = '"
                                            + jt1.getText() + "' where sid='" + SID
                                            + "'"
                            );
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Password updated", "",
                                    JOptionPane.PLAIN_MESSAGE
                            );

                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Passwords don't match"
                            );
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Wrong password entered"
                        );
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        } else if (e.getSource() == blogOut) {
            dispose();
            new MainWindow();

        }
    }

    private ResultSet query(String string) {

        Connection con = null;
        Statement st;
        ResultSet r = null;
        try {
            String driverName = "oracle.jdbc.driver.OracleDriver";
            Class.forName(driverName);
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@127.0.0.1:1521:XE", "jadagrp",
                    "password"
            );
            st = con.createStatement();
            r = st.executeQuery(string);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return r;

    }
}
