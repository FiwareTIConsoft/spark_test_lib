package com.tilab.ca.spark_test_lib.streaming.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tilab.ca.spark_test_lib.streaming.utils.MasterTypes;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SparkTestConfig {
	
	boolean useManualClock() default false;
	long batchDurationMillis() default 1000;
	String master() default MasterTypes.LOCAL_ALL_DISPOSABLE_THREADS;
	String appName() default "DefaultAppName";
}
