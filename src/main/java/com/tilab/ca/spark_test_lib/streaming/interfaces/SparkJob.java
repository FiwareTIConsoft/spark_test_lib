package com.tilab.ca.spark_test_lib.streaming.interfaces;

import org.apache.spark.api.java.JavaSparkContext;

public interface SparkJob {
	//public void execute(JavaSparkContext jsc,ExpectedOutputHandler eoh);
	public Object execute(JavaSparkContext jsc);
}
