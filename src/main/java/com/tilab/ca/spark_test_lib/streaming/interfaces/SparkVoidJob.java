
package com.tilab.ca.spark_test_lib.streaming.interfaces;

import org.apache.spark.api.java.JavaSparkContext;


public interface SparkVoidJob {
    public void execute(JavaSparkContext jsc);
}
