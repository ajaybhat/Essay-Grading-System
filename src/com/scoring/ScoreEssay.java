package com.scoring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;

import com.automarking.classifier.FastRandomClassifier;
import com.nlpgrading.NLPGrader;
import com.nlpgrading.evaluation.Score;

/**
 * Scores the essay
 */
public class ScoreEssay {
	private HashMap<String, Integer> score;
	private String essay, QID, SID;
	private Score NLPScore;
	public ScoreEssay(String Q, String S) {
		QID = Q;
		SID = S;

	}

	public void setNLPScore(Score score) {
		NLPScore = score;
	}

	public void setEssay(String es) {
		essay = es;

	}

	public HashMap<String, Integer> getScore() {
		return score;
	}

	private void scoreNLP() {
		setNLPScore(new NLPGrader(essay, QID).gradeNLP());
		score = new HashMap<String, Integer>();
		score.put("1a", NLPScore.getWordOrderScore());
		score.put("1b", NLPScore.getSubjectVerbAgreementScore());
		score.put("1c", NLPScore.getVerbUsageScore());
		score.put("1d", NLPScore.getSentenceFormationScore());
		score.put("2a", NLPScore.getCoherenceScore());
		score.put("2b", NLPScore.getTopicAdherenceScore());
		score.put("3", NLPScore.getEssayLengthScore());
	}
	
	private void scoreClassifier() {
		int classifierScore = FastRandomClassifier.test(QID, essay);
		score.put("Classifier", classifierScore);
	}
	
	public void scoreEssay() throws SQLException {
		double finalScore;
		scoreNLP();
		scoreClassifier();
		if (score.get("1c") < 2)
			finalScore = 0.5;
		else if (score.get("2b") == 0)
			finalScore = 0;
		else if (score.get("3") == 0)
			finalScore = 0;
		else if (essay.length() < 100)
			finalScore = 0.5;
		else {
			finalScore = score.get("1a") + score.get("1b") + score.get("1c")
					+ 2 * score.get("1d") + score.get("2a") + 3
					* score.get("2b") + score.get("3");
			finalScore /= 10;
		}
		finalScore = (2 * finalScore + 3 * score.get("Classifier")) / 5;
		ResultSet rs;
		String sql_stmt, AID = "A" + Calendar.getInstance().get(Calendar.HOUR)
				+ Calendar.getInstance().get(Calendar.MINUTE)
				+ Calendar.getInstance().get(Calendar.SECOND);
		sql_stmt = "insert into answer values('" + AID + "','" + QID + "','"
				+ SID + "')";
		rs = query(sql_stmt);
		rs.next();
		sql_stmt = "insert into mark values('" + AID + "'," + score.get("1a")
				+ "," + score.get("1b") + "," + score.get("1c") + ","
				+ score.get("1d") + "," + score.get("2a") + ","
				+ score.get("2b") + "," + score.get("3") + ","
				+ score.get("Classifier") + "," + Math.ceil(finalScore) + ")";
		rs = query(sql_stmt);
		rs.next();
		StringBuffer strBuffer = new StringBuffer(essay);
		for (int i = 0; i < strBuffer.length(); i++) {
			if (strBuffer.charAt(i) == '\'')
				strBuffer.deleteCharAt(i);
		}
		essay = strBuffer.toString();
		if (essay.length() > 4000)
			essay = essay.substring(4000);
		sql_stmt = "insert into answertext values('" + AID + "','" + essay
				+ "'," + (int) finalScore + ")";
		rs = query(sql_stmt);
		rs.next();
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
