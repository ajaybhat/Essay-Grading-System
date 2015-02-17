package com.automarking.classifier;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.net.search.global.GeneticSearch;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

import java.util.Random;

/**
 * performs attribute selection using CfsSubsetEval and GreedyStepwise
 */
public class AttributeSelectionRunner {

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
        //	classifier.setSearch( search );
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
        System.out.println("Set the input format : " + data.toString());
        Instances newData = Filter.useFilter(data, filter);
        System.out.println("Results of Filter:\n" + newData);

    }

    /**
     * uses the low level approach
     */
    protected static void useLowLevel(Instances data) throws Exception {
        System.out.println("\n3. Low-level");
        AttributeSelection attsel = new AttributeSelection();
        attsel.SelectAttributes(data);
        int[] indices = attsel.selectedAttributes();
        for (int i = 0; i < indices.length; i++) {
            System.out.println(data.attribute(i).toString());
        }
    }

    /**
     * takes a dataset as first argument
     *
     * @param args the commandline arguments
     * @throws Exception if something goes wrong
     */
    public static void main(String[] args) throws Exception {
        // load data
        System.out.println("\n0. Loading data");
        DataSource source = new DataSource(System.getProperty("user.dir") + "/data/Arffs/Q010.arff");
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
