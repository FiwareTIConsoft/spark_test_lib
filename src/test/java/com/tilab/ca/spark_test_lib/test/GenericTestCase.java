package com.tilab.ca.spark_test_lib.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.junit.Test;

import com.tilab.ca.spark_test_lib.streaming.SparkTest;
import com.tilab.ca.spark_test_lib.streaming.annotations.SparkTestConfig;
import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;
import com.tilab.ca.spark_test_lib.streaming.utils.MasterTypes;
import com.tilab.ca.spark_test_lib.streaming.utils.TestStreamUtils;

@SparkTestConfig(appName="GenericSparkTest",master=MasterTypes.LOCAL_2_THREADS,
				 batchDurationMillis=1000,useManualClock=true)
public class GenericTestCase extends SparkTest{

	private final String RES_PATH;
	
	public GenericTestCase() {
		String workingDir = System.getProperty("user.dir");
		RES_PATH=String.format("%s%ssrc%stest%sresources%s%s%s",
				workingDir,File.separator,File.separator,
				File.separator,File.separator,"test_dir",File.separator);
		System.out.println("res path is "+RES_PATH);
	}
	
	@Test
	public void singleFileStreamTest(){
		MyOutputHandler moh=new MyOutputHandler(1);
		$newTest()
		.expectedOutputHandler(moh)
		.sparkStreamJob(
				(jssc) -> {
					JavaDStream<String> ds=TestStreamUtils.createMockDStream(jssc, 1, RES_PATH+"file_test_1.txt");
					ds.map((str) -> Integer.parseInt(str.split(" ")[2])).reduce((a,b) -> a+b)
					.foreachRDD((rdd) -> {
						moh.saveData(rdd.collect().get(0));
						return null;
					});
				}
		).test(
			(eoh) -> {
				List<Integer> outputList=((MyOutputHandler)eoh).getOutputList();
				assertEquals(1,outputList.size());
				assertEquals(5,(int) outputList.get(0));
			}
		).executeTest(1, 20000);
	}
	
	@Test
	public void twoFileStreamTest(){
		MyOutputHandler moh=new MyOutputHandler(2);
		$newTest()
		.expectedOutputHandler(moh)
		.sparkStreamJob(
				(jssc) -> {
					JavaDStream<String> ds=TestStreamUtils.createMockDStream(RES_PATH,jssc,1, 
																			"file_test_1.txt",
																			"file_test_2.txt");
					
					ds.map((str) -> Integer.parseInt(str.split(" ")[2])).reduce((a,b) -> a+b)
					.foreachRDD((rdd) -> {
						moh.saveData(rdd.collect().get(0));
						return null;
					});
				}
		).test(
			(eoh) -> {
				List<Integer> outputList=((MyOutputHandler)eoh).getOutputList();
				assertEquals(2,outputList.size());
				assertEquals(5,(int) outputList.get(0));
				assertEquals(6,(int) outputList.get(1));
			}
		).executeTest(2, 20000);
	}
	
	
	@Test
	public void fileStreamFromFolderTest(){
		MyOutputHandler moh=new MyOutputHandler(3);
		$newTest()
		.expectedOutputHandler(moh)
		.sparkStreamJob(
				(jssc) -> {
					JavaDStream<String> ds=TestStreamUtils.createMockDStreamFromDir(jssc, 1, RES_PATH);
					
					ds.map((str) -> Integer.parseInt(str.split(" ")[2])).reduce((a,b) -> a+b)
					.foreachRDD((rdd) -> {
						moh.saveData(rdd.collect().get(0));
						return null;
					});
				}
		).test(
			(eoh) -> {
				List<Integer> outputList=((MyOutputHandler)eoh).getOutputList();
				assertEquals(3,outputList.size());
				assertEquals(5,(int) outputList.get(0));
				assertEquals(6,(int) outputList.get(1));
				assertEquals(18,(int) outputList.get(2));
			}
		).executeTest(3, 20000);
	}
	
	public class MyOutputHandler implements ExpectedOutputHandler{

		private List<Integer> outputList;
		private int numExpectedData=0;
		
		public MyOutputHandler(int numExpectedData) {
			outputList=new LinkedList<Integer>();
			this.numExpectedData=numExpectedData;
		}
		
		public void saveData(int data){
			outputList.add(data);
		}
		
		
		public List<Integer> getOutputList() {
			return outputList;
		}

		public void setOutputList(List<Integer> outputList) {
			this.outputList = outputList;
		}

		@Override
		public boolean isExpectedOutputFilled() {
			return outputList.size()==numExpectedData;
		}
		
	}
	
}
