package com.automarking.keywordsguesser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.lucene.analysis.StopFilter;

/**
 * 
 */
public class KeywordsGuesserTest {
	

	 static String[] stopWords = {"a", "about", "above", "above", "across",
			"after", "afterwards", "again", "against", "all", "almost",
			"alone", "along", "already", "also", "although", "always", "am",
			"among", "amongst", "amoungst", "amount", "an", "and", "another",
			"any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are",
			"around", "as", "at", "back", "be", "became", "because", "become",
			"becomes", "becoming", "been", "before", "beforehand", "behind",
			"being", "below", "beside", "besides", "between", "beyond", "bill",
			"both", "bottom", "but", "by", "call", "can", "cannot", "cant",
			"co", "con", "could", "couldnt", "cry", "de", "describe", "detail",
			"do", "done", "down", "due", "during", "each", "eg", "eight",
			"either", "eleven", "else", "elsewhere", "empty", "enough", "etc",
			"even", "ever", "every", "everyone", "everything", "everywhere",
			"except", "few", "fifteen", "fify", "fill", "find", "fire",
			"first", "five", "for", "former", "formerly", "forty", "found",
			"four", "from", "front", "full", "further", "get", "give", "go",
			"had", "has", "hasnt", "have", "he", "hence", "her", "here",
			"hereafter", "hereby", "herein", "hereupon", "hers", "herself",
			"him", "himself", "his", "how", "however", "hundred", "ie", "if",
			"in", "inc", "indeed", "interest", "into", "is", "it", "its",
			"itself", "keep", "last", "latter", "latterly", "least", "less",
			"let", "ltd", "made", "many", "may", "me", "meanwhile", "might",
			"mill", "mine", "more", "moreover", "most", "mostly", "move",
			"much", "must", "my", "myself", "name", "namely", "neither",
			"never", "nevertheless", "next", "nine", "no", "nobody", "none",
			"noone", "nor", "not", "nothing", "now", "nowhere", "of", "off",
			"often", "on", "once", "one", "only", "onto", "or", "other",
			"others", "otherwise", "our", "ours", "ourselves", "out", "over",
			"own", "part", "per", "perhaps", "please", "put", "rather", "re",
			"same", "see", "seem", "seemed", "seeming", "seems", "serious",
			"several", "she", "should", "show", "side", "since", "sincere",
			"six", "sixty", "so", "some", "somehow", "someone", "something",
			"sometime", "sometimes", "somewhere", "still", "such", "system",
			"take", "ten", "than", "that", "the", "their", "them",
			"themselves", "then", "thence", "there", "thereafter", "thereby",
			"therefore", "therein", "thereupon", "these", "they", "thickv",
			"thin", "third", "this", "those", "though", "three", "through",
			"throughout", "thru", "thus", "to", "together", "too", "top",
			"toward", "towards", "twelve", "twenty", "two", "un", "under",
			"until", "up", "upon", "us", "very", "via", "want", "was", "we",
			"well", "were", "what", "whatever", "when", "whence", "whenever",
			"where", "whereafter", "whereas", "whereby", "wherein",
			"whereupon", "wherever", "whether", "which", "while", "whither",
			"who", "whoever", "whole", "whom", "whose", "why", "will", "with",
			"within", "without", "would", "yet", "you", "your", "yours",
			"yourself", "yourselves"};

	boolean search(String key) {
		for (int i = 0; i < stopWords.length; i++) {
			if (key.equalsIgnoreCase(stopWords[i]))
				return true;

		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static List<String> removeStopWords(List<String> keywords) {
		List<String> copy = new ArrayList<>(keywords);
		Set<Object> stopWordsSet = StopFilter.makeStopSet(stopWords);
		for (String str : copy) {
			for (Object stop : stopWordsSet) {
				if (str.equalsIgnoreCase((String) stop))
					keywords.remove(str);
			}
		}
		return keywords;
	}

	/**
	 * @throws IOException
	 * 
	 */
	@SuppressWarnings({"rawtypes", "resource"})
	private void parseText() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(System.getProperty("user.dir")
						+ "/data/answers/Q009.tsv")));
		String line, essay = "", label = "";
		Scanner s;

		int gFirst = 0, gSecond = 0, total = 0, totalFive = 0, totalSix = 0, totalZero = 0;
		List<String> keywords = new ArrayList<>();
		List<Keyword> temp = new ArrayList<>();
		System.out.println("Working... Extracting keywords...");
		while ((line = input.readLine()) != null) {
			s = new Scanner(line);
			s.useDelimiter("\t");

			try {
				essay = "";
				label = s.next();
				label = s.next();

				essay = s.next();

				StringTokenizer tokenizer = new StringTokenizer(essay);
				StringBuffer stBuffer = new StringBuffer();
				while (tokenizer.hasMoreTokens()) {
					String t = tokenizer.nextToken();
					if (!t.startsWith("@"))
						stBuffer.append(t + " ");
				}
				essay = stBuffer.toString();
				essay = essay.trim();
				gFirst = s.nextInt();
				gSecond = s.nextInt();

			} catch (InputMismatchException e) {
				System.out.println(label + "\t" + gFirst + "\t" + s.next());
			}
			KeywordsGuesser guess = new KeywordsGuesser();
			if (gFirst >= 20 || gSecond >= 20) {
				temp = guess.guessFromString(essay);
				totalSix++;
			}

			for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
				Keyword keyword = (Keyword) iterator.next();
				if (keyword.getFrequency() > 6
						&& keyword.getStem().length() > 2) {
					keywords.add(keyword.getStem());
					totalZero++;
				}
			}
		}
		keywords = new ArrayList<String>(new HashSet<>(keywords));
		Collections.sort(keywords);
		keywords = removeStopWords(keywords);
		total = keywords.size();
		System.out.println("Tuples with score 2 : " + totalFive
				+ "\nTuples with score 3 : " + totalSix
				+ "\nTotal Keywords with good frequency : " + totalZero
				+ "\nTotal Extracted Tuples : " + (totalFive + totalSix)
				+ "\nTotal keywords : " + total
				+ "\n\nKeywords\n========================");

		for (int i = 0; i < keywords.size(); i++)
			System.out.println(keywords.get(i));

	}

}
