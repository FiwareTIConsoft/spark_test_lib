package com.tilab.ca.spark_test_lib.streaming.interfaces;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.util.ManualClock;
import org.junit.Assert;

public class SparkTestExecutor {

	protected  JavaStreamingContext jssc;
	private ManualClock mc=null;
	protected long batchDurationMillis;
	
	private SparkStreamJob ssj;
	private ExpectedOutputHandler eoh;
	private TestContainer tc;
	
	public SparkTestExecutor(JavaStreamingContext jssc,ManualClock mc,long batchDurationMillis){
		this.jssc=jssc;
		this.mc=mc;
		this.batchDurationMillis=batchDurationMillis;
	}
	
	public SparkTestExecutor sparkStreamJob(SparkStreamJob ssj){
		this.ssj=ssj;
		return this;
	}
	
	public SparkTestExecutor expectedOutputHandler(ExpectedOutputHandler eoh){
		this.eoh=eoh;
		return this;
	}
	
	public SparkTestExecutor test(TestContainer tc){
		this.tc=tc;
		return this;
	}
	
	
	public void executeTest(int numBatches,int timeoutMillis){
		System.out.println("Starting test");
		ssj.execute(jssc);
		jssc.start();
		sleep(100);
		long startTime=System.currentTimeMillis();
		if(mc!=null)
			mc.addToTime(numBatches*batchDurationMillis);
		
		while(!eoh.isExpectedOutputFilled() && System.currentTimeMillis()-startTime < timeoutMillis){
			jssc.awaitTermination(50);
		}
		long timeElapsed=System.currentTimeMillis()-startTime;
		assertThat(timeElapsed < timeoutMillis, String.format("SparkJob timed Out after %d milliseconds", timeoutMillis));
		System.out.println(String.format("Spark Job took %d milliseconds",timeElapsed));
		
		System.out.println("Stopping spark context..");
		jssc.stop(true);
		
		long startTestTime=System.currentTimeMillis();
		System.out.println("Executing test..");
		tc.execute(eoh);
		System.out.println(String.format("Test Execution took %d milliseconds",System.currentTimeMillis()-startTestTime));
		System.out.println(String.format("Whole Test Execution took %d milliseconds",System.currentTimeMillis()-startTime));
		System.out.println("Done");
	}
	
	private void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void assertThat(boolean condition,String message){
		if(!condition)
			Assert.fail(message);
	}
}
