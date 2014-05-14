package com.automarking;

import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;

/**
 * 
 */
public class QuestionSelection extends JFrame implements ActionListener {
	/**
 * 
 */
	Container panel = null;
	JLabel title, luser;
	JButton bcOK, bcBack;
	String SID;
	JRadioButton questions[];

	public QuestionSelection(String s) {
		panel = getContentPane();
		panel.setLayout(null);

		title = new JLabel("Question Selection");
		title.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 20));

		SID = s;
		questions = new JRadioButton[10];
		ButtonGroup buttonGroup = new ButtonGroup();
		questions[0] = new JRadioButton(
				"Write an autobiography of yourself, describing your parents, hometown, famiy and school");
		questions[1] = new JRadioButton(
				"Write a letter to your local newspaper in which you state your opinion on the effects computers have on people. Persuade the readers to agree with you.");
		questions[2] = new JRadioButton(
				"Write a persuasive essay to a newspaper reflecting your vies on censorship in libraries.");
		questions[3] = new JRadioButton(
				"Write an essay based on the paragraph given (1)");
		questions[4] = new JRadioButton(
				"Write an essay based on the paragraph given (2)");
		questions[5] = new JRadioButton(
				"Write an essay based on the paragraph given (3)");
		questions[6] = new JRadioButton(
				"Write an essay based on the paragraph given (4)");
		questions[7] = new JRadioButton(
				"Write an essay on the topic : Patience");
		questions[8] = new JRadioButton(
				"Write an essay on the topic : Laughter");
		questions[9] = new JRadioButton(
				"Starting with mRNA leaving the nucleus, list and describe four major steps involved in protein synthesis.");

		bcOK = new JButton("OK");
		bcBack = new JButton("Back");

		questions[0].setBounds(10, 50, 700, 20);
		questions[1].setBounds(10, 90, 700, 20);
		questions[2].setBounds(10, 130, 700, 20);
		questions[3].setBounds(10, 170, 700, 20);
		questions[4].setBounds(10, 210, 700, 20);
		questions[5].setBounds(10, 250, 700, 20);
		questions[6].setBounds(10, 290, 700, 20);
		questions[7].setBounds(10, 330, 700, 20);
		questions[8].setBounds(10, 370, 700, 20);
		questions[9].setBounds(10, 410, 700, 20);

		buttonGroup.add(questions[0]);
		buttonGroup.add(questions[1]);
		buttonGroup.add(questions[2]);
		buttonGroup.add(questions[3]);
		buttonGroup.add(questions[4]);
		buttonGroup.add(questions[5]);
		buttonGroup.add(questions[6]);
		buttonGroup.add(questions[7]);
		buttonGroup.add(questions[8]);
		buttonGroup.add(questions[9]);

		JPanel pan = new JPanel();
		pan.setLayout(null);

		title.setBounds(200, 30, 300, 60);
		bcOK.setBounds(200, 460, 75, 30);
		bcBack.setBounds(300, 460, 75, 30);

		pan.add(questions[0]);
		pan.add(questions[1]);
		pan.add(questions[2]);
		pan.add(questions[3]);
		pan.add(questions[4]);
		pan.add(questions[5]);
		pan.add(questions[6]);
		pan.add(questions[7]);
		pan.add(questions[8]);
		pan.add(questions[9]);

		panel.add(title);
		panel.add(pan);
		panel.add(bcOK);
		panel.add(bcBack);

		pan.setBounds(30, 60, 600, 400);
		bcOK.addActionListener(this);
		bcBack.addActionListener(this);
		setBounds(200, 20, 700, 550);
		setVisible(true);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	int getSelectedRadioButton(JRadioButton items[]) {
		for (int i = 0; i < items.length; i++) {
			if (items[i].isSelected())
				return i;
		}
		return 0;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bcOK) {
			int selectedItem = getSelectedRadioButton(questions);
			String QID = "Q";
			switch (selectedItem) {
				case 1 :
					QID = QID + "002";
					break;
				case 2 :
					QID = QID + "003";
					break;
				case 3 :
					QID = QID + "004";
					break;
				case 4 :
					QID = QID + "005";
					break;
				case 5 :
					QID = QID + "006";
					break;
				case 6 :
					QID = QID + "007";
					break;
				case 7 :
					QID = QID + "008";
					break;
				case 8 :
					QID = QID + "009";
					break;
				case 9 :
					QID = QID + "010";
					break;
				case 0 :
					QID = QID + "001";
					break;

				default :
					QID = QID + "001";
					break;
			}
			dispose();
			new StudentAnswerEntry(SID, QID);

		} else if (e.getSource() == bcBack) {
			dispose();
			new MainStudentWindow(SID);
		}
	}
	public static void main(String[] args) {
		new QuestionSelection("R8104");
	}
}
