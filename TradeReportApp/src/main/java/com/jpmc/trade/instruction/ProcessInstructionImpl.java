package com.jpmc.trade.instruction;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.jpmc.trade.util.DateUtil;
import com.jpmc.trade.vo.InstructionVO;

public class ProcessInstructionImpl implements ProcessInstruction{
	
	private InstructionHolder holder = InstructionHolder.getInstance();

	
	@Override
	public void processInstruction(String instructionStr) throws Exception{
		String[] instructionParts = instructionStr.split(":");  //considering input fields are specified by : separated
		if(instructionParts != null && instructionParts.length==8)
		{
			
			String entity = instructionParts[0];
			String instructionType = instructionParts[1];
			double agreedFx = Double.parseDouble(instructionParts[2]);
			String currency = instructionParts[3];
			Date instructionDate = DateUtil.getDate(instructionParts[4]);
			Date settlementDate = DateUtil.getDate(instructionParts[5]);
			Date finalSettlementDate = getValidatedSettlementDate(currency, settlementDate);
			int units = Integer.parseInt(instructionParts[6]);
			double unitPrice = Double.parseDouble(instructionParts[7]);
						
			InstructionVO instruction = new InstructionVO(entity,instructionType,agreedFx,currency,instructionDate,finalSettlementDate,units,unitPrice);
			
			holder.addInstrauction(instruction);
		}
		else
		{
			throw new Exception("invalid input");
		}
		
	}

	/**
	 * This method generates report for outgoing and incoming settlements for each day. Also reports the ranking of entities for outgoing and incoming instructions.
	 */
	@Override
	public void generateReport() {
		
		double totalSellValue =0;
		double totalBuyValue = 0;
		Map<Date,Double> incomingSettlementMap = new TreeMap<>();
		Map<Date,Double> outgoingSettlementMap = new TreeMap<>();
		Map<Double , String> incomingEntityMap = new TreeMap<>();
		Map<Double , String> outgoingEntityMap = new TreeMap<>();
		
		
		 for(InstructionVO instruction : holder.getInstructionList()){
			 double currentVal = 0;
			 double tradeVal = instruction.getAgreedFx()*instruction.getUnits() * instruction.getUnitPrice();
			if(instruction.getInstructionType().equalsIgnoreCase("S"))
			{
				
				if(incomingSettlementMap.containsKey(instruction.getSettlementDate()))
				{					
					 currentVal = incomingSettlementMap.get(instruction.getSettlementDate());
				}
				
				totalSellValue = currentVal + tradeVal;
				incomingSettlementMap.put(instruction.getSettlementDate(), totalSellValue);
				
				if(!outgoingSettlementMap.containsKey(instruction.getSettlementDate()))
				{
					outgoingSettlementMap.put(instruction.getSettlementDate(), 0d);
				}
			}
			else if(instruction.getInstructionType().equalsIgnoreCase("B"))
			{
			
				if(outgoingSettlementMap.containsKey(instruction.getSettlementDate()))
				{					
					 currentVal = outgoingSettlementMap.get(instruction.getSettlementDate());
				}
				
				totalBuyValue = currentVal + tradeVal;
				outgoingSettlementMap.put(instruction.getSettlementDate(), totalBuyValue);
				
				if(!incomingSettlementMap.containsKey(instruction.getSettlementDate()))
				{
					incomingSettlementMap.put(instruction.getSettlementDate(), 0d);
				}
			} 
		}
		
		 Comparator<InstructionVO> comp = new Comparator<InstructionVO>() {
				@Override
				public int compare(InstructionVO o1, InstructionVO o2) {
					// TODO Auto-generated method stub
					double o1TradeVal = o1.getAgreedFx()*o1.getUnitPrice()*o1.getUnits();
					double o2TradeVal = o2.getAgreedFx()*o2.getUnitPrice()*o2.getUnits();
					 if (o2TradeVal > o1TradeVal )
						return 1;
					else if (o2TradeVal < o1TradeVal)
						return -1;
					else
						return 0;
				}
			};
			
	List<InstructionVO> incomingInstructionList = holder.getInstructionList().stream().filter(inst -> inst.getInstructionType().equalsIgnoreCase("S")).collect(Collectors.toList());
	List<InstructionVO> outGoingInstructionList = holder.getInstructionList().stream().filter(inst -> inst.getInstructionType().equalsIgnoreCase("B")).collect(Collectors.toList());

			
	Collections.sort(incomingInstructionList, comp);
	Collections.sort(outGoingInstructionList, comp);
	
	
	List<String> sortedEnityListIncoming = incomingInstructionList.stream().map(InstructionVO:: getEntity).collect(Collectors.toList());
	List<String> sortedEnityListOutgoing = outGoingInstructionList.stream().map(InstructionVO:: getEntity).collect(Collectors.toList());
	
	Set<String> incomingEntitySet = new LinkedHashSet<>(sortedEnityListIncoming);
	Set<String> outgoingEntitySet = new LinkedHashSet<>(sortedEnityListOutgoing);
		
	System.out.println("****************Everyday incoming and outgoing settlement report******************");
	System.out.print("Actual settlement Date              ");
	System.out.print("Incoming Settled amount (USD)        ");
	System.out.println("Outgoing Settled amount (USD)");
	System.out.print("--------------------                ");
	System.out.print("---------------------------        ");
	System.out.println("-------------------------");
	
	for(Map.Entry<Date, Double> entry : incomingSettlementMap.entrySet())
	{
		System.out.print(DateUtil.getDateString(entry.getKey())+"                           ");
		System.out.print(entry.getValue()+"                                     ");
		System.out.println(outgoingSettlementMap.get(entry.getKey())+"              ");
	}
	   
	System.out.println("\nEntities ranking for incoming transactions ");
	System.out.println("-------------------------------------------");
	int i=0;
	for(String inst : incomingEntitySet)
	{
		i++;
	System.out.println(i  +"  "+ inst);
	}
	
	System.out.println("Entities ranking for outgoing transactions ");
	System.out.println("------------------------------------------");
	int j=0;
	for(String inst : outgoingEntitySet)
	{
		j++;
		System.out.println(j  +"  "+ inst  );
	}
		
	
	}
	
	/**
	 * This method validates the settlement date rule based on currency. If it falls in the week end, then changes the settlement date to next working day.
	 * @param currency
	 * @param settlementDate
	 * @return settlementDate
	 */
	private Date getValidatedSettlementDate(String currency , Date settlementDate)
	{
		Calendar cal = Calendar.getInstance();
		int dayOfWeek =0;
		switch(currency)
		{
		
		case "AED":
		case "SAR":
		
			cal.setTime(settlementDate);
			 dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			if(dayOfWeek == 6)
			   cal.add(Calendar.DATE, 2);
			else if(dayOfWeek == 7)
				cal.add(Calendar.DATE, 1);
			  settlementDate = cal.getTime();
			
			 break;
				
		default:
				
			cal.setTime(settlementDate);
			 dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			if(dayOfWeek == 7)
				  cal.add(Calendar.DATE, 2);
			else if(dayOfWeek == 1)
				cal.add(Calendar.DATE, 1);
			  settlementDate = cal.getTime();
			
			 break;
		
		}
		
		return settlementDate;
	}
	
}
