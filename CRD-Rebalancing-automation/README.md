CRD Portfolio Rebalancing — Technical Assessment:

Overview:

This project implements and tests a portfolio rebalancing engine for Account ABC, which holds 5 securities: IBM, MSFT, ORCL, AAPL, and HD.
The engine calculates how many shares of each security need to be bought or sold to eliminate the target variance and bring the portfolio back into balance.

Total Assets: 100,000

Core Formula:
variance = Current% - Target%
Dollar Delta = |variance| / 100 * total assets
Shares = Dollar Delta / unitCost

Assumptions:

1. No Fractional Shares. Share counts are rounded to whole numbers.

2. Unit price must be positive

3. Percentages must be between 0 and 100

4. Total assets must be non-negative

5. Residual cash is acceptable: Due to floor rounding, a small amount of cash may remain uninvested after rebalancing.(e.g. IBM: 66 × $150 = $9,900, not $10,000)

API Assumptions (RebalancingAPITest)

6. Assumed API endpoint, request and response contract for API tests, since no real API was provided for this exercise.

