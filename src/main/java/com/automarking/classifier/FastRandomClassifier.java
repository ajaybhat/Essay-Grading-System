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

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

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
	 * Question ID
	 * @param essay
	 * Test essay as String
	 * @return The class to which essay belong
	 */
	static String QID;

	public static int test(String qID, String essay) {
		QID = qID;
		String temp = QID + "\t" + essay;
		System.out.println( temp );
		essay = temp;
		System.out.println( "Random Forest Classifier now running..." );
		try {
			RandomForest randomForest = loadModel( QID );
			System.out.println( "Loading trained model done.\nTesting..." );

			BufferedReader header = new BufferedReader(
					new FileReader(
							System.getProperty( "user.dir" ) + "/data/Arffs/header.arff"
					)
			);
			String line, desp = "";
			while ( (line = header.readLine()) != null ) {
				desp = desp + line + "\n";
			}
			header.close();

			BufferedWriter data = new BufferedWriter(
					new FileWriter(
							System.getProperty( "user.dir" ) + "/data/Arffs/test.arff"
					)
			);
			data.write( desp );
			data.write( ArffGenerator.getFeatures( essay, QID ) );
			data.close();
			BufferedReader input = new BufferedReader(
					new FileReader(
							System.getProperty( "user.dir" ) + "/data/Arffs/test.arff"
					)
			);
			Instances test = new Instances( input );
			test.setClassIndex( test.numAttributes() - 1 );
			int clsLabel = 0;
			for ( int i = 0; i < test.numInstances(); i++ ) {
				Instance current = test.instance( i );
				clsLabel = (int) randomForest.classifyInstance( current );
			}

			return clsLabel;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		// load data
		// train(trainingFile,trainedModel);
		storeModel( "Q004" );
	}

	/**
	 * This method loads the model to be used as classifier.
	 *
	 * @param fileName The name of the file that stores the text.
	 */
	public static RandomForest loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new FileInputStream(
							fileName
					)
			);
			Object tmp = in.readObject();
			RandomForest classifier = (RandomForest) tmp;
			in.close();
			System.out.println( "===== Loaded model: " + fileName + " =====" );
			return classifier;
		}
		catch (Exception e) { // Given the cast, a ClassNotFoundException must
			// be caught along with the IOException
			System.out.println( "Problem found when reading: " + fileName );
		}
		return null;
	}

	private static void storeModel(String trainedModel) {
		try {
			Instances structure = new Instances(
					new FileReader(
							new File(
									System.getProperty( "user.dir" ) + "/data/Arffs/" + trainedModel
											+ ".arff"
							)
					)
			);
			structure.setClassIndex( structure.numAttributes() - 1 );
			System.out.println( "Loaded data from arff file..." );

			RandomForest randomForest = new RandomForest();
			randomForest.setNumFeatures( 30 );
			randomForest.setNumTrees( 1000 );

			System.out.println( "Training..." );
			randomForest.buildClassifier( structure );

			System.out.println(
					"Saving trained model to '" + trainedModel
							+ "'."
			);

			// Write trained model to file
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(
							new File(
									System.getProperty( "user.dir" ) + "/data/Models/"
											+ trainedModel + ".arff"
							)
					)
			);
			oos.writeObject( randomForest );
			oos.flush();
			oos.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
