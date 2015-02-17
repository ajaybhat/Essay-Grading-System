package com.automarking.classifier;

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

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

public class FeatureGenerator {
    private static SpellChecker spellChecker = null;
    private static StanfordCoreNLP pipeLine;

    public FeatureGenerator() throws IOException {
        if (spellChecker == null) {
            spellChecker = new SpellChecker(new SpellDictionaryHashMap(new File(System.getProperty("user.dir") + "/lib/english.0")));
        }

        if (pipeLine == null) {
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos");
            pipeLine = new StanfordCoreNLP(props);
        }
    }

    // Finding the number of misspelled words in the essay
    public static int misspelled(String text) throws IOException {
        new FeatureGenerator();
        return spellChecker.checkSpelling(new StringWordTokenizer(text));
    }

    // Calculating POS counts
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LinkedHashMap posFeatures(String text) {

        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // Run all Annotators on this text
        pipeLine.annotate(document);

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

        // For every sentence in the text
        for (CoreMap sentence : sentences) {
            // Keeping a count of number of tokens in the sentence
            int count = 0;

            // For every token in the sentence
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // Increment token count
                count++;

                String pos = token.get(PartOfSpeechAnnotation.class);

                // Populating posCount HashMap
                switch (pos) {
                    case ".":
                    case "!":
                    case "?":
                        posCount.put(".", (Integer) posCount.get(".") + 1);
                        break;
                    case "DT":
                        posCount.put("DT", (Integer) posCount.get("DT") + 1);
                        break;
                    case "IN":
                        posCount.put("IN", (Integer) posCount.get("IN") + 1);
                        break;
                    case "JJ":
                    case "JJR":
                    case "JJS":
                        posCount.put("JJ", (Integer) posCount.get("JJ") + 1);
                        break;
                    case "NN":
                    case "NNS":
                        posCount.put("NN", (Integer) posCount.get("NN") + 1);
                        break;
                    case "NNP":
                    case "NNPS":
                        posCount.put("NNP", (Integer) posCount.get("NNP") + 1);
                        break;
                    case "PRP":
                    case "PRP$":
                        posCount.put("PRP", (Integer) posCount.get("PRP") + 1);
                        break;
                    case "VB":
                    case "VBD":
                    case "VBG":
                    case "VBN":
                    case "VBP":
                    case "VBZ":
                        posCount.put("VB", (Integer) posCount.get("VB") + 1);
                        break;
                    case "WH":
                    case "WDT":
                    case "WRB":
                        posCount.put("WH", (Integer) posCount.get("WH") + 1);
                        break;
                }
            }

            sentLength.add((double) count);

        }

        posCount.put("ML", Descriptive.mean(sentLength));

        return (posCount);
    }
}