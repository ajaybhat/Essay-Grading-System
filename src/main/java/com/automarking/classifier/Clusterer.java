package com.automarking.classifier;

import java.io.File;
import java.io.FileReader;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

/**
 *
 */
public class Clusterer {
	public static void main(String[] args) throws Exception {
		new Clusterer().get();
	}

	void get() throws Exception {
		SimpleKMeans kMeans = new SimpleKMeans();
		Instances structure = new Instances(
				new FileReader(
						new File(
								System.getProperty( "user.dir" ) + "/data/Arffs/Q005.arff"
						)
				)
		);
		structure.deleteAttributeAt( structure.numAttributes() - 1 );
		ClusterEvaluation eval = new ClusterEvaluation();

		kMeans.setNumClusters( 5 );
		eval.setClusterer( kMeans );

		kMeans.buildClusterer( structure );
		eval.evaluateClusterer( structure );
		Instances ce = kMeans.getClusterCentroids();

		for ( int i = 0; i < ce.numInstances(); i++ ) {
			System.out.println( ce.instance( i ).toString() );
		}
		System.out.println( eval.clusterResultsToString() );
	}
}
