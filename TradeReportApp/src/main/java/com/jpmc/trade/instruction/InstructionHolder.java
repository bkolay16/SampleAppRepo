package com.jpmc.trade.instruction;

import java.util.ArrayList;
import java.util.List;

import com.jpmc.trade.vo.InstructionVO;

public class InstructionHolder {
	
	private static volatile InstructionHolder instance = null;
	
	private List<InstructionVO> instructionList = new ArrayList<>();
	
	private InstructionHolder() {
		
	}

	/**
	 * This method creates singleton instance of this class. I have made it thread safe, although problem statement says i can assume
	 * single threaded environment, but there is no harm in it. It will be good for both single threaded and multithreaded environment.
	 * This will hold the instructions added from different client.
	 * @return InstructionHolder
	 */
	public static InstructionHolder getInstance()
	{
		if(instance == null)
		{
			synchronized(InstructionHolder.class) {
				if(instance == null)
				{
					instance = new InstructionHolder();
				}
			}
		}
		return instance;
	}
	
	public List<InstructionVO> getInstructionList(){
		return instructionList;
	}
	
	public void addInstrauction(InstructionVO instruction)
	{
		instructionList.add(instruction);
	}
	
	
}
