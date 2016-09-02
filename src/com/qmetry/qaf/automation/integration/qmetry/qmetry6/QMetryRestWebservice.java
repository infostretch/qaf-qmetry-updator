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

package com.qmetry.qaf.automation.integration.qmetry.qmetry6;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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

/**
 * @author anjali.bangoriya
 *
 */
public class QMetryRestWebservice {

	private String baseURL = null;
	private String token = null;
	private String userInfo = null;
	private String scope;

	/**
	 * @param baseURL
	 * @param token
	 * @param scope
	 * @param testSuiteID
	 * @param testSuiteRunID
	 * @param testCaseID
	 * @param platformID
	 * @param dropID
	 * @param qmetryResult
	 * 
	 * @return status of test case execution whether it has updated or not.
	 * 
	 *         update test case result using test case id.
	 */
	public boolean executeTestCase(String baseURL, String token, String scope, String testSuiteID,
			String testSuiteRunID, String testCaseID, String platformID, String dropID, String qmetryResult) {
		this.baseURL = baseURL;
		this.token = token;
		this.scope = scope;
		this.userInfo = getInfoUser(scope);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		if (testSuiteRunID == null || StringUtil.isBlank(testSuiteRunID)) {
			testSuiteRunID = getTestSuiteRunID(requestHeaders, testSuiteID, platformID);
		}

		// get test case run id using test case id
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

	/**
	 * @param baseURL
	 * @param token
	 * @param scope
	 * @param testSuiteId
	 * @param testSuiteRunID
	 * @param tcRunID
	 * @param dropID
	 * @param plaformId
	 * @param qmetryResult
	 * 
	 * @return status of test case execution whether it has updated or not.
	 * 
	 *         update test case result using test case run id
	 */
	public boolean executeTestCaseUsingRunId(String baseURL, String token, String scope, String testSuiteId,
			String testSuiteRunID, String tcRunID, String dropID, String plaformId, String qmetryResult) {
		this.baseURL = baseURL;
		this.token = token;
		this.scope = scope;
		this.userInfo = getInfoUser(scope);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "*/*; charset=UTF-8");
		requestHeaders.put("Accept", "*/*");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

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

	/**
	 * @param reqHeaders
	 * @param testSuiteID
	 * @param platformID
	 * @return test suite run id
	 * 
	 *         get test suite run id from test suite id
	 */
	public String getTestSuiteRunID(Map<String, String> reqHeaders, String testSuiteID, String platformID) {

		JsonObject objTestSuiteRunID = new JsonObject();
		objTestSuiteRunID.addProperty("entityId", testSuiteID);
		objTestSuiteRunID.addProperty("id", testSuiteID);
		objTestSuiteRunID.addProperty("type", "TS");

		// create test suite entity
		JsonObject jsonObjTestSuiteRunID = new JsonObject();
		jsonObjTestSuiteRunID.addProperty("entityId", testSuiteID);
		jsonObjTestSuiteRunID.addProperty("scope", "cycle");
		jsonObjTestSuiteRunID.addProperty("tsID", testSuiteID);
		jsonObjTestSuiteRunID.add("linkedAsset", objTestSuiteRunID);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/execution/list").getRequestBuilder();
		// add header
		builder.header("Accept-Charset", "utf-8");
		for (Iterator<Map.Entry<String, String>> it = reqHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		// get test suite run id response
		String resultTestsuiteRunID = builder.post(String.class, jsonObjTestSuiteRunID.toString());

		// get suite run id from json
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

	/**
	 * @param reqHeaders
	 * @param testSuiteRunID
	 * @param testCaseID
	 * 
	 * @return testCaseRunId
	 * 
	 *         get test case run id from test case id
	 */
	/**
	 * @param reqHeaders
	 * @param testSuiteRunID
	 * @param testCaseID
	 * @return
	 */
	public String getTestCaseRunID(Map<String, String> reqHeaders, String testSuiteRunID, String testCaseID) {

		JsonObject objTestCaseRunID = new JsonObject();
		objTestCaseRunID.addProperty("limit", 200);
		objTestCaseRunID.addProperty("page", 1);
		objTestCaseRunID.addProperty("scope", "cycle");
		objTestCaseRunID.addProperty("start", 0);
		objTestCaseRunID.addProperty("tsrID", testSuiteRunID);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/execution/list/tcr").getRequestBuilder();
		// add header
		builder.header("Accept-Charset", "utf-8");
		for (Iterator<Map.Entry<String, String>> it = reqHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		// get test case run id response
		String resultTestcaseRunID = builder.post(String.class, objTestCaseRunID.toString());

		// return test case run id from json response
		JsonObject jsonTcs = new Gson().fromJson(resultTestcaseRunID, JsonElement.class).getAsJsonObject();
		JsonArray arrTCJson = jsonTcs.get("data").getAsJsonArray();
		for (JsonElement jsonElement : arrTCJson) {
			if (jsonElement.getAsJsonObject().get("tcID").toString().equalsIgnoreCase(testCaseID)) {
				return jsonElement.getAsJsonObject().get("tcRunID").getAsString();
			}
		}
		return null;
	}

	/**
	 * @param status
	 * @return statusName
	 * 
	 *         get run status id for related scope. ex for pass it may be 1000,
	 *         for fail it may be 1001 etc.
	 */
	public String getRunStatusID(TestCaseRunResult status) {
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

	/**
	 * @param scope
	 * @return
	 * 
	 * 		get user related all information using QMetry 6 rest api
	 */
	private String getInfoUser(String scope) {
		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/admin/project/getinfo").getRequestBuilder();
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);
		// add headers
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		return builder.get(String.class);
	}

	/**
	 * @param reqHeaders
	 * @param testCaseRunID
	 * @param testSuiteRunID
	 * @param dropID
	 * @param executionStatus
	 * 
	 *            it will update status of test case.
	 */
	public void updateStatus(Map<String, String> reqHeaders, String testCaseRunID, String testSuiteRunID, String dropID,
			TestCaseRunResult executionStatus) {
		JsonObject jsonTCBulkUpdate = new JsonObject();

		jsonTCBulkUpdate.addProperty("dropID", dropID);
		jsonTCBulkUpdate.addProperty("entityIDs", testCaseRunID);
		jsonTCBulkUpdate.addProperty("entityType", "TCR");
		jsonTCBulkUpdate.addProperty("qmTsRunId", testSuiteRunID);
		jsonTCBulkUpdate.addProperty("qmRunObj", "");

		String runStatus = getRunStatusID(executionStatus);
		jsonTCBulkUpdate.addProperty("runStatusID", runStatus);

		// add header
		Map<String, String> requestHeaders = new LinkedHashMap<String, String>();
		requestHeaders.putAll(reqHeaders);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/execution/runstatus/bulkupdate")
				.getRequestBuilder();
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		builder.put(jsonTCBulkUpdate.toString());
	}

	/**
	 * attach log using run id
	 * 
	 * @param token
	 *            - token generate using username and password
	 * @param scope
	 *            : project:release:cycle
	 * @param testCaseRunId
	 * @param filePath
	 *            - absolute path of file to be attached
	 * @return
	 */
	public int attachTestLogsUsingRunId(long testCaseRunId, File filePath) {
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
					builder.addTextBody("desc", "Attached on " + CurrentDate,
							org.apache.http.entity.ContentType.TEXT_PLAIN);
					builder.addTextBody("type", "TCR", org.apache.http.entity.ContentType.TEXT_PLAIN);
					builder.addTextBody("entityId", String.valueOf(testCaseRunId),
							org.apache.http.entity.ContentType.TEXT_PLAIN);
					builder.addPart("file", bin);

					HttpEntity reqEntity = builder.build();
					httppost.setEntity(reqEntity);
					httppost.addHeader("usertoken", token);
					httppost.addHeader("scope", scope);

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

	/**
	 * @param baseURL
	 * @param token
	 *            - token generate using username and password
	 * @param scope
	 *            : project:release:cycle
	 * @param scriptName
	 *            :method qualifier name
	 * @return
	 */
	public List<Long> searchExistsTestCase(String baseURL, String token, String scope, String scriptName) {
		this.baseURL = baseURL;
		this.token = token;
		this.scope = scope;
		this.userInfo = getInfoUser(scope);

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "*/*");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/search/result").getRequestBuilder();
		// add header
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		// create entity
		Map<String, Object> filter = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();

		ArrayList<String> array = new ArrayList<String>();
		array.add("testScriptName");

		filter.put("op", "CN");
		filter.put("name", "keyword");
		filter.put("value", scriptName);
		filter.put("xtype", "keyword");
		filter.put("fields", array);

		filters.add(filter);

		Map<String, Object> entity = new HashMap<String, Object>();
		entity.put("entityType", "TC");
		entity.put("searchMode", "basic");
		entity.put("scope", "Cycle");
		entity.put("filters", filters);

		String response = builder.post(String.class, entity.toString());
		List<Long> tcIds = new ArrayList<Long>();
		if (!StringUtil.isNullOrEmpty(response)) {
			JsonObject searchResult = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (searchResult.get("data").getAsJsonArray().size() > 0) {
				for (JsonElement element : searchResult.get("data").getAsJsonArray()) {
					if (element.getAsJsonObject().get("testScriptName").getAsString().equals(scriptName)) {
						tcIds.add(element.getAsJsonObject().get("tcID").getAsLong());
					}
				}
			}
		}
		return tcIds;
	}

	/**
	 * @param testCaseId
	 * @return test suite id
	 * 
	 */
	public String searchExistSuiteUsingTCID(String testCaseId) {

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		Builder builder = new RestTestBase().getWebResource(baseURL, "/rest/testsuites/list").getRequestBuilder();
		// add headers
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		JsonObject linkedAsset = new JsonObject();
		linkedAsset.addProperty("type", "TC");
		linkedAsset.addProperty("id", testCaseId);

		JsonObject listTSreq = new JsonObject();
		listTSreq.add("linkedAsset", linkedAsset);
		listTSreq.addProperty("scope", "cycle");

		String response = builder.post(String.class, listTSreq.toString());
		JsonObject testSuitesResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		for (JsonElement element : testSuitesResponse.get("data").getAsJsonArray()) {
			if (element.getAsJsonObject().has("tsID"))
				return element.getAsJsonObject().get("tsID").getAsString();
		}
		return null;

	}

	/**
	 * create test suite in defined scope with random name
	 * 
	 * @param testSuiteFolderId
	 * 
	 */
	public JsonObject createTestSuite(String testSuiteFolderId) {
		String randomizer = new Date().toString();

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "*/*");
		requestHeaders.put("Accept", "*/*");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		JsonObject createTsReq = new JsonObject();
		createTsReq.addProperty("parentFolderId", testSuiteFolderId);
		createTsReq.addProperty("name", "AutoTestCase" + randomizer);
		createTsReq.addProperty("description", "Auto Generated Test Suite by QMetryResultUpdater");

		Builder tsBuilder = new RestTestBase().getWebResource(baseURL, "/rest/testsuites/").getRequestBuilder();

		// add headers
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			tsBuilder.header(entry.getKey(), entry.getValue());
		}

		String response = tsBuilder.post(String.class, createTsReq.toString());
		JsonObject testSuitesResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		return testSuitesResponse;

	}

	/**
	 * @param scriptName
	 * @param platform
	 * @param folderId
	 * @return testCaseResponse
	 * 
	 *         gives newly created test case response
	 */
	public JsonObject createTestCase(String scriptName, String platform, String folderId) {
		// request header
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		// create testcase using method fully qualifier name
		Builder tcBuidler = new RestTestBase().getWebResource(baseURL, "/rest/testcases/").getRequestBuilder();

		// add request header
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			tcBuidler.header(entry.getKey(), entry.getValue());
		}
		JsonObject infoUSer = new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();

		JsonElement customListsDefaults = infoUSer.get("customLists");
		String testingType = "";

		// get test case testing type
		if (!customListsDefaults.isJsonNull() && customListsDefaults.getAsJsonObject().has("testingType")) {
			Set<Entry<String, JsonElement>> keys = customListsDefaults.getAsJsonObject().get("testingType")
					.getAsJsonObject().entrySet();
			Iterator<Entry<String, JsonElement>> testingTypeItr = keys.iterator();
			while (testingTypeItr.hasNext()) {
				Entry<String, JsonElement> testingTypes = testingTypeItr.next();
				String value = testingTypes.getValue().getAsString();
				if (value.equalsIgnoreCase("Automated")) {
					testingType = testingTypes.getKey();
					break;
				}
			}
		} else {
			System.err.println("Custom Lists Defaults does not exists in user info");
		}

		// create testcase entity
		String randomizer = new Date().toString();
		JsonObject createTcReq = new JsonObject();
		createTcReq.addProperty("tcVersionID", 1);
		createTcReq.addProperty("tcFolderID", folderId);
		createTcReq.addProperty("tcID", 0);
		createTcReq.addProperty("scope", "cycle");
		createTcReq.addProperty("name", "AutoTestCase" + randomizer);
		createTcReq.addProperty("testScriptName", scriptName);
		createTcReq.addProperty("testingType", testingType);
		// post action to create new testcase
		String response = tcBuidler.post(String.class, createTcReq.toString());
		JsonObject testCaseResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
		return testCaseResponse;
	}

	/**
	 * @param testSuiteId
	 * @param scope
	 * @param platform
	 * 
	 *            it will link suite with given paltform
	 */
	public void linkPlatform(String testSuiteId, String platform) {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "*/*");
		requestHeaders.put("Accept", "*/*");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		JsonObject linkPlatformRequest = new JsonObject();
		linkPlatformRequest.addProperty("qmTsId", testSuiteId);
		linkPlatformRequest.addProperty("qmPlatformId", platform);

		Builder linkPlatformBuilder = new RestTestBase().getWebResource(baseURL, "/rest/testsuites/link/platforms")
				.getRequestBuilder();

		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			linkPlatformBuilder.header(entry.getKey(), entry.getValue());
		}

