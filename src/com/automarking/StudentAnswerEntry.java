package com.automarking;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.scoring.ScoreInterface;

/**
 * 
 */
@SuppressWarnings("serial")
public class StudentAnswerEntry extends JFrame implements ActionListener {
	Container cn = null;

	// for transaction
	JLabel lQuestion, lAnswer, lUser;
	JTextArea tAnswer;
	JScrollPane tPane;
	JButton btSubmit, btBack;
	String SID, QID;
	StudentAnswerEntry(String user, String q) {
		cn = getContentPane();
		cn.setLayout(null);

		SID = user;
		QID = q;
		JLabel header = new JLabel("STUDENT ANSWER ENTRY", JLabel.CENTER);
		btSubmit = new JButton("Submit Answer");
		btBack = new JButton("Back");
		header.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));
		tAnswer = new JTextArea("Enter answer to the question as plaintext");
		lQuestion = new JLabel(getQuestion());
		JPanel temp = new JPanel();
		temp.add(lQuestion);
		lUser = new JLabel("User : " + SID, JLabel.CENTER);
		JScrollPane tPane = new JScrollPane(tAnswer), lPane = new JScrollPane(
				temp);

		btSubmit.addActionListener(this);
		btBack.addActionListener(this);

		lQuestion.setBounds(20, 60, 400, 75);
		header.setBounds(50, 20, 325, 30);
		tPane.setBounds(20, 170, 420, 100);
		lPane.setBounds(50, 40, 400, 130);

		temp.setBounds(20, 60, 400, 75);
		lUser.setBounds(360, 20, 100, 30);
		btSubmit.setBounds(60, 300, 150, 30);
		btBack.setBounds(270, 300, 100, 30);

		cn.add(header);
		cn.add(lQuestion);
		cn.add(lUser);
		cn.add(btSubmit);
		cn.add(btBack);
		cn.add(tPane);

		setTitle("Examiner");
		setVisible(true);
		setBounds(400, 150, 500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private String getQuestion() {
		String sql_stmt = "select * from question where qid='" + QID + "'";
		ResultSet rs = query(sql_stmt);

		try {
			rs.next();
			// System.out.println(rs.getString("question"));
			String question = rs.getString("question");

			return question;
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btSubmit) {

			if (tAnswer.getText().trim().equalsIgnoreCase("")
					|| tAnswer.getText().trim().equalsIgnoreCase("\n"))
				JOptionPane.showMessageDialog(null,
						"You must provide an answer to the question chosen");
			else {

				String answer = tAnswer.getText().trim();
				ScoreInterface sc = new ScoreInterface(QID, SID, answer);
				JOptionPane.showMessageDialog(null,
						"Answer entered was submitted for evaluation");
				try {
					sc.scoreEssay();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == btBack) {
			dispose();
			new MainStudentWindow(SID);
		}
	}
	public static void main(String[] args) {
		new StudentAnswerEntry("R8104", "Q001");
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
