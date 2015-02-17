package com;import java.io.*;
import java.util.Scanner;

public class FileSeparater {
    public static void main(String[] args) throws Exception {
        System.out.println("Separating...");
        File trainingSet = new File("training_set.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(trainingSet)));

        String line = null;
        Scanner scanner;
        while ((line = reader.readLine()) != null) {
            scanner = getScanner(line);
            scanner.next();
            File file = new File("Q00" + scanner.nextInt() + ".tsv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(line + "\n");
            writer.close();
        }
    }

    private static Scanner getScanner(String input) {
        Scanner scanner = new Scanner(input);
        scanner.useDelimiter("\t");
        return scanner;
    }
}
