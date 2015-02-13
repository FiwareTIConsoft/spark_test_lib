package com.tilab.ca.spark_test_lib.streaming.interfaces;

import org.apache.spark.streaming.api.java.JavaStreamingContext;

public interface SparkStreamJob {

	public void execute(JavaStreamingContext jssc,ExpectedOutputHandler eoh);
}
