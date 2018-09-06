package com.jpmc.trade.instruction;

public interface ProcessInstruction {
	
	public void processInstruction(String instructionStr) throws Exception;
	public void generateReport();

}
