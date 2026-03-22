package com.crd;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RebalancerTest {
	
	//variance tests
	
	@Test (description = "Variance is negative when current% < target% → decision should be BUY")
	public void testVarianceNegativeBUY() {
		double variance = Rebalancer.calculateVariance(20, 10);
		Assert.assertTrue(variance < 0);
		Assert.assertEquals(Rebalancer.rebalancingDecision(20, 10), "BUY");
	}
	
	@Test (description = "Variance is positive when current% > target% → decision should be SELL")
	public void testVariancePositiveSELL() {
		double variance = Rebalancer.calculateVariance(20, 30);
		Assert.assertTrue(variance > 0);
		Assert.assertEquals(Rebalancer.rebalancingDecision(20, 30), "SELL");
	}
	
	
	@Test (description = "Variance is zero when current% == target% → decision should be HOLD")
	public void testVarianceZeroHOLD() {
		double variance = Rebalancer.calculateVariance(20, 20);
		Assert.assertEquals(variance, 0);
	    Assert.assertEquals(Rebalancer.rebalancingDecision(20, 20), "HOLD");
	}
	
	//Share calculation   
	@DataProvider(name = "rebalanceData")
    public Object[][] rebalanceData() {
        return new Object[][]{
                // totalAsset, target%, current%, price, expectedShares
                {100000, 20, 10, 150, 66},   // BUY
                {100000, 20, 30, 220, -45},  // SELL
                {100000, 20, 20, 90, 0},     // HOLD
                {100000, 20, 20, 450, 0},    // HOLD
                {100000, 20, 20, 70, 0}      // HOLD
        };
    }

    @Test(dataProvider = "rebalanceData", description  = "Validates share count for all 5 Account ABC securities")
    public void testShareCalculation(int totalAsset, double target, double current, double price, int expectedShares) {
        int shares = Rebalancer.calculateShares(totalAsset, target, current, price);

        System.out.println("Calculated shares: " + shares);
        Assert.assertEquals(shares, expectedShares, "Share calculation mismatch");
    }
    
	
	
	//Negative tests
	
	@Test (expectedExceptions = IllegalArgumentException.class)
	public void priceZero() {
		Rebalancer.calculateShares(100000, 20, 20, 0);	
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void targetPercentLessThanZero() {
		Rebalancer.calculateShares(100000, -20, 10, 100);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void currentPercentLessThanZero() {
		Rebalancer.calculateShares(100000, 20, -20, 100);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void totalAssetNegative() {
		Rebalancer.calculateShares(-100000, 20, 20, 100);
	}

	
	//Edge cases
	
    @Test
    public void testFractionalSharesRounding() {
        int shares = Rebalancer.calculateShares(100000, 21, 20, 350);
        Assert.assertEquals(shares, 2, "Floor rounding: expected 2 shares");
    }

    @Test
    public void testVerySmallVariance() {
        int shares = Rebalancer.calculateShares(100000, 20.01, 20, 100);
        Assert.assertEquals(shares, 0, "Small variance should not trigger trade");
    }

    @Test
    public void testLargePortfolio() {
        int shares = Rebalancer.calculateShares(1_000_000_000, 20, 10, 150);

        Assert.assertTrue(shares > 0, "Large portfolio calculation failed");
    }

    @Test
    public void testBoundaryValues() {
        int shares = Rebalancer.calculateShares(100000, 0, 0, 100);

        Assert.assertEquals(shares, 0, "Boundary condition failed");
    }
	
}
