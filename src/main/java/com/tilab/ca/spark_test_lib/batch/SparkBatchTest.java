package com.tilab.ca.spark_test_lib.batch;

import java.lang.annotation.Annotation;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Before;

import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.utils.MasterTypes;

public class SparkBatchTest {

	private String master=MasterTypes.LOCAL_2_THREADS;
	private String appName="";
	private JavaSparkContext javaSparkContext=null;
	
	public SparkBatchTest(Class<? extends SparkBatchTest> cl) {
		Annotation[] annotations = cl.getAnnotations();
		for(Annotation annotation : annotations){
			if(annotation instanceof SparkTestConfig){
				SparkTestConfig stc=(SparkTestConfig) annotation;
				master=stc.master();
				appName=stc.appName();
			}
		}
	}
	
	public SparkBatchTest withAppName(String name){
		appName=name;
		return this;
	}
	
	public SparkBatchTest withMaster(String master){
		this.master=master;
		return this;
	}
	
	public SparkTestBatchExecutor $newBatchTest(){
		return new SparkTestBatchExecutor(javaSparkContext);
	}
	
	@Before
	public void setUp(){
		System.out.println("Setting up the environment..");
		SparkConf conf=new SparkConf().setAppName(appName).setMaster(master);
		javaSparkContext=new JavaSparkContext(conf);
	}

	@After
	public void tearDown(){
		System.out.println("Tear down the environment..");
		javaSparkContext.stop();
	}
	
}
