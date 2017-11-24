/*******************************************************************************
 * QMetry Automation Framework provides a powerful and versatile platform to
 * author
 * Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven
 * approach
 * Copyright 2016 Infostretch Corporation
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 * You should have received a copy of the GNU General Public License along with
 * this program in the name of LICENSE.txt in the root folder of the
 * distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 * See the NOTICE.TXT file in root folder of this source files distribution
 * for additional information regarding copyright ownership and licenses
 * of other open source software / files used by QMetry Automation Framework.
 * For any inquiry or need additional information, please contact
 * support-qaf@infostretch.com
 *******************************************************************************/

package com.qmetry.qaf.automation.integration.qmetry.qmetry7;

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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.Cookie;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qmetry.qaf.automation.integration.TestCaseRunResult;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.util.StringUtil;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * @author anjali.bangoriya
 */
public class QMetryRestWebservice {
	private Logger log;
	private String serviceUrl = null;
	private String token = null;
	private String userInfo = null;
	private String scope;
	private String user;
	private String password;
	private String prj;
	private String rel;
	private String cycle;
	private String build;
	private String platform;
	private String drop;
	private String suite;

	QMetryRestWebservice(String baseURL, String user, String password) {
		this.serviceUrl = baseURL;
		this.user = user;
		this.password = password;

		prj = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PRJ.getStringVal();
		rel = ApplicationProperties.INTEGRATION_PARAM_QMETRY_REL.getStringVal();
		cycle = ApplicationProperties.INTEGRATION_PARAM_QMETRY_CYCLE.getStringVal();
		build = ApplicationProperties.INTEGRATION_PARAM_QMETRY_BLD.getStringVal();
		platform = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM.getStringVal();
		drop = ApplicationProperties.INTEGRATION_PARAM_QMETRY_DROP.getStringVal();
		scope = prj + ":" + rel + ":" + cycle;
		log = Logger.getLogger(this.getClass());
		log.info("qmetry7 scheduled prj: " + prj + " rel : " + rel + " build: " + build
				+ " platform: " + platform + " drop: " + drop + "cycle:" + cycle);

	}

