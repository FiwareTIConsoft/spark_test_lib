How to use
================
<h2>Setting up the environment</h2>
To use this lib your test class must extend SparkTest class.
To provide configuration to the standalone test deploy there are 2 ways:
<ol>
<li>
<p>Through Annotations:<p>
	@SparkTestConfig(appName="GenericSparkTest",master="local[2]",
				 batchDurationMillis=1000,useManualClock=true)
<ul>
<li>**appName:** application name</li>
<li>**master:** spark master (choose you or use one provided by MasterTypes.class)</li>
<li>**batchDurationMillis:** virtual duration of the batch in milliseconds</li>
<li>**useManualClock:** use virtual clock in place of the system one (you don't have really to wait for the batch time)</li>
</ul>
</li>
<li>
<p>Through a simple dsl in the constructor class:<p>
	withMaster(MasterTypes.LOCAL_2_THREADS)
	.withAppName("GenericSparkTest")
	.withBatchDurationMillis(1000)
	.useManualClock();
</li>
</ol>

<h2>Generate fake DStream</h2>
<h2>Configure the test</h2>
<h3>ExpectedOutputHandler</h3>
You have to provide a class that implements this interface in order to save the output 
of the component to test and notify to sparkTest when the expected output is reached.

<h3>Create the mock Dstream</h3>
Spark test provides different helpers to create a mock DStream from a files that provide the content.
Example:
	TestStreamUtils.createMockDStream(<spark streaming context>, <num partitions>, <file1_path>,<file2_path>,..,<fileN_path>);
Where each file represent a dstream generated during a batch 

<h3>Setting the spark stream job</h3>
	.sparkStreamJob(
					(jssc) -> {
						//your spark stream job (jssc is the spark streaming context)
					}
			)
<h3>Setting the test</h3>
	.test(
			(eoh) -> {
				//your test goes here (eoh is an instance of your class implementing the ExpectedOutputHandler interface)
			}
		)
