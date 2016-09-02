/*******************************************************************************
 * Copyright 2016 Infostretch Corporation.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qmetry.qaf.automation.integration.qmetry.qmetry6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * The Class QmetryRestWrapper.
 */
public class QmetryRestWrapper {

	/**
	 * Field responseCode.
	 */
	private int responseCode;
	/**
	 * Field responseMessage.
	 */
	private String responseMessage;
	/**
	 * The response.
	 */
	private String response;
	protected URL url;
	protected OutputStream out;

	/**
	 * Method doRequest.
	 * 
	 * @param uri
	 *            String Connection URL
	 * @param method
	 *            String HTTP method, GET/POST/PUT/DELETE etc.
	 * @param requestHeaders
	 *            Map<String,String>
	 * @param input
	 *            String
	 * @return String
	 */
	public String doRequest(String uri, String method, Map<String, String> requestHeaders, String input) // $codepro.audit.disable
	{

		String resp = "";
		try {
			final URL urlToConnect = new URL(uri);

			final HttpURLConnection conn = (HttpURLConnection) urlToConnect.openConnection();
			conn.setRequestMethod(method.toString());
			conn.setRequestProperty("Accept-Charset", "utf-8");
			if (null != requestHeaders) {
				for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, String> entry = it.next();
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}

			}

			OutputStream output;
			if (null != input) {
				conn.setDoOutput(true);
				try {
					output = conn.getOutputStream();
					output.write(input.getBytes());
					output.flush();
				} catch (IOException e) {
					System.out.println(e.getStackTrace());
				}
			}

			responseCode = conn.getResponseCode();
			responseMessage = conn.getResponseMessage();
			System.out.println(method + " " + uri + " " + responseCode + " " + responseMessage);

			BufferedReader br = null;
			final StringBuilder sb = new StringBuilder(32);

			String line;
			try {

				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				do {
					line = br.readLine();
					if (null != line) {
						sb.append(line);
					}
				} while (null != line);
			} catch (IOException e) {
				br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
				do {
					line = br.readLine();
					if (null != line) {
						sb.append(line);
					}
				} while (null != line);

				System.out.println(e.getStackTrace());
			} finally {
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println(e.getStackTrace());
					}
				}
			}

			resp = sb.toString();
			response = resp;
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.out.println(e.getStackTrace());
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
		return resp;
	}

	/**
	 * Method getResponseCode.
	 * 
	 * @return int
	 */
	public final int getResponseCode() {
		return responseCode;
	}

	/**
	 * Method getResponseMessage.
	 * 
	 * @return String
	 */
	public final String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Gets the response.
	 * 
	 * @return the response
	 */
	public final String getResponse() {
		return response;
	}
}
