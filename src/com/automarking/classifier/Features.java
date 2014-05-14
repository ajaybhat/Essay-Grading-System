package com.automarking.classifier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;

import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Features {

	// Initializing buffer for misspelled method
	public static SpellChecker jazzyInit() throws FileNotFoundException,
			IOException {
		SpellDictionaryHashMap dictionary = new SpellDictionaryHashMap(
				new File(System.getProperty("user.dir") + "/lib/english.0"));
		SpellChecker spellChecker = new SpellChecker(dictionary);

		return (spellChecker);
	}

	// Initializing StanfordCoreNLP
	public static StanfordCoreNLP stanfordCoreNLPInit() {
		// Creates a StanfordCoreNLP object, with POS tagging
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		return pipeline;
	}

	// Finding the number of misspelled words in the essay
	public static int misspelled(String str, SpellChecker spellChecker)
			throws FileNotFoundException, IOException {
		StringWordTokenizer wt = new StringWordTokenizer(str);
		int i = spellChecker.checkSpelling(wt);

		return (i);
	}

	// Calculating POS counts
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static LinkedHashMap posFeatures(String text,
			StanfordCoreNLP pipeline) {

		// Create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// Run all Annotators on this text
		pipeline.annotate(document);

		// These are all the sentences in text string
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		// Stores POS features
		LinkedHashMap posCount = new LinkedHashMap();
		posCount.put(".", 0); // Sentence count
		posCount.put("ML", 0); // Mean token length of sentences
		posCount.put("DT", 0); // Determiners
		posCount.put("IN", 0); // Prepositions
		posCount.put("JJ", 0); // Adjectives
		posCount.put("NN", 0); // Nouns
		posCount.put("NNP", 0); // Proper nouns
		posCount.put("PRP", 0); // Pronouns
		posCount.put("VB", 0); // Verbs
		posCount.put("WH", 0); // WH words

		// Stores length of sentences
		DoubleArrayList sentLength = new DoubleArrayList();

		// For very sentence in the text
		for (CoreMap sentence : sentences) {
			// Keeping a count of number of tokens in the sentence
			int count = 0;

			// For every token in the sentence
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// Increment token count
				count++;

				String pos = token.get(PartOfSpeechAnnotation.class);

				// Populating posCount HashMap
				if (pos.equals(".") || pos.equals("!") || pos.equals("?")) {
					posCount.put(".", (Integer) posCount.get(".") + 1);
				} else if (pos.equals("DT")) {
					posCount.put("DT", (Integer) posCount.get("DT") + 1);
				} else if (pos.equals("IN")) {
					posCount.put("IN", (Integer) posCount.get("IN") + 1);
				} else if (pos.equals("JJ") || pos.equals("JJR")
						|| pos.equals("JJS")) {
					posCount.put("JJ", (Integer) posCount.get("JJ") + 1);
				} else if (pos.equals("NN") || pos.equals("NNS")) {
					posCount.put("NN", (Integer) posCount.get("NN") + 1);
				} else if (pos.equals("NNP") || pos.equals("NNPS")) {
					posCount.put("NNP", (Integer) posCount.get("NNP") + 1);
				} else if (pos.equals("PRP") || pos.equals("PRP$")) {
					posCount.put("PRP", (Integer) posCount.get("PRP") + 1);
				} else if (pos.equals("VB") || pos.equals("VBD")
						|| pos.equals("VBG") || pos.equals("VBN")
						|| pos.equals("VBP") || pos.equals("VBZ")) {
					posCount.put("VB", (Integer) posCount.get("VB") + 1);
				} else if (pos.equals("WH") || pos.equals("WDT")
						|| pos.equals("WRB")) {
					posCount.put("WH", (Integer) posCount.get("WH") + 1);
				}
			}

			sentLength.add((double) count);

		}

		posCount.put("ML", Descriptive.mean(sentLength));

		return (posCount);
	}

	// Current time in defined format
	public static String now() {
		String DATE_FORMAT_NOW = "MM-dd-yyyy_HH-mm-ss";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

}