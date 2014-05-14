package com.automarking.classifier;
import java.util.Arrays;

public class BigramFrequencies {
	private String[] bigrams;
	private int[] frequencies;

	public BigramFrequencies() {
		int i = 0;
		bigrams = new String[28 * 28 - 1];
		for (char c = 'a'; c <= 'z'; c++) {
			for (char c2 = 'a'; c2 <= 'z'; c2++) {
				bigrams[i] = Character.toString(c) + Character.toString(c2);
				i++;
			}
			bigrams[i] = Character.toString(c) + Character.toString('#');
			i++;
			bigrams[i] = Character.toString('#') + Character.toString(c);
			i++;
			bigrams[i] = Character.toString(c) + Character.toString('$');
			i++;
			bigrams[i] = Character.toString('$') + Character.toString(c);
			i++;
		}
		bigrams[i] = "#$";
		i++;
		bigrams[i] = "$#";
		i++;
		bigrams[i] = "##";

		frequencies = new int[28 * 28 - 1];
		Arrays.sort(bigrams);
	}

	public void increaseFrequency(String text) {
		int index = Arrays.binarySearch(bigrams, text);
		if(index>=0)
		frequencies[index] = frequencies[index] + 1;
	}

	public String[] getBigrams() {
		return bigrams;
	}

	public int[] getFrequencies() {
		return frequencies;
	}

}
