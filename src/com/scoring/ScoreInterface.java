package com.scoring;

import java.sql.SQLException;

/**
 * Interface for getting answers submitted and scoring
 */
public class ScoreInterface {

	static String QID, SID, essay;;
	public ScoreInterface(String q, String s, String es) {
		QID = q;
		SID = s;
		essay = es;
	}
	public static void scoreEssay() throws SQLException {
		ScoreEssay sc = new ScoreEssay(QID, SID);
		sc.setEssay(essay);
		sc.scoreEssay();
	}
}
