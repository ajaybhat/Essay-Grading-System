package com.automarking.classifier;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;
import weka.filters.unsupervised.attribute.StringToWordVector;

import com.automarking.fastrandomforest.FastRandomForest;

/**
 * Train the Ibk classifier on the data
 * 
 * @author Ajay
 */
public class FastRandomClassifier {
	/**
	 * Trains the classifier for the trained Arff and output classified value
	 * 
	 * @param qID
	 *            Question ID
	 * @param essay
	 *            Test essay as String
	 * @return The class to which essay belong
	 */
	static String QID;
	public static int test(String qID, String essay) {
		QID = qID;
		String temp = QID + "\t" + essay;
		System.out.println(temp);
		essay = temp;
		Instances structure;
		System.out.println("Random Forest Classifier now running...");
		try {
			FastRandomForest frf = loadModel(QID);
			System.out.println("Loading trained model done.\nTesting...");

			BufferedReader header = new BufferedReader(new FileReader(
					System.getProperty("user.dir") + "/data/Arffs/header.arff"));
			String line = new String(), desp = new String();
			while ((line = header.readLine()) != null) {
				desp = desp + line + "\n";
			}
			header.close();

			BufferedWriter data = new BufferedWriter(new FileWriter(
					System.getProperty("user.dir") + "/data/Arffs/test.arff"));
			data.write(desp);
			data.write(ArffGenerator.getFeatures(essay, QID));
			data.close();
			BufferedReader input = new BufferedReader(new FileReader(
					System.getProperty("user.dir") + "/data/Arffs/test.arff"));
			Instances test = new Instances(input);
			test.setClassIndex(test.numAttributes() - 1);
			int clsLabel = 0;
			for (int i = 0; i < test.numInstances(); i++) {
				Instance current = test.instance(i);
				clsLabel = (int) frf.classifyInstance(current);
			}

			return clsLabel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		// load data
		// train(trainingFile,trainedModel);
storeModel("Q004");
	}

	/**
	 * This method loads the model to be used as classifier.
	 * 
	 * @param fileName
	 *            The name of the file that stores the text.
	 * 
	 */
	public static FastRandomForest loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					fileName));
			Object tmp = in.readObject();
			FastRandomForest classifier = (FastRandomForest) tmp;
			in.close();
			System.out.println("===== Loaded model: " + fileName + " =====");
			return classifier;
		} catch (Exception e) { // Given the cast, a ClassNotFoundException must
								// be caught along with the IOException
			System.out.println("Problem found when reading: " + fileName);
		}
		return null;
	}

	private static void storeModel(String trainedModel) {
		try {
			Instances structure = new Instances(new FileReader(new File(
					System.getProperty("user.dir") + "/data/Arffs/" + trainedModel
							+ ".arff")));
			structure.setClassIndex(structure.numAttributes() - 1);
			System.out.println("Loaded data from arff file...");

			FastRandomForest fastRandomForest = new FastRandomForest();
			fastRandomForest.setNumFeatures(30);
			fastRandomForest.setNumTrees(1000);
			
			System.out.println("Training...");
			fastRandomForest.buildClassifier(structure);

			System.out.println("Saving trained model to '" + trainedModel
					+ "'.");
			
			// Write trained model to file
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(new File(
							System.getProperty("user.dir") + "/data/Models/"
									+ trainedModel + ".arff")));
			oos.writeObject(fastRandomForest);
			oos.flush();
			oos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
