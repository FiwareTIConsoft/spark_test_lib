package com.tilab.ca.spark_test_lib.streaming;

import java.lang.annotation.Annotation;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.util.ManualClock;
import org.junit.After;
import org.junit.Before;

import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.utils.MasterTypes;

public class SparkStreamingTest {
	protected boolean useManualClock=false;
	protected long batchDurationMillis=1000;
	protected String master=MasterTypes.LOCAL_ALL_DISPOSABLE_THREADS;
	protected String appName="DefaultAppName";
	protected  JavaStreamingContext jssc;
	protected Duration batchDuration=null;
	
	private ManualClock mc=null;
	//private Clock mc=null;
	
	public SparkStreamingTest(){}
	
	public SparkStreamingTest(Class<? extends SparkStreamingTest> cl) {
                appName=cl.getName()+"App";
		Annotation[] annotations = cl.getAnnotations();
		for(Annotation annotation : annotations){
			if(annotation instanceof SparkTestConfig){
				SparkTestConfig stc=(SparkTestConfig) annotation;
				useManualClock=stc.useManualClock();
				batchDurationMillis=stc.batchDurationMillis();
				master=stc.master();
				appName=stc.appName();
			}
		}
	}
	
	@Before
	public void setUp(){
		System.out.println("Setting the environment..");
		System.out.println("Creating spark streaming context");
		batchDuration=new Duration(batchDurationMillis);
		jssc = new JavaStreamingContext(getSparkConfiguration(), batchDuration);
		
		if(useManualClock)
			mc=(ManualClock)jssc.ssc().scheduler().clock();	
	}
	
	@After
	public void tearDown(){
		System.out.println("Tearing down the environment..");
		jssc.stop();
		jssc=null;
		System.clearProperty("spark.streaming.clock");
		System.out.println("End");
	}
	
	public SparkTestExecutor $newTest(){
		return new SparkTestExecutor(jssc, mc, batchDurationMillis);
	}
	
	public long getClockCurrentTime(){
		if(!useManualClock)
			return jssc.ssc().scheduler().clock().getTimeMillis();
		else
			return mc.getTimeMillis();
	}
	
	
	public void setClockStartTime(long timeMillis){
		if(!useManualClock)
			throw new IllegalStateException("Cannot set clock time if useManualClock is false");
		mc.setTime(timeMillis);
	}
	
	public void clockNextStep(long timeMillis){
		if(!useManualClock)
			System.out.println("WARNING: Can go to next step only if manualClock option is set to true.");
		else
			mc.advance(timeMillis);
	}
	
	public void clockNextStep(){
		if(!useManualClock)
			throw new IllegalStateException("Cannot set clock time if useManualClock is false");
		mc.advance(batchDurationMillis);
	}
	
	
	public SparkStreamingTest useManualClock(){
		useManualClock=true;
		return this;
	}
	
	public SparkStreamingTest withBatchDurationMillis(long millis){
		batchDurationMillis=millis;
		return this;
	}
	
	public SparkStreamingTest withAppName(String name){
		appName=name;
		return this;
	}
	
	public SparkStreamingTest withMaster(String master){
		this.master=master;
		return this;
	}
	
	
	private SparkConf getSparkConfiguration(){
		SparkConf conf=new SparkConf().setAppName(appName).setMaster(master);
		
		if(useManualClock){
			System.out.println("Using manual clock");
			conf.set("spark.streaming.clock", "org.apache.spark.streaming.util.ManualClock");
		}else{
			System.out.println("Using system clock");
			conf.set("spark.streaming.clock", "org.apache.spark.streaming.util.SystemClock");
		}
		
		return conf;
	}	
}
