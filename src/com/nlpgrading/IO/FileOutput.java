package com.nlpgrading.IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.nlpgrading.evaluation.Score;

public class FileOutput {

	BufferedWriter output;

	public FileOutput(String filepath) {

		try {

			File file = new File(filepath);

			if (file.exists()) {
				file.delete();
			}

			output = new BufferedWriter(new FileWriter(file));

			System.out
					.println("Essay\t1a\t1b\t1c\t1d\t2a\t2b\t3a\tFinalGrade\n");
			output.write("Essay\t1a\t1b\t1c\t1d\t2a\t2b\t3a\tFinalGrade\n");

		} catch (IOException e) {

			System.out.println("[Error] Output file write operation");
		}
	}

	/**
	 * Writes essay scores to CSV output file
	 * 
	 * @param filepath
	 *            path to the CSV output file
	 * @return scores
	 * @author girish
	 */
	public void writeOutput(String name, Score scores) {

		try {

			System.out.println(name + "\t" + scores.getWordOrderScore() + "\t"
					+ scores.getSubjectVerbAgreementScore() + "\t"
					+ scores.getVerbUsageScore() + "\t"
					+ scores.getSentenceFormationScore() + "\t"
					+ scores.getCoherenceScore() + "\t"
					+ scores.getTopicAdherenceScore() + "\t"
					+ scores.getEssayLengthScore() + "\t"
					+ scores.getFinalScore());
			output.write(name + "\t\t" + scores.getWordOrderScore() + "\t"
					+ scores.getSubjectVerbAgreementScore() + "\t"
					+ scores.getVerbUsageScore() + "\t"
					+ scores.getSentenceFormationScore() + "\t"
					+ scores.getCoherenceScore() + "\t"
					+ scores.getTopicAdherenceScore() + "\t"
					+ scores.getEssayLengthScore() + "\t"
					+ scores.getFinalScore() + "\n");

		} catch (IOException e) {

			System.out.println("[Error] Output file write operation");
		}
	}

	public void Finished() {

		try {
			output.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
