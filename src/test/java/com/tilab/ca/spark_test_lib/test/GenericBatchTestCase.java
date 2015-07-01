package com.tilab.ca.spark_test_lib.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.junit.Test;

import com.tilab.ca.spark_test_lib.batch.SparkBatchTest;

public class GenericBatchTestCase extends SparkBatchTest{

	private final String RES_PATH;
	
	public GenericBatchTestCase() {
		super(GenericBatchTestCase.class);
		String workingDir = System.getProperty("user.dir");
		RES_PATH=String.format("%s%ssrc%stest%sresources%s%s%s",
				workingDir,File.separator,File.separator,
				File.separator,File.separator,"test_dir",File.separator);
		System.out.println("res path is "+RES_PATH);
	}
	
        
        @Test
	public void singleTest(){
		$newBatchTest()
			.sparkTest(
					(jsc) -> {
						JavaRDD<String> testRdd=jsc.textFile(RES_PATH+"file_test_1.txt");
						return testRdd.map((str) -> Integer.parseInt(str.split(" ")[2])).reduce((a,b) -> a+b);
						
					}
			).test(
				(res) ->{
					assertEquals(5,(int) res);
				}
			).execute();
	}
        
        
        @Test
	public void singleVoidTest(){
		$newBatchTest()
			.sparkTest(
					(jsc) -> {
						JavaRDD<String> testRdd=jsc.textFile(RES_PATH+"file_test_1.txt");
						int count=testRdd.map((str) -> Integer.parseInt(str.split(" ")[2])).reduce((a,b) -> a+b);
						assertEquals(5,(int) count);
					}
			).execute();
	}
	
	

	
}
