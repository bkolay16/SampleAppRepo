package com.jpmc.trade.junit.test;


import org.junit.Test;

import com.jpmc.trade.instruction.ProcessInstruction;
import com.jpmc.trade.instruction.ProcessInstructionImpl;

public class ProcessInstructionImplTest {



	@Test
	public void testGenerateReport() {
		ProcessInstruction processInstr = new ProcessInstructionImpl();
		try {
			processInstr.processInstruction("foo:B:0.50:SGP:01 Jan 2016:02 Jan 2016:200:100.25");
			processInstr.processInstruction("bar:S:0.22:AED:05 Jan 2016:07 Jan 2016:450:150.5");
			processInstr.processInstruction("comp1:S:0.22:AED:29 Aug 2018:31 Aug 2018:100:150.5");
			processInstr.processInstruction("comp2:S:0.22:AED:29 Aug 2018:31 Aug 2018:550:150.5");
			processInstr.processInstruction("bar:B:0.50:SGP:29 Aug 2018:31 Aug 2018:550:150.5");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		processInstr.generateReport();
	}

}
