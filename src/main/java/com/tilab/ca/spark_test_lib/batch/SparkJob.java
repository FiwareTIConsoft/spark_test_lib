package com.tilab.ca.spark_test_lib.batch;

import org.apache.spark.api.java.JavaSparkContext;

public interface SparkJob {
	public void execute(JavaSparkContext jsc);
}
