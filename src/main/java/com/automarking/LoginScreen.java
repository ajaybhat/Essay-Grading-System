package com.automarking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("serial")
public class LoginScreen extends JFrame {
	Container cn = null;
	JLabel lus, lpwd;
	JButton ok, cancel;
	JTextField username;
	JPasswordField password;
	JLabel title;

	public LoginScreen(final int toggle) {
		cn = getContentPane();
		cn.setLayout( null );
		lus = new JLabel();
		lpwd = new JLabel( "Password" );
		username = new JTextField( "" );
		password = new JPasswordField( "" );
		ok = new JButton( "OK" );
		cancel = new JButton( "Cancel" );
		if ( toggle == 1 ) {

			title = new JLabel( "Examiner Login", JLabel.CENTER );
			lus.setText( "Username" );
		}
		else {

			title = new JLabel( "Student Login", JLabel.CENTER );
			lus.setText( "Student ID" );
		}
		title.setFont( new Font( "Copperplate Gothic Bold", Font.PLAIN, 20 ) );

		ok.addActionListener(
				new ActionListener() {

					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent e) {
						ResultSet rs = null;
						String user = username.getText();
						if ( user.equalsIgnoreCase( "Admin" )
								&& password.getText().equalsIgnoreCase( "Admin" ) ) {
							dispose();
							new MainAdminWindow( user );

						}
						else {
							JOptionPane.showMessageDialog(
									null,
									"Wrong password entered or User doesn't exist", "",
									JOptionPane.ERROR_MESSAGE
							);
						}
						try {
							if ( toggle == 1 ) {
								rs = query(
										"select * from examiner where username='"
												+ user + "'"
								);
							}
							else {
								rs = query( "select * from student where sid='" + user + "'" );
							}

							rs.next();
							String pass = rs.getString( "password" );
							if ( pass.equals( password.getText() ) ) {
								dispose();
								if ( toggle == 1 ) {
									new MainAdminWindow( user );
								}
								else {
									new MainStudentWindow( user );
								}
							}

						}
						catch (Exception e1) {
							System.out.println( "SQL Connection problem" );
							e1.printStackTrace();
						}

					}
				}
		);

		cancel.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						dispose();
						new MainWindow();

					}
				}
		);

		title.setBounds( 0, 0, 400, 50 );
		lus.setBounds( 75, 70, 100, 30 );
		lpwd.setBounds( 75, 110, 100, 30 );
		username.setBounds( 160, 70, 200, 30 );
		password.setBounds( 160, 110, 200, 30 );
		ok.setBounds( 120, 160, 75, 30 );
		cancel.setBounds( 215, 160, 75, 30 );

		cn.add( lus );
		cn.add( lpwd );
		cn.add( username );
		cn.add( password );
		cn.add( title );
		cn.add( ok );
		cn.add( cancel );

		setBounds( 400, 200, 400, 250 );
		setVisible( true );
		setResizable( false );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

	}

	private ResultSet query(String string) {

		Connection con = null;
		Statement st;
		ResultSet r = null;
		try {
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName( driverName );
			con = DriverManager.getConnection(
					"jdbc:oracle:thin:@127.0.0.1:1521:XE", "jadagrp", "password"
			);
			st = con.createStatement();
			r = st.executeQuery( string );
		}
		catch (Exception e) {

			e.printStackTrace();
		}

		return r;
	}

}