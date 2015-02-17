package com.automarking.classifier;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;

/**
 * Train the Random Forest classifier on the data
 *
 * @author Ajay
 */
public class Classifier {
    static String QID;

    public static void main(String[] args) throws Exception {

        trainModel("Q003");
        testModel(System.getProperty("user.dir") + "/data/Models/Q003.model", "7698\t3\t\"The setting of the story, are very dry and humid, and flat, and hilly. The author noticed the terrain changing, Flat road. Was replaced by short, rolling hills (Joe kurmaskie). Being out in the middle of a dessert like surronding during June, and nothing surronding him is a scary thought. The heat, and lack of water affect him, by him possibly getting mirages, or he @MONTH1 faint, because of the heat, and how much fluid he has lost. The hills dont help any eigther.\"\t2\t1\t\t2\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n");
    }


    /**
     * Test the classifier for the input essay and output classified value
     *
     * @param modelFilePath Question ID
     * @param essay         Test essay as String
     * @return The class to which essay belong
     */
    public static int testModel(String modelFilePath, String essay) {
        QID = modelFilePath;
        System.out.println(QID + ":\t" + essay);
        try {
            RandomForest randomForest = loadModel(QID);
            System.out.println("Loading trained model done.\nTesting...");
            String testDataPath = System.getProperty("user.dir") + "/data/Data/test.tsv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(testDataPath));
            writer.write(essay);
            writer.close();

            ArffGenerator.prepareDataset(testDataPath, "test");
            ArffGenerator.generateARFF("test");

            BufferedReader input = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/data/Arffs/test.arff"));
            Instances test = new Instances(input);
            test.setClassIndex(test.numAttributes() - 1);
            System.out.println("Random Forest Classifier now running...");
            for (int i = 0; i < test.numInstances(); i++) {
                Instance current = test.instance(i);
                System.out.println("Assigned a score of: " + randomForest.classifyInstance(current));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * This method loads the model to be used as classifier.
     *
     * @param fileName The name of the file that stores the text.
     */
    public static RandomForest loadModel(String fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Object tmp = in.readObject();
            RandomForest classifier = (RandomForest) tmp;
            in.close();
            System.out.println("===== Loaded model: " + fileName + " =====");
            return classifier;
        } catch (Exception e) { // Given the cast, a ClassNotFoundException must
            // be caught along with the IOException
            System.out.println("Problem found when reading: " + fileName);
        }
        return null;
    }

    /**
     * Train a model and save to filesystme
     *
     * @param trainArffFileName
     */
    private static void trainModel(String trainArffFileName) {
        try {
            Instances structure = new Instances(new FileReader(new File(System.getProperty("user.dir") + "/data/Arffs/" + trainArffFileName + ".arff")));
            structure.setClassIndex(structure.numAttributes() - 1);
            System.out.println("Loaded data from arff file...");

            RandomForest randomForest = new RandomForest();
            randomForest.setNumFeatures(30);
            randomForest.setNumTrees(1000);

            System.out.println("Training...");
            randomForest.buildClassifier(structure);

            System.out.println("Saving trained model to '" + trainArffFileName + "'.");

            // Write trained model to file
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(System.getProperty("user.dir") + "/data/Models/" + trainArffFileName + ".model")));
            objectOutputStream.writeObject(randomForest);
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
