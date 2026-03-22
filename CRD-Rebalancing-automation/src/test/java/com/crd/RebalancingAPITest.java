package com.crd;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import java.util.List;

/**
 * REST Assured + TestNG API tests for the Portfolio Rebalancing API.
 *
 * Assumed endpoint:
 *   POST http://localhost:8080/api/rebalance
 *
 * Sample Request Body:
 * {
 *   "accountId"   : "ABC",
 *   "totalAssets" : 100000,
 *   "securities": [
 *     { "ticker": "IBM",  "targetPct": 20, "currentPct": 10, "unitPrice": 150 },
 *     { "ticker": "MSFT", "targetPct": 20, "currentPct": 20, "unitPrice": 90  },
 *     { "ticker": "ORCL", "targetPct": 20, "currentPct": 30, "unitPrice": 220 },
 *     { "ticker": "AAPL", "targetPct": 20, "currentPct": 20, "unitPrice": 450 },
 *     { "ticker": "HD",   "targetPct": 20, "currentPct": 20, "unitPrice": 70  }
 *   ]
 * }
 *
 * Sample Response Body:
 * {
 *   "accountId": "ABC",
 *   "results": [
 *     { "ticker": "IBM",  "shares": 66,  "action": "BUY"       },
 *     { "ticker": "MSFT", "shares": 0,   "action": "HOLD"      },
 *     { "ticker": "ORCL", "shares": -45, "action": "SELL"      },
 *     { "ticker": "AAPL", "shares": 0,   "action": "HOLD"      },
 *     { "ticker": "HD",   "shares": 0,   "action": "HOLD"      }
 *   ]
 * }
 */

public class RebalancingAPITest {

	 private static final String BASE_URL   = "http://localhost:8080";
	    private static final String ENDPOINT   = "/api/rebalance";
	    private static final String ACCOUNT_ID = "ABC";
	    
	    private static final String FULL_PORTFOLIO_PAYLOAD = """
	        {
	          "accountId"   : "ABC",
	          "totalAssets" : 100000,
	          "securities"  : [
	            { "ticker": "IBM",  "targetPct": 20, "currentPct": 10, "unitPrice": 150 },
	            { "ticker": "MSFT", "targetPct": 20, "currentPct": 20, "unitPrice": 90  },
	            { "ticker": "ORCL", "targetPct": 20, "currentPct": 30, "unitPrice": 220 },
	            { "ticker": "AAPL", "targetPct": 20, "currentPct": 20, "unitPrice": 450 },
	            { "ticker": "HD",   "targetPct": 20, "currentPct": 20, "unitPrice": 70  }
	          ]
	        }
	        """;
	        	
	    private Response portfolioResponse; 
	 
	    @BeforeClass
	    public void setup() {
	        RestAssured.baseURI = BASE_URL;
	        
	        portfolioResponse = RestAssured
	                .given()
	                    .contentType(ContentType.JSON)
	                    .body(FULL_PORTFOLIO_PAYLOAD)
	                .when()
	                    .post(ENDPOINT);
	        Assert.assertEquals(
	                portfolioResponse.statusCode(), 200,
	                "Rebalance API should return HTTP 200 — check endpoint and payload"
	        );
	    }
	        
	        
	        @DataProvider(name = "securitiesData")
	        public Object[][] securitiesData() {
	            return new Object[][] {
	                
	                { "IBM",  "BUY",  66 },
	                { "MSFT", "HOLD",  0 },
	                { "ORCL", "SELL", 45 },
	                { "AAPL", "HOLD",  0 },
	                { "HD",   "HOLD",  0 }
	            };
	        }
	        
	        @Test(dataProvider = "securitiesData",
	                description  = "Validate action and share count for each security in Account ABC portfolio")
	          public void testEachSecurityActionAndShares(
	                  String ticker, String expectedAction, int expectedShares) {
	       

	              List<String> tickers = portfolioResponse.jsonPath().getList("results.ticker");
	              int index = tickers.indexOf(ticker);

	              Assert.assertTrue(index != -1, "Ticker not found: " + ticker);

	              String actualAction = portfolioResponse.jsonPath().getString("results[" + index + "].action");
	              int actualShares = portfolioResponse.jsonPath().getInt("results[" + index + "].shares");
	       

	              Assert.assertEquals(
	                      actualAction, expectedAction,
	                      ticker + ": action mismatch"
	              );
	              Assert.assertEquals(
	                      actualShares, expectedShares,
	                      ticker + ": share count mismatch — check variance and floor rounding"
	              );
	    }
	        
	        @Test
	        public void testInvalidRequest() {
	            String badPayload = """
	            {
	              "accountId": "ABC",
	              "totalAssets": -100
	            }
	            """;

	            given()
	                .contentType(ContentType.JSON)
	                .body(badPayload)
	            .when()
	                .post(ENDPOINT)
	            .then()
	                .statusCode(400);
	        }
	
}
