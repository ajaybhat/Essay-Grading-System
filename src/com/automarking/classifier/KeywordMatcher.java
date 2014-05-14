package com.automarking.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.util.Version;

import com.automarking.keywordsguesser.KeywordsGuesserTest;

/**
 * 
 */
public class KeywordMatcher {
	String QID;
	private double positiveMeasure,negativeMeasure;

	/**
	 * Constructor for matching keywords
	 */
	public KeywordMatcher(String qID,String text) {
		QID = qID;

		double positive = 0,total = 0;
		StringTokenizer tokenizer = new StringTokenizer(text);
		List<String> tokenList = new ArrayList<>(),keywords = readFile();

		while (tokenizer.hasMoreTokens())
			tokenList.add(tokenizer.nextToken());
		tokenList = KeywordsGuesserTest.removeStopWords(tokenList);
		for (String word:tokenList)
		{
			if(keywords.contains(word.toLowerCase()))
				positive++;
		}
		total = (double)((int)tokenList.size());
		positiveMeasure = positive/total;
		negativeMeasure = 1 - positiveMeasure;
	}

	public double getpositiveMeasure()
	{
		return positiveMeasure;
	}
	 
	public double getnegativeMeasure()
	{
		return negativeMeasure;
	}
	 
	
	@SuppressWarnings("resource")
	List<String> readFile() {
		List<String> keywords = new ArrayList<>();
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(System.getProperty("user.dir")
							+ "/data/Keywords/" + QID + ".txt"))));
			String line;

			while ((line = input.readLine()) != null)
				keywords.add(line);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return keywords;
	}
}
