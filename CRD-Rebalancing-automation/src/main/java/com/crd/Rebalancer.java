package com.crd;

public class Rebalancer {
	
	//calculate variance (current - target)
	
	public static double calculateVariance(double targetPct, double currentPct) {
	       return currentPct - targetPct;
	}
	
	
	//calculate No.of shares 
	
	public static int calculateShares(double totalAssets, double targetPct, double currentPct, double unitPrice) {
		    if (unitPrice <= 0) {
		    	throw new IllegalArgumentException("Price must  be greater than 0");	
		    }
		    
		    if (targetPct <0 || currentPct <0) {
		    	throw new IllegalArgumentException("Percentages cannot be negative");
		    }
		    
		    if (totalAssets < 0) {
		    	throw new IllegalArgumentException("Total Assets cannot be negative");
		    }
		    
		    if (targetPct > 100 || currentPct > 100) {
		    	throw new IllegalArgumentException("Percentages cannot exceed 100");
		    }
		
		    double targetValue = totalAssets * (targetPct/100);
		    double currentValue = totalAssets * (currentPct/100);
		    
		    double difference = targetValue - currentValue;
		    double rawShares = difference / unitPrice;
		    
		    if (rawShares > 0) {
		    	return (int) Math.floor(rawShares);
		    } else if (rawShares < 0) {
		    	return (int) Math.ceil(rawShares);
		    } else {
		    	return 0;
		    }
	}
	
	//rebalance decision
	
	
	public static String rebalancingDecision(double targetPct, double currentPct) {
		    double variance = calculateVariance(targetPct, currentPct);
		    
		    if(variance < 0) {
		    	return "BUY";
		    } else if (variance > 0) {
		    	return "SELL";
		    } else {
		    	return "HOLD";
	    }
		    	
	};
		    		
		
	
	
	
	

}
