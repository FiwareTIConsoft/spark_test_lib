package com.tilab.ca.spark_test_lib.batch;

import com.tilab.ca.spark_test_lib.streaming.interfaces.SparkJob;
import org.apache.spark.api.java.JavaSparkContext;

import com.tilab.ca.spark_test_lib.streaming.interfaces.SparkVoidJob;
import com.tilab.ca.spark_test_lib.streaming.interfaces.TestResContainer;

public class SparkTestBatchExecutor{

	private JavaSparkContext jsc;
	private SparkJob sj;
	private SparkVoidJob svj;
	private TestResContainer trc;
	
	
	public SparkTestBatchExecutor(JavaSparkContext jsc){
		this.jsc=jsc;
	}
	
        
        public SparkTestBatchExecutor sparkTest(SparkJob sj){
            this.sj=sj;
	    return this;
        }
        
        public SparkTestBatchExecutor sparkTest(SparkVoidJob svj){
            this.svj=svj;
	    return this;
        }
        
        public SparkTestBatchExecutor test(TestResContainer tc){
		this.trc=tc;
		return this;
	}
        
        public void execute(){
            System.out.println("Starting test..");
            long startTime=System.currentTimeMillis();
            if(sj!=null && trc!=null){
                Object res=sj.execute(jsc);
                System.out.println("Spark job time elapsed: "+(System.currentTimeMillis()-startTime)+" ms");
                trc.execute(res);
            }else if(svj!=null){
                svj.execute(jsc);
            }else{
                throw new IllegalStateException("Expected one between sparkJob+testResContainer or sparkVoidJob");
            }
            System.out.println("Spark total time elapsed: "+(System.currentTimeMillis()-startTime)+" ms");
            System.out.println("End test..");
        }
}
