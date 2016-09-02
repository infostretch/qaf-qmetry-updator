/*******************************************************************************
 * QMetry Automation Framework provides a powerful and versatile platform to author 
 * Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven approach
 *                
 * Copyright 2016 Infostretch Corporation
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 *
 * You should have received a copy of the GNU General Public License along with this program in the name of LICENSE.txt in the root folder of the distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 *
 * See the NOTICE.TXT file in root folder of this source files distribution 
 * for additional information regarding copyright ownership and licenses
 * of other open source software / files used by QMetry Automation Framework.
 *
 * For any inquiry or need additional information, please contact support-qaf@infostretch.com
 *******************************************************************************/

package com.qmetry.qaf.automation.integration.qmetry.qmetry6.patch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qmetry.qaf.automation.integration.TestCaseRunResult;
import com.qmetry.qaf.automation.util.StringUtil;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.sun.jersey.api.client.WebResource.Builder;

public class QMetryRestWebservice {

	private String baseURL = null;
	private String token = null;

	/*
	 * update test case result
	 */
	public boolean executeTestCase(String baseURL, String token, String projectID, String releaseID, String cycleID,
			String testSuiteID, String testSuiteRunID, String testCaseID, String platformID, String dropID,
			String qmetryResult) {
		this.baseURL = baseURL;
		this.token = token;
		Map<String, String> requestHeaders = null;

		requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", projectID + ":" + releaseID + ":" + cycleID);
		if (testSuiteRunID == null || StringUtil.isBlank(testSuiteRunID)) {
			testSuiteRunID = getTestSuiteRunID(requestHeaders, testSuiteID, platformID);
		}
		String tcRunID = getTestCaseRunID(requestHeaders, testSuiteRunID, testCaseID);
		if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.PASS.toQmetry6())) {
			updateStatus(requestHeaders, tcRunID, testSuiteRunID, dropID, TestCaseRunResult.PASS);
			return true;
		} else if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.FAIL.toQmetry6())) {
			updateStatus(requestHeaders, tcRunID, testSuiteRunID, dropID, TestCaseRunResult.FAIL);
			return true;
		} else if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.SKIPPED.toQmetry6())) {
			updateStatus(requestHeaders, tcRunID, testSuiteRunID, dropID, TestCaseRunResult.SKIPPED);
			return true;
		}
		return false;
	}

	public boolean executeTestCaseUsingRunId(String baseURL, String token, String projectID, String releaseID,
			String cycleID, String testSuiteId, String testSuiteRunID, String tcRunID, String dropID, String plaformId,
			String qmetryResult) {
		this.baseURL = baseURL;
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", projectID + ":" + releaseID + ":" + cycleID);
		if (testSuiteRunID == null || StringUtil.isBlank(testSuiteRunID)) {
			testSuiteRunID = getTestSuiteRunID(requestHeaders, testSuiteId, plaformId);
		}
		if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.PASS.toQmetry6())) {
			updateStatus(requestHeaders, tcRunID, testSuiteRunID, dropID, TestCaseRunResult.PASS);
			return true;
		} else if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.FAIL.toQmetry6())) {
			updateStatus(requestHeaders, tcRunID, testSuiteRunID, dropID, TestCaseRunResult.FAIL);
			return true;
		} else if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.SKIPPED.toQmetry6())) {
			updateStatus(requestHeaders, tcRunID, testSuiteRunID, dropID, TestCaseRunResult.SKIPPED);
			return true;
		}
		return false;

	}

	/*
	 * get test suite run id from test suite id
	 */
	public String getTestSuiteRunID(Map<String, String> reqHeaders, String testSuiteID, String platformID) {

		JsonObject objTestSuiteRunID = new JsonObject();
		objTestSuiteRunID.addProperty("entityId", testSuiteID);
		objTestSuiteRunID.addProperty("id", testSuiteID);
		objTestSuiteRunID.addProperty("type", "TS");

		JsonObject jsonObjTestSuiteRunID = new JsonObject();
		jsonObjTestSuiteRunID.addProperty("entityId", testSuiteID);
		jsonObjTestSuiteRunID.addProperty("scope", "cycle");
		jsonObjTestSuiteRunID.addProperty("tsID", testSuiteID);
		jsonObjTestSuiteRunID.add("linkedAsset", objTestSuiteRunID);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/execution/list").getRequestBuilder();

		builder.header("Accept-Charset", "utf-8");
		if (null != reqHeaders) {
			for (Iterator<Map.Entry<String, String>> it = reqHeaders.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				builder.header(entry.getKey(), entry.getValue());
			}

		}
		String resultTestsuiteRunID = builder.post(String.class, jsonObjTestSuiteRunID.toString());
		JsonObject jsonTestSuite = new Gson().fromJson(resultTestsuiteRunID, JsonElement.class).getAsJsonObject();

		JsonArray arrTestcaseRunID = jsonTestSuite.get("data").getAsJsonArray();

		for (JsonElement jsonElement : arrTestcaseRunID) {
			if (jsonElement.getAsJsonObject().get("platformID").getAsString().equalsIgnoreCase(platformID)) {
				return jsonElement.getAsJsonObject().get("tsRunID").getAsString();
			}
		}
		System.err.println("Not Found");
		return null;
	}

	/*
	 * get test suite run id from test case id
	 */
	public String getTestCaseRunID(Map<String, String> reqHeaders, String testSuiteRunID, String testCaseID) {

		JsonObject objTestCaseRunID = new JsonObject();
		objTestCaseRunID.addProperty("limit", 200);
		objTestCaseRunID.addProperty("page", 1);
		objTestCaseRunID.addProperty("scope", "cycle");
		objTestCaseRunID.addProperty("start", 0);
		objTestCaseRunID.addProperty("tsrID", testSuiteRunID);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/execution/list/tcr").getRequestBuilder();

		builder.header("Accept-Charset", "utf-8");
		if (null != reqHeaders) {
			for (Iterator<Map.Entry<String, String>> it = reqHeaders.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				builder.header(entry.getKey(), entry.getValue());
			}

		}
		String resultTestcaseRunID = builder.post(String.class, objTestCaseRunID.toString());

		JsonObject jsonTcs = new Gson().fromJson(resultTestcaseRunID, JsonElement.class).getAsJsonObject();

		JsonArray arrTCJson = jsonTcs.get("data").getAsJsonArray();

		for (JsonElement jsonElement : arrTCJson) {
			if (jsonElement.getAsJsonObject().get("tcID").toString().equalsIgnoreCase(testCaseID)) {
				return jsonElement.getAsJsonObject().get("tcRunID").getAsString();
			}
		}
		return null;
	}

	/*
	 * get run status id for related scope. ex for pass it may be 1000, for fail
	 * it may be 1001 etc.
	 */
	public String getRunStatusID(Map<String, String> reqHeaders, TestCaseRunResult status) {
		String scope = reqHeaders.get("scope");
		String[] values = scope.split(":");
		String domain = values[0];
		String release = values[1];
		String cycle = values[2];

		String userInfo = getInfoUser(domain, release, cycle);
		JsonObject jsonObject = new Gson().fromJson(userInfo, JsonObject.class);
		JsonArray array = jsonObject.get("allstatus").getAsJsonArray();

		Map<String, String> statusMap = new HashMap<String, String>();

		for (JsonElement element : array) {
			JsonObject statusObject = element.getAsJsonObject();
			statusMap.put(statusObject.get("name").getAsString(), statusObject.get("id").getAsString());
		}

		Iterator<String> runStatusItr = statusMap.keySet().iterator();
		while (runStatusItr.hasNext()) {

			String statusName = runStatusItr.next();
			if (statusName.equalsIgnoreCase(status.toQmetry6())) {
				return statusMap.get(statusName);
			}
		}
		return null;
	}

	/*
	 * get user related all information using QMetry 6 rest api
	 */
	private String getInfoUser(String domain, String release, String cycle) {
		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/admin/project/getinfo").getRequestBuilder();
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", domain + ":" + release + ":" + cycle);
		if (null != requestHeaders) {
			for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				builder.header(entry.getKey(), entry.getValue());
			}

		}
		return builder.get(String.class);
	}

	/*
	 * put updated result for particular testcase and it will update result in
	 * QMetry
	 */
	public void updateStatus(Map<String, String> reqHeaders, String testCaseRunID, String testSuiteRunID, String dropID,
			TestCaseRunResult executionStatus) {
		JsonObject jsonTCBulkUpdate = new JsonObject();

		jsonTCBulkUpdate.addProperty("dropID", dropID);
		jsonTCBulkUpdate.addProperty("entityIDs", testCaseRunID);
		jsonTCBulkUpdate.addProperty("entityType", "TCR");
		jsonTCBulkUpdate.addProperty("qmTsRunId", testSuiteRunID);
		jsonTCBulkUpdate.addProperty("qmRunObj", "");
		String runStatus = getRunStatusID(reqHeaders, executionStatus);
		jsonTCBulkUpdate.addProperty("runStatusID", runStatus);

		Map<String, String> requestHeaders = new LinkedHashMap<String, String>();
		requestHeaders.putAll(reqHeaders);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/execution/runstatus/bulkupdate")
				.getRequestBuilder();

		if (null != requestHeaders) {
			for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				builder.header(entry.getKey(), entry.getValue());
			}

		}
		builder.put(jsonTCBulkUpdate.toString());
	}

	/**
	 * attach log using run id
	 * 
	 * @param token
	 *            - token generate using username and password
	 * @param projectID
	 * @param releaseID
	 * @param cycleID
	 * @param testCaseRunId
	 * @param filePath
	 *            - absolute path of file to be attached
	 * @return
	 */
	public int attachTestLogsUsingRunId(String token, String projectID, String releaseID, String cycleID,
			long testCaseRunId, File filePath) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

			final String CurrentDate = format.format(new Date());
			Path path = Paths.get(filePath.toURI());
			byte[] outFileArray = Files.readAllBytes(path);

			if (outFileArray != null) {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				try {
					HttpPost httppost = new HttpPost(baseURL + "/rest/attachments/testLog");

					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

					FileBody bin = new FileBody(filePath);
					builder.addTextBody("desc", "Attached on " + CurrentDate, ContentType.TEXT_PLAIN);
					builder.addTextBody("type", "TCR", ContentType.TEXT_PLAIN);
					builder.addTextBody("entityId", String.valueOf(testCaseRunId), ContentType.TEXT_PLAIN);
					builder.addPart("file", bin);

					HttpEntity reqEntity = builder.build();
					httppost.setEntity(reqEntity);
					httppost.addHeader("usertoken", token);
					httppost.addHeader("scope", projectID + ":" + releaseID + ":" + cycleID);

					CloseableHttpResponse response = httpclient.execute(httppost);
					String str = null;
					try {
						str = EntityUtils.toString(response.getEntity());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
					}
					JsonElement gson = new Gson().fromJson(str, JsonElement.class);
					JsonElement data = gson.getAsJsonObject().get("data");
					int id = Integer.parseInt(data.getAsJsonArray().get(0).getAsJsonObject().get("id").toString());
					return id;
				} finally {
					httpclient.close();
				}
			} else {
				System.out.println(filePath + " file does not exists");
			}
		} catch (Exception ex) {
			System.out.println("Error in attaching file - " + filePath);
			System.out.println(ex.getMessage());
		}
		return 0;
	}
}
