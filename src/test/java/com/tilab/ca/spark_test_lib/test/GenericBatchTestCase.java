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
		MyOutputHandler mh=new MyOutputHandler(1);
		$newBatchTest()
			.expectedOutputHandler(mh)
			.sparkJob(
					(jsc,moh) -> {
						JavaRDD<String> testRdd=jsc.textFile(RES_PATH+"file_test_1.txt");
						int count=testRdd.map((str) -> Integer.parseInt(str.split(" ")[2])).reduce((a,b) -> a+b);
						((MyOutputHandler)moh).saveData(count);
					}
			).test(
				(eoh) ->{
					List<Integer> outputList=((MyOutputHandler)eoh).getOutputList();
					assertEquals(1,outputList.size());
					assertEquals(5,(int) outputList.get(0));
				}
			).executeTest(10000);
	}
	
	

	
}
