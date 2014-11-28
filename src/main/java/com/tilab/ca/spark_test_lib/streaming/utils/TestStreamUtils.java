package com.tilab.ca.spark_test_lib.streaming.utils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.collection.immutable.Seq;



public class TestStreamUtils {

	/*
	 * To apply classTag in JAVA
	 * ClassTag<myImage> newTag=ClassTag$.MODULE$.apply(myImage.class); 
	 * ClassTag<Tuple2<Integer, Results>> newTag2=ClassTag$.MODULE$.apply(Tuple2.class); 
	 * */
	public static JavaDStream<String> createMockDStream(JavaStreamingContext jssc,int minPartitions,String... filesPaths){	
		
		if(filesPaths==null){
			throw new IllegalArgumentException("Cannot create a dstream without at least 1 filePath");
		}
		
		Queue<JavaRDD<String>> rddQueue=new LinkedList<JavaRDD<String>>();
		
		for(int i=0;i<filesPaths.length;i++)
			rddQueue.add(jssc.sparkContext().textFile(filesPaths[i],minPartitions));
		
		return jssc.queueStream(rddQueue);
	}
	
	
	public static JavaDStream<String> createMockDStream(String commonPath,
														JavaStreamingContext jssc,
														int minPartitions,
														String... filesNames){	
		if(filesNames==null || commonPath==null){
			throw new IllegalArgumentException("Cannot create a dstream without at least 1 fileName and commonPath");
		}
		String dirPath=commonPath.endsWith("/")?commonPath:commonPath+"/";
		String[] filesPaths=new String[filesNames.length];
		
		for(int i=0;i<filesNames.length;i++)
			filesPaths[i]=dirPath+filesNames[i];
		
		return createMockDStream(jssc,minPartitions,filesPaths);
	}
	
	
	public static JavaDStream<String> createMockDStreamFromDir(JavaStreamingContext jssc,int minPartitions,String directoryPath){	
		
		if(directoryPath==null){
			throw new IllegalArgumentException("Cannot create a dstream without a directoryPath");
		}
		
		File dir = new File(directoryPath);
		//Queue<JavaRDD<String>> rddQueue=new LinkedList<JavaRDD<String>>();
		String[] children = dir.list();
		
		if (children == null) {
			throw new IllegalArgumentException("directoryPath is invalid or does not exist");
		} else {
			List<String> filesNamesList=Arrays.asList(children);
			filesNamesList.sort((f1,f2) -> f1.compareTo(f2));
			filesNamesList.forEach((fn) -> System.out.println(fn));
			return createMockDStream(directoryPath,jssc, minPartitions, (String[])filesNamesList.toArray());
		}
	}		
}
