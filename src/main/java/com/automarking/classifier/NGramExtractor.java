package com.automarking.classifier;

import java.util.Hashtable;

public class NGramExtractor {
    public static BigramFrequencies computeBigramFrequencies(String text) {
        text = text.toLowerCase();
        char[] tokens = text.toCharArray();
        int nrTokens = tokens.length;
        BigramFrequencies bigrams = new BigramFrequencies();

        for (int i = 1; i < nrTokens; i++) {
            while (i < nrTokens && illegalBigram(tokens[i - 1], tokens[i])) {
                i++;
            }
            if (i < nrTokens) {
                String s = Character.toString(tokens[i - 1])
                        + Character.toString(tokens[i]);
                bigrams.increaseFrequency(s);
            }
        }
        return bigrams;
    }

    private static boolean illegalBigram(char c, char c2) {
        boolean b = !(((c >= 'a' && c <= 'z') || c == '$' || c == '#') && ((c2 >= 'a' && c2 <= 'z')
                || c2 == '$' || c2 == '#'));
        return b;
    }

    public static int numberOfDifferentNgrams(int[] nGramsFrequencies) {
        int nDifferentNGrams = 0;
        for (int i = 0; i < nGramsFrequencies.length; i++) {
            if (nGramsFrequencies[i] > 0) {
                nDifferentNGrams++;
            }
        }
        return nDifferentNGrams;
    }

    public static double NGramTypeTokenRatio(int[] nGramsFrequencies) {
        double nTokens = 0;
        for (int i = 0; i < nGramsFrequencies.length; i++) {
            nTokens = nTokens + (double) nGramsFrequencies[i];
        }
        double types = numberOfDifferentNgrams(nGramsFrequencies);
        return (types / nTokens);
    }

    public static int numberOfUnexpectedBigrams(
            int[] nGramsFrequencies,
            String[] bigrams) {
        Hashtable<String, Double> normalizedBigramFrequencyTable = new Hashtable<String, Double>();
        for (int i = 0; i < nGramsFrequencies.length; i++) {
            normalizedBigramFrequencyTable.put(
                    bigrams[i],
                    (double) nGramsFrequencies[i]
                            / (double) nGramsFrequencies.length
            );
        }
        int nUnexpectedBigrams = 0;
        for (String bigram : normalizedBigramFrequencyTable.keySet()) {
            if (normalizedBigramFrequencyTable.get(bigram) <= 0.05) {
                nUnexpectedBigrams++;
            }
        }
        return nUnexpectedBigrams;
    }

    public static BigramFrequencies getBigramTemplate() {
        BigramFrequencies bigrams = new BigramFrequencies();
        return bigrams;
    }


    public static TrigramFrequencies computeTrigramFrequencies(String text) {
        text = text.toLowerCase();
        char[] tokens = text.toCharArray();
        int nrTokens = tokens.length;
        TrigramFrequencies trigrams = new TrigramFrequencies();

        for (int i = 2; i < nrTokens; i++) {
            while (i < nrTokens
                    && illegalTrigram(tokens[i - 2], tokens[i - 1], tokens[i])) {
                i++;
            }
            if (i < nrTokens) {
                String s = Character.toString(tokens[i - 2])
                        + Character.toString(tokens[i - 1])
                        + Character.toString(tokens[i]);
                trigrams.increaseFrequency(s);
            }
        }
        return trigrams;
    }

    private static boolean illegalTrigram(char c, char c2, char c3) {
        boolean b = !(((c >= 'a' && c <= 'z') || c == '$' || c == '#')
                && ((c2 >= 'a' && c2 <= 'z') || c2 == '$' || c2 == '#') && ((c3 >= 'a' && c3 <= 'z')
                || c3 == '$' || c3 == '#'));
        return b;
    }


    public static int numberOfUnexpectedTrigrams(
            int[] nGramsFrequencies,
            String[] trigrams) {
        Hashtable<String, Double> normalizedTrigramFrequencyTable = new Hashtable<String, Double>();
        for (int i = 0; i < nGramsFrequencies.length; i++) {
            normalizedTrigramFrequencyTable.put(
                    trigrams[i],
                    (double) nGramsFrequencies[i]
                            / (double) nGramsFrequencies.length
            );
        }
        int nUnexpectedTrigrams = 0;
        for (String trigram : normalizedTrigramFrequencyTable.keySet()) {
            if (normalizedTrigramFrequencyTable.get(trigram) <= 0.05) {
                nUnexpectedTrigrams++;
            }
        }
        return nUnexpectedTrigrams;
    }

    public static TrigramFrequencies getTrigramTemplate() {
        TrigramFrequencies trigrams = new TrigramFrequencies();
        return trigrams;

    }
}
