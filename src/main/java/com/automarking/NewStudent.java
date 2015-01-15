package com.automarking;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 */
public class NewStudent extends JFrame implements ActionListener {
	JLabel header, lAcc, lName, lAdd, lPhno, lPass, lDOB;
	@SuppressWarnings("rawtypes")
	JComboBox cDD, cMM, cYY;
	JButton bOK, bReset, bBack;
	JTextField tAcc, tName, tAdd, tPhno, tPass;
	String AccTypes[] = {
			"Savings Account", "Checking Account",
			"Personal Account"
	};
	String[] dd = new String[31], mm = {
			"Jan", "Feb", "Mar", "Apr", "May",
			"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	},
			yy = new String[23];

	Container cn = null;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public NewStudent() {

		cn = getContentPane();
		cn.setLayout( null );
		for ( int i = 1; i <= 31; i++ ) {
			dd[i - 1] = Integer.toString( i );
		}
		for ( int i = 1990; i <= 2012; i++ ) {
			yy[i - 1990] = Integer.toString( i );
		}

		header = new JLabel( "NEW STUDENT", JLabel.CENTER );
		lAcc = new JLabel( "Student ID" );
		lName = new JLabel( "Name" );
		lPhno = new JLabel( "Phone No. " );
		lDOB = new JLabel( "Date of Birth" );
		lAdd = new JLabel( "Address" );
		lPass = new JLabel( "Password" );
		cDD = new JComboBox( dd );
		cMM = new JComboBox( mm );
		cYY = new JComboBox( yy );
		bOK = new JButton( "OK" );
		bReset = new JButton( "Reset" );
		bBack = new JButton( "Back" );
		tAcc = new JTextField( "" );
		tName = new JTextField( "" );
		tAdd = new JTextField( "" );
		tPhno = new JTextField( "" );
		tPass = new JTextField( "" );
		header.setFont( new Font( "Copperplate Gothic Bold", Font.PLAIN, 20 ) );

		header.setBounds( 0, 0, 400, 30 );
		lAcc.setBounds( 20, 50, 100, 30 );
		tAcc.setBounds( 130, 50, 200, 30 );
		lName.setBounds( 20, 90, 100, 30 );
		tName.setBounds( 130, 90, 200, 30 );
		lPhno.setBounds( 20, 130, 100, 30 );
		tPhno.setBounds( 130, 130, 200, 30 );
		lDOB.setBounds( 20, 170, 100, 30 );
		cDD.setBounds( 130, 170, 50, 30 );
		cMM.setBounds( 200, 170, 50, 30 );
		cYY.setBounds( 270, 170, 50, 30 );
		lAdd.setBounds( 20, 210, 100, 30 );
		tAdd.setBounds( 130, 210, 200, 30 );
		lPass.setBounds( 20, 250, 100, 30 );
		tPass.setBounds( 130, 250, 200, 30 );

		bOK.setBounds( 50, 310, 80, 30 );
		bReset.setBounds( 150, 310, 80, 30 );
		bBack.setBounds( 250, 310, 80, 30 );

		setBounds( 400, 150, 400, 400 );

		add( header );
		add( lAcc );
		add( tAcc );
		add( lName );
		add( tName );
		add( lDOB );
		add( cDD );
		add( cMM );
		add( cYY );
		add( lAdd );
		add( tAdd );
		add( lPhno );
		add( tPhno );
		add( lPass );
		add( tPass );
		add( bOK );
		add( bReset );
		add( bBack );

		bOK.addActionListener( this );
		bReset.addActionListener( this );
		bBack.addActionListener( this );
		setVisible( true );
		setResizable( false );
		tAcc.requestFocus();
		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		int fa, fp, fb;
		long phno = 0;
		String sid, name, address, date, sql_stmt;
		fa = fp = fb = 1;
		if ( ae.getSource() == bOK ) {
			if ( tName.getText().trim().length() == 0 ) {
				JOptionPane.showMessageDialog( null, "Enter Student Name" );

				tName.setText( "" );
				tName.requestFocus();
			}

			else if ( tAdd.getText().trim().length() == 0 ) {
				JOptionPane.showMessageDialog( null, "Enter Address" );
				tAdd.setText( "" );
				tAdd.requestFocus();
			}

			else if ( tPass.getText().trim().length() == 0 ) {
				JOptionPane.showMessageDialog( null, "Enter a password" );
				tPass.setText( "" );
				tPass.requestFocus();
			}
			else if ( tAcc.getText().trim().length() == 0 ) {
				JOptionPane.showMessageDialog( null, "Enter Student ID" );
				tAcc.setText( "" );
				tAcc.requestFocus();

			}
			if ( tPhno.getText().trim().length() == 0 ) {
				JOptionPane.showMessageDialog( null, "Enter Phone No" );
				tPhno.setText( "" );
				tPhno.requestFocus();
			}
			else {
				try {
					phno = Long.parseLong( tPhno.getText() );
					fp = 1;
				}
				catch (NumberFormatException e) {
					JOptionPane
							.showMessageDialog(
									null,
									"Phone No can have only numeric values. Please try again"
							);
					fp = 0;
					tPhno.setText( "" );
					tPhno.requestFocus();
				}

			}

			if ( fa == 1 && fp == 1 && fb == 1 ) {
				name = tName.getText();
				sid = tAcc.getText();
				address = tAdd.getText();
				date = (String) cDD.getSelectedItem() + "-"
						+ cMM.getSelectedItem().toString().toUpperCase() + "-"
						+ (String) cYY.getSelectedItem();

				String password = tPass.getText();
				DBConnection d = new DBConnection();
				sql_stmt = "insert into student values('" + sid + "','" + password + "')";
				d.dbquery( sql_stmt );
				sql_stmt = "insert into studentprofile values('" + sid + "','"
						+ name + "','" + Long.toString( phno ) + "','" + date
						+ "','" + address + "')";
				d.dbquery( sql_stmt );
				JOptionPane.showMessageDialog(
						null, "Details of \tID : "
								+ sid + "\n\tStudent : " + name + "\nsaved"
				);
				tAcc.setText( "" );
				tName.setText( "" );
				tAdd.setText( "" );
				tAdd.setText( "" );
				tPhno.setText( "" );
				tPass.setText( "" );
				tAcc.requestFocus();
			}
		}

		else if ( ae.getSource() == bReset ) {
			tAcc.setText( "" );
			tName.setText( "" );
			tAdd.setText( "" );
			tPhno.setText( "" );
			tPass.setText( "" );
			tAcc.requestFocus();
		}
		else if ( ae.getSource() == bBack ) {
			dispose();
			new MainAdminWindow( "Alan" );
		}
	}
}
