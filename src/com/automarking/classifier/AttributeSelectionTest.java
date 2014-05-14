package com.automarking.classifier;
import weka.attributeSelection.*;
import weka.core.*;
import weka.core.converters.ConverterUtils.*;
import weka.classifiers.*;
import weka.classifiers.meta.*;
import weka.classifiers.trees.*;
import weka.filters.*;

import java.util.*;

/**
 * performs attribute selection using CfsSubsetEval and GreedyStepwise
 * (backwards) and trains J48 with that. Needs 3.5.5 or higher to compile.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttributeSelectionTest {

	/**
	 * uses the meta-classifier
	 */
	protected static void useClassifier(Instances data) throws Exception {
		System.out.println("\n1. Meta-classfier");
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		CfsSubsetEval eval = new CfsSubsetEval();
		//GreedyStepwise search = new GreedyStepwise();
		GeneticSearch search = new GeneticSearch();
	//	search.setSearchBackwards(false);
		RandomForest base = new RandomForest();
		classifier.setClassifier(base);
		System.out.println("Set the classifier : " + base.toString());
		classifier.setEvaluator(eval);
		System.out.println("Set the evaluator : " + eval.toString());
		classifier.setSearch(search);
		System.out.println("Set the search : " + search.toString());
		Evaluation evaluation = new Evaluation(data);
		evaluation.crossValidateModel(classifier, data, 10, new Random(1));
		System.out.println(evaluation.toSummaryString());
	}

	/**
	 * uses the filter
	 */
	protected static void useFilter(Instances data) throws Exception {
		System.out.println("\n2. Filter");
		weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);
		filter.setEvaluator(eval);
		System.out.println("Set the evaluator : " + eval.toString());
		filter.setSearch(search);
		System.out.println("Set the search : " + search.toString());
		filter.setInputFormat(data);
		//System.out.println("Set the input format : " + data.toString());
		Instances newData = Filter.useFilter(data, filter);
		//System.out.println("Results of Filter:\n"+newData);
		
	}

	/**
	 * uses the low level approach
	 */
	protected static void useLowLevel(Instances data) throws Exception {
		System.out.println("\n3. Low-level");
		AttributeSelection attsel = new AttributeSelection();
		CfsSubsetEval eval = new CfsSubsetEval();
		//GreedyStepwise search = new GreedyStepwise();
		//search.setSearchBackwards(false);
		GeneticSearch search = new GeneticSearch();
		search.setCrossoverProb(0.2);
		attsel.setEvaluator(eval);
		System.out.println("Set the evaluator : " + eval.toString());
		attsel.setSearch(search);
		System.out.println("Set the search : " + search.toString());
		attsel.SelectAttributes(data);
		int[] indices = attsel.selectedAttributes();
		//System.out.println("selected attribute indices (starting with 0):\n"
			//	+ Utils.arrayToString(indices));
		for (int i = 0; i < indices.length; i++) {
			System.out.println(data.attribute(i).toString());
		}
	}

	/**
	 * takes a dataset as first argument
	 * 
	 * @param args
	 *            the commandline arguments
	 * @throws Exception
	 *             if something goes wrong
	 */
	public static void main(String[] args) throws Exception {
		// load data
		System.out.println("\n0. Loading data");
		DataSource source = new DataSource(System.getProperty("user.dir")
				+ "/data/Arffs/Q010.arff");
		Instances data = source.getDataSet();
		data.deleteAttributeAt(0);
		data.setClassIndex(data.numAttributes() - 1);

		// 1. meta-classifier
		useClassifier(data);

		// 2. filter
		useFilter(data);

		// 3. low-level
		useLowLevel(data);
	}
}
