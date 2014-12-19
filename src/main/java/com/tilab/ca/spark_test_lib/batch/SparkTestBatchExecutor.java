package com.tilab.ca.spark_test_lib.batch;

import org.apache.spark.api.java.JavaSparkContext;

import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.interfaces.TestContainer;

public class SparkTestBatchExecutor {

	private JavaSparkContext jsc;
	private SparkJob sj;
	private ExpectedOutputHandler eoh;
	private TestContainer tc;
	
	
	public SparkTestBatchExecutor(JavaSparkContext jsc){
		this.jsc=jsc;
	}
	
	public SparkTestBatchExecutor sparkJob(SparkJob sj){
		this.sj=sj;
		return this;
	}
	
	public SparkTestBatchExecutor expectedOutputHandler(ExpectedOutputHandler eoh){
		this.eoh=eoh;
		return this;
	}
	
	public SparkTestBatchExecutor test(TestContainer tc){
		this.tc=tc;
		return this;
	}
	
	public void executeTest(){
		System.out.println("Starting test");
		System.out.println("Executing spark job");
		long startTime=System.currentTimeMillis();
		sj.execute(jsc);
		System.out.println("executing test");
		tc.execute(eoh);
		System.out.println("Done");
		System.out.println(String.format("Whole Test Execution took %d milliseconds",System.currentTimeMillis()-startTime));
		jsc.stop();
	}
}
