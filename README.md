Setting up the environment
================
To use this lib your test class must extend SparkTest class.
To provide configuration to the standalone test deploy there are 2 ways:
- Through Annotations:
	@SparkTestConfig(appName="GenericSparkTest",master="local[2]",batchDurationMillis=1000,useManualClock=true)
- **appName:** application name
- **master:** spark master (choose you or use one provided by MasterTypes.class)</li>
- **batchDurationMillis:** virtual duration of the batch in milliseconds</li>
- **useManualClock:** use virtual clock in place of the system one (you don't have really to wait for the batch time)</li>

-Through a simple dsl in the constructor class:
	withMaster(MasterTypes.LOCAL_2_THREADS).withAppName("GenericSparkTest").withBatchDurationMillis(1000).useManualClock();

Setting up the environment
================
- ExpectedOutputHandler
You have to provide a class that implements this interface in order to save the output 
of the component to test and notify to sparkTest when the expected output is reached.

- Create the mock Dstream
Spark test provides different helpers to create a mock DStream from a files that provide the content.
Example:
	TestStreamUtils.createMockDStream(<spark streaming context>, <num partitions>, <file1_path>,<file2_path>,..,<fileN_path>);
Where each file represent a dstream generated during a batch 

- Setting the spark stream job
	.sparkStreamJob((jssc) -> {	//your spark stream job (jssc is the spark streaming context)});
- Setting the test
	.test((eoh) -> {//your test goes here (eoh is an instance of your class implementing the ExpectedOutputHandler interface)})
	
- Complete Structure
 $newTest()
 .expectedOutputHandler(moh)
 .sparkStreamJob(
 (jssc) -> {
            JavaDStream<String> ds=TestStreamUtils.createMockDStream(jssc, numPartitions, filepath);
			//spark stream job
		).test(
			(eoh) -> {
				//test code
			}
		).executeTest(numBatches, timeoutMillis);