		linkPlatformBuilder.put(linkPlatformRequest.toString());

	}

	/**
	 * @param testSuiteId
	 * @param testCaseKey
	 * 
	 *            it will link test case with given suite
	 */
	public void linkTestCaseWithSuite(String testSuiteKey, String testCaseKey) {

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "*/*");
		requestHeaders.put("Accept", "*/*");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		JsonObject linkTcReq = new JsonObject();
		linkTcReq.addProperty("tsIDs", testSuiteKey);
		linkTcReq.addProperty("tcID", testCaseKey);

		Builder testCaseBuilder = new RestTestBase().getWebResource(baseURL, "/rest/testcases/link/testsuites")
				.getRequestBuilder();

		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseBuilder.header(entry.getKey(), entry.getValue());
		}
		testCaseBuilder.put(linkTcReq.toString());
	}

	/**
	 * @param testCaseId
	 * @param platform
	 * @param scope
	 * @return testCaseExecuteResponse
	 * 
	 *         it will automatically create test suite link test suite with
	 *         platform and testcase. Return suite response to get suite id
	 */
	public JsonObject executeTestCaseToGetTestSuiteId(String testCaseId, String platform) {

		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		JsonObject executeTCID = new JsonObject();
		executeTCID.addProperty("tcid", testCaseId);
		executeTCID.addProperty("platformId", platform);
		Builder testCaseBuilder = new RestTestBase().getWebResource(baseURL, "/rest/testcases/execute")
				.getRequestBuilder();
		// add headers
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseBuilder.header(entry.getKey(), entry.getValue());
		}

		String response = testCaseBuilder.post(String.class, executeTCID.toString());

		JsonObject testCaseExecuteResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		return testCaseExecuteResponse;
	}

	public String createTestCaseFolder() {
		String randomizer = new Date().toString();
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		Builder testCaseFolderBuilder = new RestTestBase().getWebResource(baseURL, "/rest/testcases/folders")
				.getRequestBuilder();

		JsonObject infoUSer = new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		JsonElement rootFolders = infoUSer.get("rootFolders");
		JsonElement testCase;
		int parentFolderId = 1;
		// get test case parent folder id
		if (!rootFolders.isJsonNull() && rootFolders.getAsJsonObject().has("TC")) {
			testCase = rootFolders.getAsJsonObject().get("TC");

			if (!testCase.isJsonNull() && testCase.getAsJsonObject().has("id"))
				parentFolderId = testCase.getAsJsonObject().get("id").getAsInt();
			else
				System.err.println("Test case id does not exists in user info");
		} else {
			System.err.println("Test case does not exists in user info");
		}

		JsonObject testCaseFolderEntity = new JsonObject();
		testCaseFolderEntity.addProperty("scope", "cycle");
		testCaseFolderEntity.addProperty("name", "Imported_" + randomizer);
		testCaseFolderEntity.addProperty("parentId", parentFolderId);
		// add headers
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseFolderBuilder.header(entry.getKey(), entry.getValue());
		}
		String response = null;
		try {
			response = testCaseFolderBuilder.post(String.class, testCaseFolderEntity.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonObject testCaseExecuteResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		for (JsonElement element : testCaseExecuteResponse.get("data").getAsJsonArray()) {
			if (element.getAsJsonObject().has("id"))
				return element.getAsJsonObject().get("id").getAsString();
		}
		// "description":"<Testsuite Folder Description>",
		// "testSuiteState":<Testsuite State>,
		// "owner":<Owner of the testsuite>,
		// "parentId":<root folder id or parent folder
		return response;

	}

	public String createTestSuiteFolder() {

		String randomizer = new Date().toString();
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);

		Builder testCaseFolderBuilder = new RestTestBase().getWebResource(baseURL, "/rest/testsuites/folders")
				.getRequestBuilder();

		JsonObject infoUSer = new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		JsonElement rootFolders = infoUSer.get("rootFolders");
		JsonElement testsuite;
		int parentFolderId = 1;
		// get test case parent folder id
		if (!rootFolders.isJsonNull() && rootFolders.getAsJsonObject().has("TS")) {
			testsuite = rootFolders.getAsJsonObject().get("TS");

			if (!testsuite.isJsonNull() && testsuite.getAsJsonObject().has("id"))
				parentFolderId = testsuite.getAsJsonObject().get("id").getAsInt();
			else
				System.err.println("Test Suite id does not exists in user info");
		} else {
			System.err.println("Test Suite does not exists in user info");
		}

		JsonObject testSuiteFolderEntity = new JsonObject();
		testSuiteFolderEntity.addProperty("scope", "cycle");
		testSuiteFolderEntity.addProperty("name", "Imported_" + randomizer);
		testSuiteFolderEntity.addProperty("parentId", parentFolderId);
		// add headers
		for (Iterator<Map.Entry<String, String>> it = requestHeaders.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseFolderBuilder.header(entry.getKey(), entry.getValue());
		}
		String response = null;
		try {
			response = testCaseFolderBuilder.post(String.class, testSuiteFolderEntity.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonObject testSuiteExecuteResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		for (JsonElement element : testSuiteExecuteResponse.get("data").getAsJsonArray()) {
			if (element.getAsJsonObject().has("id"))
				return element.getAsJsonObject().get("id").getAsString();
		}
		return response;

	}
}
