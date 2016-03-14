/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.aries.samples.ariestrader.web.prims;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.aries.samples.ariestrader.api.TradeServiceUtilities;
import org.apache.aries.samples.ariestrader.api.TradeServices;
import org.apache.aries.samples.ariestrader.api.persistence.*;
import org.apache.aries.samples.ariestrader.util.*;

/**
 * 
 * PingJDBCReadPrepStmt uses a prepared statement for database read access. 
 * This primative uses {@link org.apache.aries.samples.ariestrader.direct.TradeJEEDirect} to set the price of a random stock
 * (generated by {@link org.apache.aries.samples.ariestrader.Trade_Config}) through the use of prepared statements. 
 * 
 */

public class PingJDBCRead extends HttpServlet
{

	private static String initTime;
	private static int hitCount;

	/**
	 * forwards post requests to the doGet method
	 * Creation date: (11/6/2000 10:52:39 AM)
	 * @param res javax.servlet.http.HttpServletRequest
	 * @param res2 javax.servlet.http.HttpServletResponse
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		doGet(req, res);
	}
	/**
	* this is the main method of the servlet that will service all get requests.
	* @param request HttpServletRequest
	* @param responce HttpServletResponce
	**/
	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		res.setContentType("text/html");
		java.io.PrintWriter out = res.getWriter();
		String symbol=null;
		StringBuffer output = new StringBuffer(100);

		try
			{
			//TradeJdbc (via TradeServices) uses prepared statements so I am going to make use of it's code.
                        TradeServices tradeServices = TradeServiceUtilities.getTradeServices("(mode=JDBC)");
			symbol = TradeConfig.rndSymbol();
			
			QuoteDataBean quoteData = null;
			int iter = TradeConfig.getPrimIterations();
			for (int ii = 0; ii < iter; ii++) {
				quoteData = tradeServices.getQuote(symbol);
			}

			output.append(
				"<html><head><title>Ping JDBC Read w/ Prepared Stmt.</title></head>"
					+ "<body><HR><FONT size=\"+2\" color=\"#000066\">Ping JDBC Read w/ Prep Stmt:</FONT><HR><FONT size=\"-1\" color=\"#000066\">Init time : "
					+ initTime);
                        hitCount++;
			output.append("<BR>Hit Count: " + hitCount);
			output.append(
				"<HR>Quote Information <BR><BR>: "
					+ quoteData.toHTML());
			output.append("<HR></body></html>");
			out.println(output.toString());
		}
		catch (Exception e)
		{
			Log.error(
				e,
				"PingJDBCRead w/ Prep Stmt -- error getting quote for symbol",
				symbol);
			res.sendError(500, "PingJDBCRead Exception caught: " + e.toString());
		}

	}
	/** 
	 * returns a string of information about the servlet
	 * @return info String: contains info about the servlet
	 **/
	public String getServletInfo()
	{
		return "Basic JDBC Read using a prepared statment, makes use of TradeJdbc class via TradeServices";
	}
	/**
	* called when the class is loaded to initialize the servlet
	* @param config ServletConfig:
	**/
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		hitCount = 0;
		initTime = new java.util.Date().toString();
	}
}