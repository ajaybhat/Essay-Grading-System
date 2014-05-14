package com.automarking;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * 
 */
public class MainAdminWindow extends JFrame implements ActionListener {
	Container cn = null;
	JLabel title, luser;
	JButton bcreateQuestion, bcreateStudent, bviewQuestions, bviewAllAnswers,
			bviewStudentAnswers, bviewProfile, bchangePassword, blogOut;
	String EID;
	/**
 * 
 */
	public MainAdminWindow(String eID) {
		cn = getContentPane();
		cn.setLayout(null);
		EID = eID;
		title = new JLabel("Examiner Login", JLabel.CENTER);
		luser = new JLabel("Examiner : " + EID, JLabel.RIGHT);
		title.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));

		bcreateQuestion = new JButton("Create new question");
		bcreateStudent = new JButton("Create new student");
		bviewProfile = new JButton("View student profile");
		bviewQuestions = new JButton("View all questions");
		bviewAllAnswers = new JButton("View all submitted answers");
		bviewStudentAnswers = new JButton("View specific submitted answers");
		bchangePassword = new JButton("Change password");
		blogOut = new JButton("Logout");

		title.setBounds(50, 15, 200, 60);
		luser.setBounds(250, 30, 100, 30);
		bcreateStudent.setBounds(50, 100, 300, 30);
		bviewProfile.setBounds(50, 150, 300, 30);
		bviewQuestions.setBounds(50, 200, 300, 30);
		bviewAllAnswers.setBounds(50, 250, 300, 30);
		bviewStudentAnswers.setBounds(50, 300, 300, 30);
		bchangePassword.setBounds(50, 350, 150, 30);
		blogOut.setBounds(200, 350, 150, 30);

		bcreateQuestion.addActionListener(this);
		bcreateStudent.addActionListener(this);
		bviewAllAnswers.addActionListener(this);
		bviewQuestions.addActionListener(this);
		bviewProfile.addActionListener(this);
		bviewStudentAnswers.addActionListener(this);
		bchangePassword.addActionListener(this);
		blogOut.addActionListener(this);

		cn.add(title);
		cn.add(luser);
		cn.add(bcreateQuestion);
		cn.add(bcreateStudent);
		cn.add(bviewProfile);
		cn.add(bviewStudentAnswers);
		cn.add(bviewQuestions);
		cn.add(bviewAllAnswers);
		cn.add(blogOut);
		cn.add(bchangePassword);

		setBounds(300, 70, 400, 450);
		setVisible(true);
		setResizable(false);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bviewAllAnswers) {

		} else if (e.getSource() == bcreateStudent) {
			dispose();
			new NewStudent();
		} else if (e.getSource() == bchangePassword) {
			JPanel panel = new JPanel();
			JPasswordField oldpass = new JPasswordField(10);
			panel.add(new JLabel("Enter current password"));
			panel.add(oldpass);
			JOptionPane.showConfirmDialog(null, panel, "",
					JOptionPane.PLAIN_MESSAGE);

			panel.removeAll();
			ResultSet rs = null;
			if (oldpass.getText().trim().length() == 0
					|| oldpass.getText() == null)
				;
			else {

				try {
					rs = query("select password from examiner where user='"
							+ EID + "'");
					rs.next();
					System.out.println(rs.getString("password"));
					String password = rs.getString("password");
					password.equals(oldpass.getText());
					if (password.equals(oldpass.getText())) {
						JPasswordField jt1 = new JPasswordField(10), jt2 = new JPasswordField(
								10);

						panel.add(new JLabel("Enter new password"));
						panel.add(jt1);

						panel.add(new JLabel("Re-enter new password"));
						panel.add(jt2);
						JOptionPane.showConfirmDialog(null, panel, "",
								JOptionPane.PLAIN_MESSAGE);
						if (jt1.getText().equals(jt2.getText())) {
							rs = query("update examiner set password = '"
									+ jt1.getText() + "' where username='"
									+ EID + "'");
							JOptionPane.showMessageDialog(null,
									"Password updated", "",
									JOptionPane.PLAIN_MESSAGE);

						} else {
							JOptionPane.showMessageDialog(null,
									"Passwords don't match");
						}
					} else
						JOptionPane.showMessageDialog(null,
								"Wrong password entered");
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
					"password");
			st = con.createStatement();
			r = st.executeQuery(string);
		} catch (Exception e) {

			e.printStackTrace();
		}

		return r;

	}

}
