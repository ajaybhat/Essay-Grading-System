package com.automarking.classifier;

import java.util.Arrays;

public class TrigramFrequencies {
	private String[] trigrams;
	private int[] frequencies;

	public TrigramFrequencies() {
		int i = 0;
		trigrams = new String[28 * 28 * 28 - 1];
		for ( char c = 'a'; c <= 'z'; c++ ) {
			for ( char c2 = 'a'; c2 <= 'z'; c2++ ) {
				for ( char c3 = 'a'; c3 <= 'z'; c3++ ) {
					trigrams[i] = Character.toString( c )
							+ Character.toString( c2 ) + Character.toString( c3 );
					i++;
				}
				trigrams[i] = Character.toString( c ) + Character.toString( c2 )
						+ Character.toString( '#' );
				i++;
				trigrams[i] = Character.toString( c ) + Character.toString( c2 )
						+ Character.toString( '$' );
				i++;
				trigrams[i] = Character.toString( c ) + Character.toString( '#' )
						+ Character.toString( c2 );
				i++;
				trigrams[i] = Character.toString( c ) + Character.toString( '$' )
						+ Character.toString( c2 );
				i++;
				trigrams[i] = Character.toString( '#' ) + Character.toString( c )
						+ Character.toString( c2 );
				i++;
				trigrams[i] = Character.toString( '$' ) + Character.toString( c )
						+ Character.toString( c2 );
				i++;
			}
			trigrams[i] = Character.toString( c ) + Character.toString( '#' )
					+ Character.toString( '#' );
			i++;
			trigrams[i] = Character.toString( c ) + Character.toString( '#' )
					+ Character.toString( '$' );
			i++;
			trigrams[i] = Character.toString( '#' ) + Character.toString( c )
					+ Character.toString( '#' );
			i++;
			trigrams[i] = Character.toString( '#' ) + Character.toString( c )
					+ Character.toString( '$' );
			i++;
			trigrams[i] = Character.toString( '#' ) + Character.toString( '#' )
					+ Character.toString( c );
			i++;
			trigrams[i] = Character.toString( '#' ) + Character.toString( '$' )
					+ Character.toString( c );
			i++;
			trigrams[i] = Character.toString( c ) + Character.toString( '$' )
					+ Character.toString( '$' );
			i++;
			trigrams[i] = Character.toString( c ) + Character.toString( '$' )
					+ Character.toString( '#' );
			i++;
			trigrams[i] = Character.toString( '$' ) + Character.toString( c )
					+ Character.toString( '$' );
			i++;
			trigrams[i] = Character.toString( '$' ) + Character.toString( c )
					+ Character.toString( '#' );
			i++;
			trigrams[i] = Character.toString( '$' ) + Character.toString( '$' )
					+ Character.toString( c );
			i++;
			trigrams[i] = Character.toString( '$' ) + Character.toString( '#' )
					+ Character.toString( c );
			i++;
		}

		trigrams[i] = "$$#";
		i++;
		trigrams[i] = "$#$";
		i++;
		trigrams[i] = "$##";
		i++;
		trigrams[i] = "#$$";
		i++;
		trigrams[i] = "#$#";
		i++;
		trigrams[i] = "##$";
		i++;
		trigrams[i] = "###";
		i++;
		frequencies = new int[28 * 28 * 28 - 1];
		Arrays.sort( trigrams );
	}

	public void increaseFrequency(String text) {
		int index = Arrays.binarySearch( trigrams, text );
		if ( index >= 0 ) {
			frequencies[index] = frequencies[index] + 1;
		}
	}

	public String[] getTrigrams() {
		return trigrams;
	}

	public int[] getFrequencies() {
		return frequencies;
	}
}
