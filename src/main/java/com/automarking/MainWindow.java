package com.automarking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	Container cn = null;
	JButton bAdmin, bUser;
	JLabel title;

	public MainWindow() {
		cn = getContentPane();
		cn.setLayout( null );
		title = new JLabel( "ESSAY SCORING SYSTEM", JLabel.CENTER );
		title.setFont( new Font( "Copperplate Gothic Bold", Font.PLAIN, 20 ) );

		bAdmin = new JButton( "Examiner Login" );
		bUser = new JButton( "Student Login" );

		bAdmin.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
						new LoginScreen( 1 );


					}
				}
		);
		bUser.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
						new LoginScreen( 0 );

					}
				}
		);

		title.setBounds( 0, 20, 300, 30 );
		bAdmin.setBounds( 50, 100, 200, 30 );
		bUser.setBounds( 50, 170, 200, 30 );

		cn.add( bAdmin );
		cn.add( bUser );
		cn.add( title );

		setBounds( 450, 200, 300, 300 );
		setVisible( true );
		setResizable( false );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

	}

	public static void main(String[] args) {

		new MainWindow();
	}
}