	public String login() {
		String authentication1 = "username=" + user + "&password=" + password;
		String response = null;
		try {
			response = new RestTestBase().getWebResource(serviceUrl, "/rest/login")
					.getRequestBuilder().header("Accept", "application/json")
					.header("Content-Type", "application/x-www-form-urlencoded")
					.post(String.class, authentication1);

			JsonObject jsonRequest =
					new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (jsonRequest.has("usertoken")) {
				token = jsonRequest.get("usertoken").getAsString();
				userInfo = getUserInfo();
				return token;
			} else {
				System.err.println(
						"QMetry credentials are invalid. Please contatct QMetry team");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public String getTCIDusingAttribute(String entityKey, String attribute) {

		String response = null;
		try {
			response = getAuthorizedBuilder("/rest/testcases/list").post(String.class,
					"{\"scope\":\"cycle\"}");

			JsonObject jsonRequest =
					new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (jsonRequest.has("data")) {
				JsonArray data = jsonRequest.get("data").getAsJsonArray();
				for (int i = 0; i < data.size(); i++) {
					JsonObject testCaseDetails = data.get(i).getAsJsonObject();
					if (testCaseDetails.has(attribute) && testCaseDetails.get(attribute)
							.getAsString().equalsIgnoreCase(entityKey))
						return testCaseDetails.get("tcID").getAsString();
				}
			} else {
				System.err.println(
						"QMetry credentials are invalid. Please contatct QMetry team");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entityKey;
	}

	public String getTSIDusingAttribute(String entityKey, String attribute) {

		String response = null;
		try {
			response = getAuthorizedBuilder("/rest/testsuites/list").post(String.class,
					"{\"scope\":\"cycle\"}");

			JsonObject jsonRequest =
					new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (jsonRequest.has("data")) {
				JsonArray data = jsonRequest.get("data").getAsJsonArray();
				for (int i = 0; i < data.size(); i++) {
					JsonObject testCaseDetails = data.get(i).getAsJsonObject();
					if (testCaseDetails.has(attribute) && testCaseDetails.get(attribute)
							.getAsString().equalsIgnoreCase(entityKey))
						return testCaseDetails.get("tsID").getAsString();
				}
			} else {
				System.err.println(
						"QMetry credentials are invalid. Please contatct QMetry team");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entityKey;
	}
	public String getTSRunIDUsingTSID(String tsID) {
		String response = null;
		try {
			response = getAuthorizedBuilder("/rest/execution/list").post(String.class,
					"{\"scope\":\"cycle\",\"entityId\":\"" + tsID + "\"}");

			JsonObject jsonRequest =
					new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (jsonRequest.has("data")) {
				JsonArray data = jsonRequest.get("data").getAsJsonArray();
				for (int i = 0; i < data.size(); i++) {
					JsonObject testCaseDetails = data.get(i).getAsJsonObject();
					if (testCaseDetails.has("tsID") && testCaseDetails.get("tsID")
							.getAsString().equalsIgnoreCase(tsID))
						return testCaseDetails.get("tsRunID").getAsString();
				}
			} else {
				System.err.println(
						"QMetry credentials are invalid. Please contatct QMetry team");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsID;
	}
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
	 * @return status of test case execution whether it has updated or not.
	 *         update test case result using test case id.
	 */
	public boolean executeTestCase(String testSuiteID, String testSuiteRunID,
			String testCaseID, String status) {
		log.info(String.format(
				"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcid: [%s], status: [%s]",
				token, testSuiteID, platform, testCaseID, status));
		String suiteResponse = null;
		if (testSuiteRunID == null || StringUtil.isBlank(testSuiteRunID)) {
			suiteResponse = getTestSuiteRunID(testSuiteID);
			JsonObject jsonTestSuite = new Gson()
					.fromJson(suiteResponse, JsonElement.class).getAsJsonObject();
			JsonArray arrTestSuiteRunID = jsonTestSuite.get("data").getAsJsonArray();

			for (JsonElement jsonElement : arrTestSuiteRunID) {
				if (jsonElement.getAsJsonObject().get("platformID").getAsString()
						.equalsIgnoreCase(
								ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM
										.getStringVal())) {
					testSuiteRunID =
							jsonElement.getAsJsonObject().get("tsRunID").getAsString();
					break;
				}
			}
		}

		// get test case run id using test case id
		String tcRunID = null;
		String response = getTestCaseRunID(testSuiteRunID);
		JsonObject jsonTcs =
				new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
		JsonArray arrTCJson = jsonTcs.get("data").getAsJsonArray();
		for (JsonElement jsonElement : arrTCJson) {
			if (jsonElement.getAsJsonObject().get("tcID").toString()
					.equalsIgnoreCase(String.valueOf(testCaseID))) {
				tcRunID = jsonElement.getAsJsonObject().get("tcRunID").getAsString();
				break;
			}
		}

		if (status.equalsIgnoreCase(TestCaseRunResult.PASS.toQmetry6())) {
			updateStatus(tcRunID, testSuiteRunID, TestCaseRunResult.PASS);
			return true;
		} else if (status.equalsIgnoreCase(TestCaseRunResult.FAIL.toQmetry6())) {
			updateStatus(tcRunID, testSuiteRunID, TestCaseRunResult.FAIL);
			return true;
		} else if (status.equalsIgnoreCase(TestCaseRunResult.SKIPPED.toQmetry6())) {
			updateStatus(tcRunID, testSuiteRunID, TestCaseRunResult.SKIPPED);
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
	 * @return status of test case execution whether it has updated or not.
	 *         update test case result using test case run id
	 */
	public boolean executeTestCaseUsingRunId(String testSuiteID, String testSuiteRunID,
			String tcRunID, String qmetryResult) {
		log.info(String.format(
				"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcrunid: [%s], status: [%s]",
				token, testSuiteID, platform, tcRunID, qmetryResult));

		if (testSuiteRunID == null || StringUtil.isBlank(testSuiteRunID)) {
			String response = getTestSuiteRunID(testSuiteID);
			JsonObject jsonTestSuite =
					new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			JsonArray arrTestSuiteRunID = jsonTestSuite.get("data").getAsJsonArray();
			for (JsonElement jsonElement : arrTestSuiteRunID) {
				if (jsonElement.getAsJsonObject().get("platformID").getAsString()
						.equalsIgnoreCase(platform)) {
					testSuiteRunID =
							jsonElement.getAsJsonObject().get("tsRunID").getAsString();
				}
			}

		}

		if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.PASS.toQmetry6())) {
			updateStatus(tcRunID, testSuiteRunID, TestCaseRunResult.PASS);
			return true;
		} else if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.FAIL.toQmetry6())) {
			updateStatus(tcRunID, testSuiteRunID, TestCaseRunResult.FAIL);
			return true;
		} else if (qmetryResult.equalsIgnoreCase(TestCaseRunResult.SKIPPED.toQmetry6())) {
			updateStatus(tcRunID, testSuiteRunID, TestCaseRunResult.SKIPPED);
			return true;
		}
		return false;

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
					HttpPost httppost =
							new HttpPost(serviceUrl + "/rest/attachments/testLog");

					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

					FileBody bin = new FileBody(filePath);
					builder.addTextBody("desc", "Attached on " + CurrentDate,
							org.apache.http.entity.ContentType.TEXT_PLAIN);
					builder.addTextBody("type", "TCR",
							org.apache.http.entity.ContentType.TEXT_PLAIN);
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
					int id = Integer.parseInt(data.getAsJsonArray().get(0)
							.getAsJsonObject().get("id").toString());
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

	public int attachTestLogs(long tcVersionIdASEntityId, File filePath) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

			final String CurrentDate = format.format(new Date());
			Path path = Paths.get(filePath.toURI());
			byte[] outFileArray = Files.readAllBytes(path);

			if (outFileArray != null) {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				try {
					HttpPost httppost = new HttpPost(serviceUrl + "/rest/attachments");

					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

					FileBody bin = new FileBody(filePath);
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
					int id = Integer.parseInt(data.getAsJsonArray().get(0)
							.getAsJsonObject().get("id").toString());
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

	public String getTestCaseRunID(String testSuiteRunId) {

		JsonObject objTestCaseRunID = new JsonObject();
		objTestCaseRunID.addProperty("limit", 200);
		objTestCaseRunID.addProperty("page", 1);
		objTestCaseRunID.addProperty("scope", "cycle");
		objTestCaseRunID.addProperty("start", 0);
		objTestCaseRunID.addProperty("tsrID", testSuiteRunId);

		Builder builder =
				new RestTestBase().getWebResource(serviceUrl, "/rest/execution/list/tcr")
						.getRequestBuilder();
		// add header
		builder.header("Accept-Charset", "utf-8");
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		// get test case run id response
		String response = null;
		try {
			response = builder.post(String.class, objTestCaseRunID.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String getTestSuiteRunID(String testSuiteID) {

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

		Builder builder = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/execution/list").getRequestBuilder();
		// add header
		builder.header("Accept-Charset", "utf-8");
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		// get test suite run id response
		String response = null;
		try {
			response = builder.post(String.class, jsonObjTestSuiteRunID.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public Map<String, String> getRequestHeader() {
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Content-Type", "application/json; charset=UTF-8");
		requestHeaders.put("Accept", "application/json");
		requestHeaders.put("usertoken", token);
		requestHeaders.put("scope", scope);
		return requestHeaders;
	}

	/**
	 * @param status
	 * @param userInfo
	 * @return statusName get run status id for related scope. ex for pass it
	 *         may be 1000, for fail it may be 1001 etc.
	 */
	public String getRunStatusID(TestCaseRunResult status) {
		JsonObject jsonObject =
				new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		JsonArray array = jsonObject.get("allstatus").getAsJsonArray();

		Map<String, String> statusMap = new HashMap<String, String>();
		for (JsonElement element : array) {
			JsonObject statusObject = element.getAsJsonObject();
			statusMap.put(statusObject.get("name").getAsString(),
					statusObject.get("id").getAsString());
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

	public String getUserInfo() {
		Builder builder = getAuthorizedBuilder("/rest/admin/project/getinfo");
		String userInfo = null;
		try {
			userInfo = builder.get(String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public Builder getAuthorizedBuilder(String endPoint) {
		Builder builder = new RestTestBase().getWebResource(serviceUrl, endPoint)
				.getRequestBuilder();
		// add headers
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}
		return builder;
	}

	public String updateStatus(String testCaseRunId, String testSuiteRunID,
			TestCaseRunResult runResult) {
		JsonObject jsonTCBulkUpdate = new JsonObject();

		jsonTCBulkUpdate.addProperty("dropID", drop);
		jsonTCBulkUpdate.addProperty("entityIDs", testCaseRunId);
		jsonTCBulkUpdate.addProperty("entityType", "TCR");
		jsonTCBulkUpdate.addProperty("qmTsRunId", testSuiteRunID);
		jsonTCBulkUpdate.addProperty("qmRunObj", "");
		jsonTCBulkUpdate.addProperty("isAutoExecuted", 1);

		String runStatus = getRunStatusID(runResult);
		jsonTCBulkUpdate.addProperty("runStatusID", runStatus);

		// add header
		Map<String, String> requestHeaders = new LinkedHashMap<String, String>();
		requestHeaders.putAll(getRequestHeader());

		Builder builder = getAuthorizedBuilder("/rest/execution/runstatus/bulkupdate");

		String response = null;
		try {
			response = builder.put(String.class, jsonTCBulkUpdate.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String createTestSuiteFolder(String parentFolderId) {

		String randomizer = new Date().toString();

		Builder testCaseFolderBuilder =
				new RestTestBase().getWebResource(serviceUrl, "/rest/testsuites/folders")
						.getRequestBuilder();
		JsonObject testSuiteFolderEntity = new JsonObject();
		testSuiteFolderEntity.addProperty("scope", "cycle");
		testSuiteFolderEntity.addProperty("name", "Imported_" + randomizer);
		testSuiteFolderEntity.addProperty("parentId", parentFolderId);
		// add headers
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseFolderBuilder.header(entry.getKey(), entry.getValue());
		}
		String response = null;
		try {
			response = testCaseFolderBuilder.post(String.class,
					testSuiteFolderEntity.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;

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
	 * @param serviceUrl
	 * @param scope
	 * @return
	 */
	public int attachTestLogsUsingRunID(String serviceUrl, long testCaseRunId,
			File filePath, String scope) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

			final String CurrentDate = format.format(new Date());
			Path path = Paths.get(filePath.toURI());
			byte[] outFileArray = Files.readAllBytes(path);

			if (outFileArray != null) {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				try {
					HttpPost httppost =
							new HttpPost(serviceUrl + "/rest/attachments/testLog");

					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

					FileBody bin = new FileBody(filePath);
					builder.addTextBody("desc", "Attached on " + CurrentDate,
							org.apache.http.entity.ContentType.TEXT_PLAIN);
					builder.addTextBody("type", "TCR",
							org.apache.http.entity.ContentType.TEXT_PLAIN);
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
					int id = Integer.parseInt(data.getAsJsonArray().get(0)
							.getAsJsonObject().get("id").toString());
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

	public String searchExistsTestCase(String scriptName) {

		Builder builder = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/search/result").getRequestBuilder();
		// add header
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
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

		String response = null;
		try {
			response = builder.post(String.class, entity.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String searchExistSuiteUsingTCID(String testCaseId) {
		Builder builder = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/testsuites/list").getRequestBuilder();
		// add headers
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			builder.header(entry.getKey(), entry.getValue());
		}

		JsonObject linkedAsset = new JsonObject();
		linkedAsset.addProperty("type", "TC");
		linkedAsset.addProperty("id", testCaseId);

		JsonObject listTSreq = new JsonObject();
		listTSreq.add("linkedAsset", linkedAsset);
		listTSreq.addProperty("scope", "cycle");

		String response = null;
		try {
			response = builder.post(String.class, listTSreq.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public String createTestCase(String scriptName, String folderId) {

		// create testcase using method fully qualifier name
		Builder tcBuidler = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/testcases/").getRequestBuilder();

		// add request header
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			tcBuidler.header(entry.getKey(), entry.getValue());
		}
		JsonObject userInfoJson =
				new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();

		JsonElement customListsDefaults = userInfoJson.get("customLists");
		String testingType = "";

		// get test case testing type
		if (!customListsDefaults.isJsonNull()
				&& customListsDefaults.getAsJsonObject().has("testingType")) {
			Set<Entry<String, JsonElement>> keys = customListsDefaults.getAsJsonObject()
					.get("testingType").getAsJsonObject().entrySet();
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

		if (!StringUtil.isNullOrEmpty(testingType))
			createTcReq.addProperty("testingType", testingType);
		// post action to create new testcase
		String response = null;
		try {
			response = tcBuidler.post(String.class, createTcReq.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	public String createTestSuite(String testSuiteFolderId) {
		String randomizer = new Date().toString();

		JsonObject createTsReq = new JsonObject();
		createTsReq.addProperty("parentFolderId", testSuiteFolderId);
		createTsReq.addProperty("name", "AutoTestCase" + randomizer);
		createTsReq.addProperty("description",
				"Auto Generated Test Suite by QMetryResultUpdater");

		Builder tsBuilder = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/testsuites/").getRequestBuilder();

		// add headers
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			tsBuilder.header(entry.getKey(), entry.getValue());
		}

		String response = null;
		try {
			response = tsBuilder.post(String.class, createTsReq.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public String linkPlatform(String newTestSuiteId) {

		JsonObject linkPlatformRequest = new JsonObject();
		linkPlatformRequest.addProperty("qmTsId", newTestSuiteId);
		linkPlatformRequest.addProperty("qmPlatformId", platform);

		Builder linkPlatformBuilder = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/testsuites/link/platforms")
				.getRequestBuilder();

		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			linkPlatformBuilder.header(entry.getKey(), entry.getValue());
		}

		String response = null;
		try {
			response =
					linkPlatformBuilder.put(String.class, linkPlatformRequest.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String linkTestCaseWithSuite(String testSuiteKey, String testCaseKey) {

		JsonObject linkTcReq = new JsonObject();
		linkTcReq.addProperty("tsIDs", testSuiteKey);
		linkTcReq.addProperty("tcID", testCaseKey);

		Builder testCaseBuilder = new RestTestBase()
				.getWebResource(serviceUrl, "/rest/testcases/link/testsuites")
				.getRequestBuilder();

		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseBuilder.header(entry.getKey(), entry.getValue());
		}
		String response = null;
		try {
			response = testCaseBuilder.put(String.class, linkTcReq.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String executeTestCaseToGetTestSuiteId(String testCaseId) {
		JsonObject executeTCID = new JsonObject();
		executeTCID.addProperty("tcId", testCaseId);
		executeTCID.addProperty("platformId", platform);
		Builder testCaseBuilder = new RestTestBase()
				.getWebResource(serviceUrl, "rest/testcases/execute").getRequestBuilder();
		// add headers
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseBuilder.header(entry.getKey(), entry.getValue());
		}
		testCaseBuilder.cookie(new Cookie("tcActiveScope", "btnTcScopeToolCycle"));
		String response = null;
		try {
			response = testCaseBuilder.post(String.class, executeTCID.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public int createTestSuiteParentFolder() {

		JsonObject infoUSer =
				new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		JsonElement rootFolders = infoUSer.get("rootFolders");
		JsonElement testsuite;

		// get test case parent folder id
		if (!rootFolders.isJsonNull() && rootFolders.getAsJsonObject().has("TS")) {
			testsuite = rootFolders.getAsJsonObject().get("TS");

			if (!testsuite.isJsonNull() && testsuite.getAsJsonObject().has("id"))
				return testsuite.getAsJsonObject().get("id").getAsInt();
			else System.err.println("Test Suite id does not exists in user info");
		} else {
			System.err.println("Test Suite does not exists in user info");
		}
		return 0;
	}

	public int createTestCaseParentFolder() {
		JsonObject infoUSer =
				new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		JsonElement rootFolders = infoUSer.get("rootFolders");
		JsonElement testCase;
		// get test case parent folder id
		if (!rootFolders.isJsonNull() && rootFolders.getAsJsonObject().has("TC")) {
			testCase = rootFolders.getAsJsonObject().get("TC");

			if (!testCase.isJsonNull() && testCase.getAsJsonObject().has("id"))
				return testCase.getAsJsonObject().get("id").getAsInt();
			else System.err.println("Test case id does not exists in user info");
		} else {
			System.err.println("Test case does not exists in user info");
		}
		return 0;
	}

	public String createTestCaseFolder(String parentFolderId) {

		String randomizer = new Date().toString();

		Builder testCaseFolderBuilder =
				new RestTestBase().getWebResource(serviceUrl, "/rest/testcases/folders")
						.getRequestBuilder();

		JsonObject testCaseFolderEntity = new JsonObject();
		testCaseFolderEntity.addProperty("scope", "cycle");
		testCaseFolderEntity.addProperty("name", "Imported_" + randomizer);
		testCaseFolderEntity.addProperty("parentId", parentFolderId);
		// add headers
		for (Iterator<Map.Entry<String, String>> it =
				getRequestHeader().entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			testCaseFolderBuilder.header(entry.getKey(), entry.getValue());
		}
		String response = null;
		try {
			response = testCaseFolderBuilder.post(String.class,
					testCaseFolderEntity.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public void setPlatform(String platform) {
		this.platform = platform;

	}

	public void setSuite(String suite) {
		this.suite = suite;

	}

}
