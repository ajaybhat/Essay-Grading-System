package com.automarking.keywords;

import java.io.*;
import java.util.*;

import static java.lang.System.getProperty;

/**
 * Runs the keyword guesser
 */
public class KeywordsGuesserRunner {

    static List<String> stopWords = Arrays.asList(
            "a", "about", "above", "above", "across",
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
            "yourself", "yourselves"
    );

    @SuppressWarnings("deprecation")
    public static List<String> removeStopWords(List<String> keywords) {
        stopWords.forEach(stopWord -> keywords.removeIf(stopWord::equalsIgnoreCase));
        return keywords;
    }

    boolean search(String key) {
        for (String stopWord : stopWords) {
            if (key.equalsIgnoreCase(stopWord)) {
                return true;
            }

        }
        return false;
    }

    /**
     * @throws IOException
     */
    @SuppressWarnings({"rawtypes", "resource"})
    private static void guessKeywords() throws IOException {
        File file = new File(getProperty("user.dir") + "/data/Answer_datasets/test.tsv");
        System.out.println(file.getAbsolutePath());
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line, essay = "", label = "";
        Scanner s;

        int gFirst = 0, gSecond = 0, total, scoreTwoTuples = 0, scoreThreeTuples = 0, goodFrequency = 0;
        List<String> keywordStems = new ArrayList<>();
        List<Keyword> keywords;
        System.out.println("Working...\nExtracting keywords...");
        while ((line = input.readLine()) != null) {
            s = new Scanner(line);
            s.useDelimiter("\t");

            try {
                essay = "";
                label = s.next();
                label = s.next();

                essay = s.next();

                StringTokenizer tokenizer = new StringTokenizer(essay);
                StringBuilder stBuffer = new StringBuilder();
                while (tokenizer.hasMoreTokens()) {
                    String t = tokenizer.nextToken();
                    if (!t.startsWith("@")) {
                        stBuffer.append(t).append(" ");
                    }
                }

                essay = stBuffer.toString().trim();
                gFirst = s.nextInt();
                gSecond = s.nextInt();

            } catch (InputMismatchException e) {
                System.out.println(label + "\t" + gFirst + "\t" + s.next());
            }
            KeywordsGuesser keywordsGuesser = new KeywordsGuesser();
            if (gFirst == 2 && gSecond == 2) {
                scoreTwoTuples++;
            }
            if (gSecond == 3 && gSecond == 3) {
                scoreThreeTuples++;
            }

            if (gFirst >= 2 || gSecond >= 2) {
                keywords = keywordsGuesser.guessFromString(essay);
                for (Keyword keyword : keywords) {
                    if (keyword.getFrequency() > 5 && keyword.getStem().length() > 2) {
                        keywordStems.add(keyword.getStem());
                        goodFrequency++;
                    }
                }
            }
        }


        keywordStems = removeStopWords(new ArrayList<>(new HashSet<>(keywordStems)));
        Collections.sort(keywordStems);

        total = keywordStems.size();
        System.out.println(
                "Tuples with score 2 : " + scoreTwoTuples
                        + "\nTuples with score 3 : " + scoreThreeTuples
                        + "\nTotal Keywords with good frequency : " + goodFrequency
                        + "\nTotal Extracted Tuples : " + (scoreTwoTuples + scoreThreeTuples)
                        + "\nTotal keywords : " + total
                        + "\n\nKeywords\n========================");

        keywordStems.forEach(System.out::println);
        System.out.println("Writing keywords to file...");
        BufferedWriter writer = new BufferedWriter(new FileWriter(getProperty("user.dir") + "/data/Keywords/test.txt", true));
        keywordStems.forEach(keyword ->

                        writeToFile(writer, keyword)

        );
    }

    public static void main(String[] args) throws IOException {
        guessKeywords();
    }

    private static void writeToFile(BufferedWriter writer, String text) {
        try {
            writer.write(text);
        } catch (IOException e) {
            System.out.println("Couldn't write " + text);
        }
    }
}
