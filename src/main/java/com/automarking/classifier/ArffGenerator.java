package com.automarking.classifier;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;

import static com.automarking.classifier.Sentences.*;

/**
 *
 */
public class ArffGenerator {

    static String arffLine, QID = "Q003";

    public static void main(String[] args) throws Exception {

        QID = "Q003";
        // Log Message
        System.out.println("Preparing training dataset...");
        prepareDataset(System.getProperty("user.dir") + "/data/Answer_datasets/" + QID + ".tsv", QID);
        System.out.println("Done");

        // Log Message
        System.out.println("Generating ARFF file...");
        generateARFF(QID);
        System.out.println("Done");

    }

    static String getFeatures(String dataSet, String qID) {

        // Initializing the spell check dictionary
        try {
            // Splitting each row of the dataset
            String tokens[] = (dataSet.trim()).split("\t");

            String misspelled, word_count, char_count, difficult, avg_syllable_word, avg_syllable_sentence, readability, bigramTypeTokenRatio, nUnexpectedBigrams, nDifferentBiGrams, trigramTypeTokenRatio, nUnexpectedTrigrams, nDifferentTriGrams, positiveMeasure, negativeMeasure;

            // Feature = character_count
            String essay = tokens[2];
            char_count = Integer.toString(essay.length());

            // Feature = word_count
            word_count = Integer
                    .toString(getNumberOfWords(essay));
            // Feature = misspelled
            misspelled = Integer.toString(FeatureGenerator.misspelled(essay));
            // Feature = syllables per word
            avg_syllable_word = Double.toString(avgSyllablesPerWord(essay));

            // Feature = syllables per sentence
            avg_syllable_sentence = Double.toString(avgSyllablesPerSentence(essay));

            // Feature = difficult words
            difficult = Integer.toString(getNumberOfDifficultWords(essay));

            // Feature = readabililty
            readability = Double.toString(calculateReadability(essay));

            // Feature = bigram type to token ratio
            bigramTypeTokenRatio = Double.toString(computeBigramTypeTokenRatio(essay));

            // Feature = n unexpected bigrams
            nUnexpectedBigrams = Double.toString(computeNUnexpectedBigrams(essay));

            // Feature = n different bigrams
            nDifferentBiGrams = Double.toString(computeNDifferentBigrams(essay));

            // Feature = trigram type to token ratio
            trigramTypeTokenRatio = Double.toString(computeTrigramTypeTokenRatio(essay));

            // Feature = n unexpected trigrams
            nUnexpectedTrigrams = Double.toString(computeNUnexpectedTrigrams(essay));

            // Feature = n different trigrams
            nDifferentTriGrams = Double.toString(computeNDifferentTrigrams(essay));

            KeywordMatcher matcher = new KeywordMatcher(qID, essay);
            positiveMeasure = Double.toString(matcher.getpositiveMeasure());
            negativeMeasure = Double.toString(matcher.getnegativeMeasure());

            // Features = sentence_count, sent_mean_len and 8 pos features
            // combined in one
            LinkedHashMap posFeatures = FeatureGenerator.posFeatures(essay);
            Iterator itr = posFeatures.keySet().iterator();
            String pos = "";
            while (itr.hasNext()) {
                Object key = itr.next();
                pos = pos + posFeatures.get(key).toString() + ",";
            }
            pos = pos.substring(0, pos.length() - 1);

            // Feature = score
            String score;
            if (tokens.length > 2) {
                // If the dataset is training data
                String score1 = tokens[3], score2 = tokens[4];
                score = Integer.toString((int) ((Double.parseDouble(score1) + Double.parseDouble(score2)) / 2)
                );
            } else {
                score = "0";
            }
            // Writing a data point to the file
            arffLine = char_count + ","
                    + word_count + ","
                    + misspelled + ","
                    + avg_syllable_word + ","
                    + avg_syllable_sentence + ","
                    + difficult + ","
                    + readability + ","
                    + bigramTypeTokenRatio + ","
                    + nDifferentBiGrams + ","
                    + nUnexpectedBigrams + ","
                    + trigramTypeTokenRatio + ","
                    + nDifferentTriGrams + ","
                    + nUnexpectedTrigrams + ","
                    + positiveMeasure + ","
                    + negativeMeasure + ","
                    + pos + ","
                    + score + "\n";
            return arffLine;
        } catch (IOException e) {
            System.out.println("Couldn't read file");
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
        BufferedWriter data = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/data/Data/" + type + ".csv"));

        // Reading the dataset and generating features
        String essay;
        String newLine;
        int count = 0;
        System.out.println();
        while ((essay = file.readLine()) != null) {

            // Ignoring the first line of the dataset
            if (essay.startsWith("Id")) {
                continue;
            }

            // Log Message
            count++;
            System.out.println("Generating features for Essay #" + count);

            newLine = getFeatures(essay, QID);
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
        BufferedReader header = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/data/Arffs/header.arff"));
        String line1;
        String arffLine = "";
        while ((line1 = header.readLine()) != null) {
            arffLine = arffLine + line1 + "\n";
        }
        header.close();

        BufferedWriter arff = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/data/Arffs/" + type + ".arff"));
        arff.write(arffLine);

        BufferedReader csv = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/data/Data/" + type + ".csv"));
        String csvLine;
        while ((csvLine = csv.readLine()) != null) {
            arff.write(csvLine + "\n");
        }

        arff.close();
    }

}
