package com.nlpgrading;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.nlpgrading.evaluation.Score;
import com.nlpgrading.grader.AutoGrader;
import com.nlpgrading.grader.Essay;

public class NLPGrader {

	String essayRaw, QID;

	/**
	 * Default constructor,takes essay as argument
	 */
	public NLPGrader(String text, String q) {
		essayRaw = text;
		QID = q;
	}

	/**
	 * Grades the essay for each NLP measure
	 *
	 * @return the NLP graded score
	 */
	public Score gradeNLP() {

		Essay essay;
		AutoGrader grader;

		essay = new Essay();
		grader = new AutoGrader( QID );
		ArrayList<String> essayText = new ArrayList<>();

		StringTokenizer tokenizer = new StringTokenizer( essayRaw, "." );
		while ( tokenizer.hasMoreTokens() ) {
			String temp = tokenizer.nextToken() + ".";
			essayText.add( temp );
		}

		essay.setSentences( essayText );
		for ( int i = 0; i < essay.getSentences().size(); i++ ) {
			String line = essay.getSentences().get( i );
			if ( !line.equals( "" ) ) {
				essay.addPosTag( grader.getStanfordPosTags( line ) );
			}

		}

		grader.gradeEssayLength( essay );// 3
		grader.segmentEssay( essay );

		for ( int i = 0; i < essay.getDetectedSentences().size(); i++ ) {
			String text = essay.getDetectedSentences().get( i );
			if ( !text.equals( "" ) ) {
				essay.addParsedSentence( grader.getParseTree( text ) );
			}
		}

		grader.gradeSyntax( essay );// 1a,1b,1c,1d
		grader.gradeTopicCoherence( essay );// 2b
		grader.gradeTextCoherence( essay );// 2a

		return essay.getEssayScore();
	}
}
