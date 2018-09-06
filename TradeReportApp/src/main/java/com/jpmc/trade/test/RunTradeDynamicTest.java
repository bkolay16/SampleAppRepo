package com.jpmc.trade.test;

import java.util.Scanner;

import com.jpmc.trade.instruction.ProcessInstruction;
import com.jpmc.trade.instruction.ProcessInstructionImpl;

public class RunTradeDynamicTest {

	private static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		ProcessInstruction processInstr = new ProcessInstructionImpl();
		
		
		while(true)
		{
			
	          String input =		scanner.nextLine();
	          if(input.equals("report"))
	          {
	        	  processInstr.generateReport();
	          }
	          else
	          {
		          try {
					processInstr.processInstruction(input);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }
			
		}
		
	}
}
