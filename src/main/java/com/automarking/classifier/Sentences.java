package com.automarking.classifier;

import java.util.ArrayList;
import java.util.Hashtable;

public class Sentences {

    public static String punctuations = "'[](){}	¨:,’¦!-?\";/";

    public static double averageSentenceLength(String text) {
        int number_of_sentences = 0;
        int number_of_words = 0;
        double avg = 0.0;

        number_of_sentences = numberOfSentences(text);
        number_of_words = getNumberOfWords(text);

        avg = (double) number_of_words / (double) number_of_sentences;
        return avg;
    }

    public static int getNumberOfWords(String text) {
        String delimiters = "[ ]+";
        String[] input = text.split(delimiters);
        return input.length;
    }

    public static int numberOfSentences(String text) {
        String delims = "[.?!]+";
        String[] tokens = text.split(delims);

        return tokens.length;
    }

    public static double calculateReadability(String str) {
        double nWords = (double) getNumberOfWords(str);
        double nSentences = (double) numberOfSentences(str);
        double nSyllables = (double) numberOfSyllables(str);

        return 208.835 - 1.015 * (nWords / nSentences)
                - 84.6 * (nSyllables / nWords);
    }

    public static int numberOfSyllables(String str) {
        int nSyllables = 0;

        String delimiters = " ";
        String[] tokens = str.split(delimiters);

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].contains(".") || tokens[i].contains("!") || tokens[i].contains("?")) {
                tokens[i] = tokens[i].replace(".", "").replace("!", "").replace("?", "");
            }
        }

        for (String token : tokens) {
            char[] current = token.toCharArray();
            char previous = '.';
            // Count number of vowels
            for (int j = 0; j < current.length; j++) {
                if (current[j] == 'a' || current[j] == 'i' || current[j] == 'o'
                        || current[j] == 'u' || current[j] == 'y') {
                    nSyllables++;
                }
                if (current[j] == 'e' && j != current.length - 1) {
                    nSyllables++;
                }
            }
            // substract 1 vowel from every diphthong
            for (int j = 0; j < current.length; j++) {
                char next = '.';
                if (j + 1 != current.length) {
                    next = current[j + 1];
                }
                if (previous == 'a' && current[j] == 'a' || previous == 'a'
                        && current[j] == 'i' || previous == 'a'
                        && current[j] == 'o' || previous == 'a'
                        && current[j] == 'u' || previous == 'a'
                        && current[j] == 'y' || previous == 'a'
                        && current[j] == 'e' || previous == 'i'
                        && current[j] == 'a' || previous == 'i'
                        && current[j] == 'o' || previous == 'i'
                        && current[j] == 'u' || previous == 'i'
                        && current[j] == 'e' || previous == 'o'
                        && current[j] == 'a' || previous == 'o'
                        && current[j] == 'i' || previous == 'o'
                        && current[j] == 'o' || previous == 'o'
                        && current[j] == 'u' || previous == 'o'
                        && current[j] == 'y' || previous == 'o'
                        && current[j] == 'e' || previous == 'u'
                        && current[j] == 'a' || previous == 'u'
                        && current[j] == 'i' || previous == 'u'
                        && current[j] == 'o' || previous == 'u'
                        && current[j] == 'u' || previous == 'u'
                        && current[j] == 'y' || previous == 'u'
                        && current[j] == 'e' || previous == 'y'
                        && current[j] == 'a' || previous == 'y'
                        && current[j] == 'o' || previous == 'y'
                        && current[j] == 'u' || previous == 'y'
                        && current[j] == 'e' || previous == 'e'
                        && current[j] == 'a' || previous == 'e'
                        && current[j] == 'i' || previous == 'e'
                        && current[j] == 'o' || previous == 'e'
                        && current[j] == 'u' || previous == 'e'
                        && current[j] == 'y' || previous == 'e'
                        && current[j] == 'e' || previous == 'a'
                        && current[j] == 'n' && next == 'e' || previous == 'u'
                        && current[j] == 'r' && next == 'e') {
                    nSyllables--;
                }
                previous = current[j];
            }
        }

        return nSyllables;
    }

    public static double avgSyllablesPerWord(String str) {
        double nWords = (double) getNumberOfWords(str);
        double nSyllables = (double) numberOfSyllables(str);

        return nSyllables / nWords;
    }

    public static double avgSyllablesPerSentence(String str) {
        double nSentences = (double) Sentences.numberOfSentences(str);
        double nSyllables = (double) numberOfSyllables(str);

        return nSyllables / nSentences;
    }

    public static double computeBigramTypeTokenRatio(String bigramTypeTokenEssay) {

        BigramFrequencies bigrams = NGramExtractor
                .computeBigramFrequencies(bigramTypeTokenEssay);
        return NGramExtractor.NGramTypeTokenRatio(bigrams.getFrequencies());
    }

    public static int computeNDifferentBigrams(String bigramTypeTokenEssay) {

        BigramFrequencies bigrams = NGramExtractor
                .computeBigramFrequencies(bigramTypeTokenEssay);
        return NGramExtractor.numberOfDifferentNgrams(bigrams.getFrequencies());
    }

    public static int computeNUnexpectedBigrams(String bigramTypeTokenEssay) {

        BigramFrequencies bigrams = NGramExtractor
                .computeBigramFrequencies(bigramTypeTokenEssay);

        return NGramExtractor.numberOfUnexpectedBigrams(bigrams.getFrequencies(), bigrams.getBigrams());
    }

    public static double computeTrigramTypeTokenRatio(String trigramTypeTokenEssay) {

        TrigramFrequencies trigrams = NGramExtractor
                .computeTrigramFrequencies(trigramTypeTokenEssay);
        return NGramExtractor.NGramTypeTokenRatio(trigrams.getFrequencies());
    }

    public static int computeNDifferentTrigrams(String trigramTypeTokenEssay) {

        TrigramFrequencies trigrams = NGramExtractor
                .computeTrigramFrequencies(trigramTypeTokenEssay);
        return NGramExtractor.numberOfDifferentNgrams(trigrams.getFrequencies());
    }

    public static int computeNUnexpectedTrigrams(String trigramTypeTokenEssay) {

        TrigramFrequencies trigrams = NGramExtractor
                .computeTrigramFrequencies(trigramTypeTokenEssay);

        return NGramExtractor.numberOfUnexpectedBigrams(trigrams.getFrequencies(), trigrams.getTrigrams());
    }

    public static boolean isIllegalCharacter(char c) {
        return ((c == '\\') || (c == '|') || (c == '%') || (c == '~')
                || (c == '*') || (c == '+') || (c == '=') || (c == '<')
                || (c == '>') || (c == '`') || (c == '_') || (c == '&') || (c == '$'));
    }

    public static boolean isPunctuation(char c) {
        return punctuations.contains("" + c);
    }

    public static String[] getWords(String text) {
        ArrayList<String> words = new ArrayList<String>();
        char[] input = text.toCharArray();
        String currentWord = "";
        for (char anInput : input) {
            if (!isPunctuation(anInput) && anInput != ' ') {
                currentWord = currentWord + anInput;
            } else {
                if (currentWord.length() > 0) {
                    words.add(currentWord);
                }
                currentWord = "";
            }
        }
        String[] output = new String[words.size()];
        words.toArray(output);
        return output;
    }

    public static Hashtable<String, Integer> getWordFrequencyTable(String text) {
        String[] words = getWords(text);
        Hashtable<String, Integer> frequencyTable = new Hashtable<String, Integer>();
        for (String word : words) {
            if (frequencyTable.containsKey(word)) {
                frequencyTable.put(word, frequencyTable.get(word) + 1);
            } else {
                frequencyTable.put(word, 1);
            }
        }
        return frequencyTable;
    }

    public static Hashtable<String, Double> getNormalizedFrequencyTable(
            String text) {
        Hashtable<String, Integer> frequencyTable = getWordFrequencyTable(text);
        double nWords = getWords(text).length;

        Hashtable<String, Double> normalizedFrequencyTable = new Hashtable<String, Double>();
        for (String word : frequencyTable.keySet()) {
            normalizedFrequencyTable.put(
                    word,
                    ((double) frequencyTable.get(word) / nWords)
            );
        }

        return normalizedFrequencyTable;
    }

    public static int getNumberOfDifficultWords(String text) {
        Hashtable<String, Double> normalizedFrequencyTable = getNormalizedFrequencyTable(text);

        int nDifficultWords = 0;
        for (String word : normalizedFrequencyTable.keySet()) {
            if (normalizedFrequencyTable.get(word) <= 0.1) {
                nDifficultWords++;
            }
        }

        return nDifficultWords;
    }
}
