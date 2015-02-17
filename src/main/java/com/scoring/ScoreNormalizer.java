package com.scoring;

import java.io.*;
import java.util.Scanner;

public class ScoreNormalizer {

    public static void main(String[] args) throws IOException {
        File file = new File("");
        BufferedReader input = new BufferedReader(new FileReader(file));
        BufferedWriter output = new BufferedWriter(new FileWriter(file));

        String line;

        while((line = input.readLine())!=null) {
            Scanner scanner = new Scanner(line);
            scanner.next();
            scanner.next();
            scanner.next();

        }
    }
}
