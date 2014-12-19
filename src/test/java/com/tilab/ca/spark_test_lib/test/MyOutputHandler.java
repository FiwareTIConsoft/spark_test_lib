package com.tilab.ca.spark_test_lib.test;

import java.util.LinkedList;
import java.util.List;

import com.tilab.ca.spark_test_lib.streaming.interfaces.ExpectedOutputHandler;

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
