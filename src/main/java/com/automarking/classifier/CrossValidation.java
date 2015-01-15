package com.automarking.classifier;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;

/**
 * Performs a single run of cross-validation and adds the prediction on the test
 * set to the dataset.
 */
public class CrossValidation {

	/**
	 * Performs the cross-validation. See Javadoc of class for information on
	 * command-line parameters.
	 *
	 * @param args the command-line parameters
	 *
	 * @throws Exception if something goes wrong
	 */
	public static void main(String[] args) throws Exception {
		// loads data and set class index
		Instances data = DataSource.read(
				System.getProperty( "user.dir" )
						+ "/data/Arffs/Q006.arff"
		);
		System.out.println( "Read instances" );
		data.deleteAttributeAt( 0 );
		String clsIndex = "last";
		if ( clsIndex.length() == 0 ) {
			clsIndex = "last";
		}
		switch ( clsIndex ) {
			case "first":
				data.setClassIndex( 0 );
				break;
			case "last":
				data.setClassIndex( data.numAttributes() - 1 );
				break;
			default:
				data.setClassIndex( Integer.parseInt( clsIndex ) - 1 );
				break;
		}
		System.out.println( "Set classindex : " + data.classIndex() );
		// classifier

		Classifier cls = new NaiveBayes();
		// cls.setNumTrees(30);
		// other options
		int seed = (int) System.currentTimeMillis();
		// int folds = Integer.parseInt(Utils.getOption("x", args));
		int folds = 10;
		// randomize data
		Random rand = new Random( seed );
		Instances randData = new Instances( data );
		randData.randomize( rand );
		System.out.println( "Randomized data" );

		if ( randData.classAttribute().isNominal() ) {
			System.out.println( "Class attribute is nominal. Stratifying..." );
			randData.stratify( folds );

		}

		// perform cross-validation and add predictions
		Instances predictedData = null;
		Evaluation eval = new Evaluation( randData );
		for ( int n = 0; n < folds; n++ ) {

			System.out.println(
					"Fold #" + (n + 1)
							+ "\n=============\nGetting training data..."
			);
			Instances train = randData.trainCV( folds, n );
			System.out.println( "Getting testing data..." );
			Instances test = randData.testCV( folds, n );

			// the above code is used by the StratifiedRemoveFolds filter, the
			// code below by the Explorer/Experimenter:
			// Instances train = randData.trainCV(folds, n, rand);

			// build and evaluate classifier
			System.out.println( "Building classifier..." );
			cls.buildClassifier( train );
			System.out.println( "Evaluating model..." );
			eval.evaluateModel( cls, test );
			// add predictions

			cls = new NaiveBayes();
			AddClassification filter = new AddClassification();
			System.out.println( "Creating filter..." );
			filter.setClassifier( cls );
			filter.setOutputClassification( true );
			filter.setOutputDistribution( true );
			filter.setOutputErrorFlag( true );
			filter.setInputFormat( train );

			System.out.println( "Training classifier..." );
			Filter.useFilter( train, filter ); // trains the classifier

			System.out.println( "Performing predictions on testing data..." );
			Instances pred = Filter.useFilter( test, filter ); // perform
			// predictions
			// on test set
			if ( predictedData == null ) {
				predictedData = new Instances( pred, 0 );
			}
			for ( int j = 0; j < pred.numInstances(); j++ ) {
				predictedData.add( pred.instance( j ) );
			}
		}

		// output evaluation
		System.out.println();
		System.out.println( "=== Setup ===" );
		System.out.println( "Classifier: " + cls.getClass().getName() );
		System.out.println( "Dataset: " + data.relationName() );
		System.out.println( "Folds: " + folds );
		System.out.println( "Seed: " + seed );
		System.out.println();
		System.out.println(
				eval.toSummaryString(
						"=== " + folds
								+ "-fold Cross-validation ===", true
				)
		);

		// output "enriched" dataset
		// DataSink.write(Utils.getOption("o", args), predictedData);
		DataSink.write(
				System.getProperty( "user.dir" )
						+ "/data/predictions.arff", predictedData
		);

	}
}
