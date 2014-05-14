package com.automarking.classifier;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.swabunga.spell.event.SpellChecker;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * 
 */
public class ArffGenerator {

	static String arffLine, QID;

	public static void main(String[] args) throws Exception {

		QID = "Q004";
		// Log Message
		System.out.println("Preparing training dataset...");
		prepareDataset(System.getProperty("user.dir")
				+ "/data/Answer_datasets/" + QID + ".tsv", QID);
		System.out.println("Done");

		// Log Message
		System.out.println("Generating ARFF file...");
		generateARFF(QID);
		System.out.println("Done");

	}

	static String getFeatures(String dataSet, String qID) {

		// Initializing StanfordCoreNLP
		StanfordCoreNLP pipe = Features.stanfordCoreNLPInit();

		// Initializing the spell check dictionary
		SpellChecker spellChecker;
		try {
			spellChecker = Features.jazzyInit();

			// Splitting each row of the dataset
			String tokens[] = (dataSet.trim()).split("\t");

			LinkedHashMap posFeature = new LinkedHashMap<>();
			String misspelled, word_count, char_count = new String(), difficult, avg_syllable_word, avg_syllable_sentence, readability, bigramTypeTokenRatio, nUnexpectedBigrams, nDifferentBiGrams, trigramTypeTokenRatio, nUnexpectedTrigrams, nDifferentTriGrams, positiveMeasure, negativeMeasure;

			// Feature = character_count
			char_count = Integer.toString(tokens[1].length());

			// Feature = word_count
			word_count = Integer
					.toString(Sentences.getNumberOfWords(tokens[1]));
			// Feature = misspelled
			misspelled = Integer.toString(Features.misspelled(tokens[1],
					spellChecker));
			// Feature = syllables per word
			avg_syllable_word = Double.toString(Sentences
					.avgSyllablesPerWord(tokens[1]));

			// Feature = syllables per sentence
			avg_syllable_sentence = Double.toString(Sentences
					.avgSyllablesPerSentence(tokens[1]));

			// Feature = difficult words
			difficult = Integer.toString(Sentences
					.getNumberOfDifficultWords(tokens[1]));

			// Feature = readabililty
			readability = Double.toString(Sentences
					.calculateReadability(tokens[1]));

			// Feature = bigram type to token ratio
			bigramTypeTokenRatio = Double.toString(Sentences
					.computeBigramTypeTokenRatio(tokens[1]));

			// Feature = n unexpected bigrams
			nUnexpectedBigrams = Double.toString(Sentences
					.computeNUnexpectedBigrams(tokens[1]));

			// Feature = n different bigrams
			nDifferentBiGrams = Double.toString(Sentences
					.computeNDifferentBigrams(tokens[1]));

			// Feature = trigram type to token ratio
			trigramTypeTokenRatio = Double.toString(Sentences
					.computeTrigramTypeTokenRatio(tokens[1]));

			// Feature = n unexpected trigrams
			nUnexpectedTrigrams = Double.toString(Sentences
					.computeNUnexpectedTrigrams(tokens[1]));

			// Feature = n different trigrams
			nDifferentTriGrams = Double.toString(Sentences
					.computeNDifferentTrigrams(tokens[1]));

			KeywordMatcher matcher = new KeywordMatcher(qID, tokens[1]);
			positiveMeasure = Double.toString(matcher.getpositiveMeasure());
			negativeMeasure = Double.toString(matcher.getnegativeMeasure());

			// Features = sentence_count, sent_mean_len and 8 pos features
			// combined in one
			posFeature = Features.posFeatures(tokens[1], pipe);
			Iterator itr = posFeature.keySet().iterator();
			String pos = "";
			while (itr.hasNext()) {
				Object key = itr.next();
				pos = pos + posFeature.get(key).toString() + ",";
			}
			pos = pos.substring(0, pos.length() - 1);

			// Feature = score
			String score;
			if (tokens.length > 2) {
				// If the dataset is training data
				String score1 = tokens[2], score2 = tokens[3];
				score = Integer
						.toString((int) ((Double.parseDouble(score1) + Double
								.parseDouble(score2)) / 2));
			} else
				score = "0";
			// Writing a data point to the file
			arffLine = char_count + "," + word_count + "," + misspelled + ","
					+ avg_syllable_word + "," + avg_syllable_sentence + ","
					+ difficult + "," + readability + ","
					+ bigramTypeTokenRatio + "," + nDifferentBiGrams + ","
					+ nUnexpectedBigrams + "," + trigramTypeTokenRatio + ","
					+ nDifferentTriGrams + "," + nUnexpectedTrigrams + ","
					+ positiveMeasure + "," + negativeMeasure + "," + pos + ","
					+ score + "\n";
			return arffLine;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Converting the raw datasets into features and storing them in ARFF files.
	 * See Features class for the description of the features.
	 */
	@SuppressWarnings("rawtypes")
	public static void prepareDataset(String filename, String type)
			throws IOException {

		// Reading the dataset
		BufferedReader file = new BufferedReader(new FileReader(filename));

		// Creating the data file and writing the description to it
		BufferedWriter data = new BufferedWriter(
				new FileWriter(System.getProperty("user.dir") + "/data/Data/"
						+ type + ".data"));

		// Reading the dataset and generating features
		String line2 = new String();
		String newLine = new String();
		int count = 0;
		System.out.println();
		while ((line2 = file.readLine()) != null) {

			// Ignoring the first line of the dataset
			if (line2.startsWith("Id")) {
				continue;
			}

			// Log Message
			count++;
			System.out.println("Generating features for Essay #" + count);

			newLine = getFeatures(line2, QID);
			data.write(newLine);

		}

		// Closing all files
		file.close();
		data.close();
	}
	/*
	 * Generate ARFF files
	 */
	@SuppressWarnings("resource")
	public static void generateARFF(String type) throws IOException {
		// Reading ARFF file description from header.txt file
		BufferedReader header = new BufferedReader(new FileReader(
				System.getProperty("user.dir") + "/data/Arffs/header.arff"));
		String line1 = new String();
		String desp = new String();
		while ((line1 = header.readLine()) != null) {
			desp = desp + line1 + "\n";
		}
		header.close();

		BufferedWriter arff = new BufferedWriter(new FileWriter(
				System.getProperty("user.dir") + "/data/Arffs/" + type
						+ ".arff"));
		arff.write(desp);

		BufferedReader data = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "/data/Data/"
						+ type + ".data"));
		String line2 = new String();
		while ((line2 = data.readLine()) != null)
			arff.write(line2 + "\n");

		arff.close();
	}

}
