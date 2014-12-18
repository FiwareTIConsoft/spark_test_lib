To use this lib your test class must extend SparkStreamingTest class for Spark Streaming and SparkBatchTest for spark batch.
To provide configuration to the standalone test deploy there are 2 ways:

## Through Annotations:
```
@SparkTestConfig(appName="GenericSparkTest",master="local[2]",batchDurationMillis=1000,useManualClock=true)
```
- **appName:** application name
- **master:** spark master (choose you or use one provided by MasterTypes.class)</li>
- **batchDurationMillis:** virtual duration of the batch in milliseconds</li>
- **useManualClock:** use virtual clock in place of the system one (you don't have really to wait for the batch time)</li>

## Through a simple DSL in the constructor class:
```
	withMaster(MasterTypes.LOCAL_2_THREADS)
	  .withAppName("GenericSparkTest")
	  .withBatchDurationMillis(1000)
	  .useManualClock();
```
In the constructor of the test class provide the current class to the super class:
```
public GenericStreamTestCase() {
		super(GenericStreamTestCase.class);
		....
}
```

Setting up the test
================
## ExpectedOutputHandler
You have to provide a class that implements this interface in order to save the output 
of the component to test and notify to sparkTest when the expected output is reached.

## Create the mock Dstream
Spark test provides different helpers to create a mock DStream from a file that provide the content. Each File represents the content received during a batch. 
These helpers can be found into the classs **TestStreamUtils**:
```
	TestStreamUtils.createMockDStream(JavaStreamingContext, minPartitions, file1Path,file2Path,..,fileNPath);
```
```
	TestStreamUtils.createMockDStream(CommonPath,JavaStreamingContext, minPartitions, file1Name,file2Name,..,fileName);
```
Similar to the previous but you can define a common path to the files.

However if you want to pick all the files inside a target directory you can use the following method:
```
	TestStreamUtils.createMockDStreamFromDir(JavaStreamingContext, minPartitions, directoryPath);
```


## Setting the spark stream job
```
	.sparkStreamJob(
		(jssc) -> {	
					//your spark stream job (jssc is the spark streaming context)
				}
	);
```
## Setting the test
```
	.test(
		(eoh) -> {
			//your test goes here (eoh is an instance of your class implementing the ExpectedOutputHandler interface)
		}
	)
```	
## Complete Structure
```
 $newTest()
 .expectedOutputHandler(moh)
 .sparkStreamJob(
	(jssc) -> {
				JavaDStream<String> ds=TestStreamUtils.createMockDStream(jssc, numPartitions, filepath);
				//spark stream job
  )
  .test(
			(eoh) -> {
				//test code
			}
		)
  .executeTest(numBatches, timeoutMillis);
```

#Test class Example

```
@SparkTestConfig(appName="GenericSparkTest",master=MasterTypes.LOCAL_2_THREADS,
				 batchDurationMillis=1000,useManualClock=true)
public class GenericStreamTestCase extends SparkStreamingTest{
	
	public GenericStreamTestCase() {
		super(GenericStreamTestCase.class);
	    ....
	}
	
	@Test
	public void singleFileStreamTest(){
		MyOutputHandler moh=new MyOutputHandler();
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
```
