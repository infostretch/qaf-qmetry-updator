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
package com.qmetry.qaf.automation.integration.qmetry.qmetry6.testhelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.integration.TestCaseRunResult;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.QMetryRestWebservice;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.Qmetry6RestClient;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.util.RandomStringGenerator;
import com.qmetry.qaf.automation.util.RandomStringGenerator.RandomizerTypes;
import com.qmetry.qaf.automation.util.StringUtil;
import com.qmetry.qaf.automation.util.Validator;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;

public class QMetry6Test {
	String testSuiteRunID = null;
	String token;
	String testCaseRunId;
	private String userInfo;

	private String testSuiteFolderId;
	private String testSuiteKey;
	private String newTestSuiteId;
	private String newTestCaseId;

	private String testCaseKey;
	private String testCaseFolderId;
	private QMetryRestWebservice qmetryRestWebservices;

	private String testCaseId;
	private String scriptName;

	/*
	 * initialize Qmetry6RestClient
	 */
	@BeforeTest
	public void init() {
		Qmetry6RestClient.getInstance();
		qmetryRestWebservices = Qmetry6RestClient.getIntegration();
		testCaseId = ConfigurationManager.getBundle().getString("integration.param.qmetry.tcId");
		scriptName = ConfigurationManager.getBundle().getString("integration.param.qmetry.scriptName");
	}

	/*
	 * verify get test suite run id from suite id
	 */
	@Test()
	public void VerifyGetTestSuiteRunID() {

		// get test suite run id response
		String resultTestsuiteRunID = qmetryRestWebservices
				.getTestSuiteRunID(ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.getStringVal());

		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));

		// get suite run id from json
		JsonObject jsonTestSuite = new Gson().fromJson(resultTestsuiteRunID, JsonElement.class).getAsJsonObject();
		JsonArray arrTestSuiteRunID = jsonTestSuite.get("data").getAsJsonArray();

		Validator.assertThat("Test suite run id should be more than 0", arrTestSuiteRunID.size(),
				Matchers.greaterThan(0));
		for (JsonElement jsonElement : arrTestSuiteRunID) {
			if (jsonElement.getAsJsonObject().get("platformID").getAsString()
					.equalsIgnoreCase(ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM.getStringVal())) {
				testSuiteRunID = jsonElement.getAsJsonObject().get("tsRunID").getAsString();
			}
		}

	}

	/*
	 * verify test case run id using test suite run id and get tc run id from
	 * response.
	 */
	@Test(dependsOnMethods = { "VerifyGetTestSuiteRunID" })
	public void VerifyTestCaseRunID() {
		// get test case run id response
		String resultTestcaseRunID = qmetryRestWebservices.getTestCaseRunID(testSuiteRunID);

		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));
		// return test case run id from json response
		JsonObject jsonTcs = new Gson().fromJson(resultTestcaseRunID, JsonElement.class).getAsJsonObject();
		JsonArray arrTCJson = jsonTcs.get("data").getAsJsonArray();

		Validator.assertThat("Test case run id should be more than 0", arrTCJson.size(), Matchers.greaterThan(0));
		for (JsonElement jsonElement : arrTCJson) {
			if (jsonElement.getAsJsonObject().get("tcID").toString().equalsIgnoreCase(String.valueOf(testCaseId))) {
				testCaseRunId = jsonElement.getAsJsonObject().get("tcRunID").getAsString();
				break;
			}
		}

	}

	/*
	 * verify user info
	 */
	@Test()
	public void verifyInfoUser() {

		userInfo = qmetryRestWebservices.getUserInfo();
		JsonObject infoUserJson = new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		String userName = null;
		if (infoUserJson.has("currentUser")) {
			JsonElement currentUser = infoUserJson.get("currentUser");
			userName = currentUser.getAsJsonObject().get("name").getAsString();
		}
		String user = ApplicationProperties.INTEGRATION_PARAM_QMETRY_USER.getStringVal();
		Validator.verifyThat("UserName should be " + user, userName, Matchers.equalToIgnoringCase(user));
	}

	/*
	 * verify run status id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyRunStatusId() {

		JsonObject jsonObject = new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();
		JsonArray array = jsonObject.get("allstatus").getAsJsonArray();

		Map<String, String> statusMap = new HashMap<String, String>();
		for (JsonElement element : array) {
			JsonObject statusObject = element.getAsJsonObject();
			statusMap.put(statusObject.get("name").getAsString(), statusObject.get("id").getAsString());
		}
		List<Status> statusList = Arrays.asList(Status.values());

		boolean flag = true;
		Iterator<Status> runStatusItr = statusList.iterator();
		while (runStatusItr.hasNext()) {
			Status statusName = runStatusItr.next();
			if (!statusMap.containsKey(statusName.text)) {
				flag = false;
				break;
			}
		}
		Validator.verifyThat("All status are not user info", flag, Matchers.equalTo(true));

	}

	/*
	 * verify update status
	 */
	@Test(dependsOnMethods = { "VerifyGetTestSuiteRunID", "VerifyTestCaseRunID" })
	public void verifyUpdateStatus() {
		String response = qmetryRestWebservices.updateStatus(testCaseRunId, testSuiteRunID, TestCaseRunResult.PASS);

		Validator.assertThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));

		JsonObject jsonObject = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
		// status should be updated
		Validator.verifyThat("Status should update", jsonObject.get("success").toString(), Matchers.is("true"));

	}

	/*
	 * verify search exist test case using scriptname
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifySearchExistTestCase() {
		String response = qmetryRestWebservices.searchExistsTestCase(scriptName);
		if (!StringUtil.isNullOrEmpty(response)) {
			JsonObject searchResult = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (searchResult.get("data").getAsJsonArray().size() > 0) {
				Validator.verifyThat("It should search exist test case using script name",
						searchResult.get("data").getAsJsonArray().size(), Matchers.greaterThan(0));
			}
		}

	}

	/*
	 * verify search exist test suite using test case id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifySearchExistSuiteUsingTCID() {
		String response = qmetryRestWebservices.searchExistSuiteUsingTCID(testCaseId);
		Validator.assertThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));
		JsonObject testSuitesResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		Validator.verifyThat("It should search exist test suite using test case id",
				testSuitesResponse.get("data").getAsJsonArray().size(), Matchers.greaterThan(0));

	}

	/*
	 * verify create test suite based on test suite folder id
	 */
	@Test(dependsOnMethods = { "verifyCreateTestSuiteFolder" })
	public void verifyCreateTestSuite() {
		String response = qmetryRestWebservices.createTestSuite(testSuiteFolderId);
		Validator.assertThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));
		JsonObject testSuiteResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
		Validator.assertThat("Test suite should create", testSuiteResponse.get("data").getAsJsonArray().size(),
				Matchers.greaterThan(0));
		if (testSuiteResponse.get("success").getAsBoolean()) {
			newTestSuiteId = String.valueOf(
					testSuiteResponse.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsLong());
			testSuiteKey = testSuiteResponse.get("data").getAsJsonArray().get(0).getAsJsonObject().get("entityKey")
					.getAsString();
		}

	}

	/*
	 * verify create test case using scriptname and test case folder id
	 */
	@Test(dependsOnMethods = { "verifyCreateTestSuiteFolder" })
	public void verifyCreateTestCase() {
		String response = qmetryRestWebservices.createTestCase(scriptName, testCaseFolderId);
		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));
		JsonObject testCaseResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
		Validator.assertThat("Test case should create", testCaseResponse.get("data").getAsJsonArray().size(),
				Matchers.greaterThan(0));
		if (testCaseResponse.get("success").getAsBoolean()) {
			newTestCaseId = testCaseResponse.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id")
					.getAsString();
			testCaseKey = testCaseResponse.get("data").getAsJsonArray().get(0).getAsJsonObject().get("entityKey")
					.getAsString();
		}

	}

	/*
	 * verify link platform with test suite id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser", "verifyCreateTestSuite" })
	public void verifyLinkPlatform() {
		qmetryRestWebservices.linkPlatform(newTestSuiteId);
		Validator.assertThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));

	}

	/*
	 * verify link test case with test suite
	 */
	@Test(dependsOnMethods = { "verifyInfoUser", "verifyCreateTestSuite", "verifyCreateTestCase" })
	public void verifyLinkTestCaseWithSuite() {
		qmetryRestWebservices.linkTestCaseWithSuite(testSuiteKey, testCaseKey);
		Validator.verifyThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));

	}

	/*
	 * verify execute test case to get test suite id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser", "verifyCreateTestCase" })
	public void verifyExecuteTestCaseToGetTestSuiteId() {
		qmetryRestWebservices.executeTestCaseToGetTestSuiteId(newTestCaseId);
		Validator.verifyThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));
	}

	/*
	 * verify create test case folder
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyCreateTestCaseFolder() {
		boolean flag = false;
		int tcParentFolderId = 1;
		tcParentFolderId = qmetryRestWebservices.createTestCaseParentFolder();
		String response = qmetryRestWebservices.createTestCaseFolder(String.valueOf(tcParentFolderId));
		if (!StringUtil.isNullOrEmpty(response)) {
			JsonObject testCaseExecuteResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			for (JsonElement element : testCaseExecuteResponse.get("data").getAsJsonArray()) {
				if (element.getAsJsonObject().has("id")) {
					flag = true;
					testCaseFolderId = element.getAsJsonObject().get("id").getAsString();
					break;
				}
			}
		}
		Validator.verifyThat("It should search test suite using test case id", flag, Matchers.is(true));
	}

	/*
	 * check that it will create test suite folder with Imported_+{current date}
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyCreateTestSuiteFolder() {
		boolean flag = false;
		int parentFolderId = 1;
		parentFolderId = qmetryRestWebservices.createTestSuiteParentFolder();
		String response = qmetryRestWebservices.createTestSuiteFolder(String.valueOf(parentFolderId));
		if (!StringUtil.isNullOrEmpty(response)) {
			JsonObject testSuiteExecuteResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

			for (JsonElement element : testSuiteExecuteResponse.get("data").getAsJsonArray()) {
				if (element.getAsJsonObject().has("id")) {
					flag = true;
					testSuiteFolderId = element.getAsJsonObject().get("id").getAsString();
					break;
				}
			}
		}
		Validator.verifyThat("It should create test suite folder", flag, Matchers.is(true));

	}

	/*
	 * check that it will not create test suite folder with Imported_+{current
	 * date}
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotCreateTestSuiteFolder() {

		String response = qmetryRestWebservices
				.createTestSuiteFolder(RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY));

		Validator.verifyThat("It should not create test suite folder", response, Matchers.nullValue());

	}

	/*
	 * verify that it should not get test suite run id in case of using wrong
	 * test suite id
	 */
	@Test()
	public void VerifyNotGetTestSuiteRunID() {
		String wrongTestSuiteID = RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY);
		String resultTestsuiteRunID = qmetryRestWebservices.getTestSuiteRunID(wrongTestSuiteID);
		Validator.assertThat(resultTestsuiteRunID, Matchers.notNullValue());
		// get suite run id from json
		// #TODO
		JsonObject jsonTestSuite = new Gson().fromJson(resultTestsuiteRunID, JsonElement.class).getAsJsonObject();
		JsonArray arrTestSuiteRunID = jsonTestSuite.get("data").getAsJsonArray();
		Validator.verifyThat("Test Suite Run Id", arrTestSuiteRunID.size(), Matchers.lessThan(1));

	}

	/*
	 * verify not create test case folder in case of using wrong parent folder
	 * id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotCreateTestCaseFolder() {
		String wrongTestCaseFolder = RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY);
		String response = qmetryRestWebservices.createTestCaseFolder(wrongTestCaseFolder);
		Validator.verifyThat("It should not create test case folder", response, Matchers.nullValue());

	}

	/*
	 * verify not to execute test case to get test suite id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser", "verifyCreateTestCase" })
	public void verifyNotExecuteTestCaseToGetTestSuiteId() {
		qmetryRestWebservices
				.executeTestCaseToGetTestSuiteId(RandomStringGenerator.get(5, RandomizerTypes.LETTERS_ONLY));

		Validator.verifyThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));

	}

	/*
	 * verify not to link test case with test suite
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotLinkTestCaseWithSuite() {
		qmetryRestWebservices.linkTestCaseWithSuite(RandomStringGenerator.get(5, RandomizerTypes.LETTERS_ONLY),
				testCaseKey);
		Validator.verifyThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));

	}

	/*
	 * verify not to search exists test case in case of using non exists script
	 * name
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotSearchExistTestCase() {
		String response = qmetryRestWebservices
				.searchExistsTestCase(RandomStringGenerator.get(5, RandomizerTypes.LETTERS_ONLY));
		if (!StringUtil.isNullOrEmpty(response)) {
			JsonObject searchResult = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();
			if (searchResult.get("data").getAsJsonArray().size() > 0) {
				Validator.verifyThat("It should not search test suite using test case id",
						searchResult.get("data").getAsJsonArray().size(), Matchers.lessThan(1));
			}
		}

	}

	/*
	 * verify not to search exist suite using test case id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotSearchExistSuiteUsingTCID() {

		String response = qmetryRestWebservices
				.searchExistSuiteUsingTCID(RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY));
		// it should get response after that it will find exist suite using tcid
		// from response
		Validator.assertThat("Response should be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.is(200));
		JsonObject testSuitesResponse = new Gson().fromJson(response, JsonElement.class).getAsJsonObject();

		Validator.verifyThat("It should not search test suite using test case id",
				testSuitesResponse.get("data").getAsJsonArray().size(), Matchers.lessThan(1));

	}

	/*
	 * verify not to create test suite
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotCreateTestSuite() {
		qmetryRestWebservices.createTestSuite(RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY));
		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));

	}

	/*
	 * verify not to create test case
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotCreateTestCase() {
		qmetryRestWebservices.createTestCase(scriptName, RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY));
		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));

	}

	/*
	 * verify not to link test case with test suite
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotLinkPlatform() {
		qmetryRestWebservices.linkTestCaseWithSuite(testSuiteKey,
				RandomStringGenerator.get(8, RandomizerTypes.LETTERS_ONLY));
		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));

	}

	/*
	 * verify not to get test case run id using wrong test case id
	 */
	@Test(dependsOnMethods = { "VerifyGetTestSuiteRunID" })
	public void VerifyNotGetTestCaseRunID() {
		String wrongTestCaseID = RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY);
		String resultTestcaseRunID = qmetryRestWebservices.getTestCaseRunID(wrongTestCaseID);
		System.out.println(new RestTestBase().getResponse().getStatus().getStatusCode());
		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));
		Validator.verifyThat("Test case Run id response should be null", resultTestcaseRunID, Matchers.nullValue());

	}

	/*
	 * verify not to get run status id
	 */
	@Test(dependsOnMethods = { "verifyInfoUser" })
	public void verifyNotGetRunStatusId() {

		if (userInfo != null) {
			JsonObject jsonObject = new Gson().fromJson(userInfo, JsonElement.class).getAsJsonObject();

			JsonArray array = jsonObject.get("allstatus").getAsJsonArray();

			Validator.verifyThat("Status are not in user info", array.size() > 0, Matchers.equalTo(true));
		} else {
			Validator.verifyThat("Status are not in user info", userInfo, Matchers.nullValue());
		}

	}

	/*
	 * verify not to update status using test suite run id and test case run id
	 */
	@Test(dependsOnMethods = { "VerifyGetTestSuiteRunID", "VerifyTestCaseRunID" })
	public void verifyNotUpdateStatus() {

		qmetryRestWebservices.updateStatus(RandomStringGenerator.get(8, RandomizerTypes.DIGITS_ONLY), testSuiteRunID,
				TestCaseRunResult.PASS);

		Validator.assertThat("Response should not be 200", new RestTestBase().getResponse().getStatus().getStatusCode(),
				Matchers.not(200));

	}
}
