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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.util.FileUtil;
import com.qmetry.qaf.automation.util.StringUtil;

/**
 * @author anjali.bangoriya
 */
public class Qmetry7RestClient {

	private Logger log;
	private static QMetryRestWebservice integration;
	private final String user =
			ApplicationProperties.INTEGRATION_PARAM_QMETRY_USER.getStringVal();
	private final String pwd =
			ApplicationProperties.INTEGRATION_PARAM_QMETRY_PWD.getStringVal();
	private final String serviceUrl =
			ApplicationProperties.INTEGRATION_PARAM_QMETRY_SERVICE_URL.getStringVal();

	private static Qmetry7RestClient INSTANCE;

	public static Qmetry7RestClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Qmetry7RestClient();
		return INSTANCE;
	}

	private Qmetry7RestClient() {
		log = Logger.getLogger(this.getClass());
		log.info("Init :: qmetry7WSUtil.");
		integration = new QMetryRestWebservice(serviceUrl, user, pwd);
		integration.login();

	}

	public static QMetryRestWebservice getIntegration() {
		if (INSTANCE == null)
			INSTANCE = new Qmetry7RestClient();
		return integration;
	}

	public boolean executeTestCaseUsingRunIdAndTCId(long tcRunId, String status) {
		String suite = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.getStringVal();
		String suiteRunId =
				ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUITERUNID.getStringVal();

		return integration.executeTestCaseUsingRunId(suite, suiteRunId,
				String.valueOf(tcRunId), status);
	}

	public boolean executeTestCase(long tcid, String status) {
		try {
			String suite =
					ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.getStringVal("");
			if (!StringUtil.isNumeric(suite))
				suite = integration.getTSIDusingAttribute(suite, "entityKey");
			String suiteRunId = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUITERUNID
					.getStringVal("");
			if (StringUtil.isEmpty(suiteRunId)) {
				suiteRunId = integration.getTSRunIDUsingTSID(suite);
			}

			ConfigurationManager.getBundle().setProperty(
					ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUITERUNID.key,
					suiteRunId);
			ConfigurationManager.getBundle().setProperty(
					ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.key, suite);
			return integration.executeTestCase(suite, suiteRunId, String.valueOf(tcid),
					status);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}

	public int attachFileUsingRunId(long testCaseRunId, int stepId, File f) {
		try {
			String attachmentType = FileUtil.getContentType(f);
			String content = FileUtil.getBase64String(f);
			log.debug("attachmentType: local file: " + f.getAbsolutePath() + " type: "
					+ attachmentType + " content" + content);
			return integration.attachTestLogsUsingRunId(testCaseRunId, f);
		} catch (Exception e) {
			log.error(e);
		}
		return 0;
	}

	public int attachFile(long testCaseId, int stepId, File f) {
		try {
			String attachmentType = FileUtil.getContentType(f);
			String content = FileUtil.getBase64String(f);
			log.debug("attachmentType: local file: " + f.getAbsolutePath() + " type: "
					+ attachmentType + " content" + content);
			return integration.attachTestLogs(testCaseId, f);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			log.error(e);
		}
		return 0;
	}

	public boolean executeTestCaseWithoutID(String scriptName, String status) {
		String suite = null;
		try {
			log.info(String.format(
					"QMetry executeTestCase with params: scriptName: [%s], status: [%s]",
					scriptName, status));

			List<Long> tcIds = new ArrayList<Long>();
			String response = integration.searchExistsTestCase(scriptName);
			if (!StringUtil.isNullOrEmpty(response)) {
				JsonObject searchResult = new Gson().fromJson(response, JsonElement.class)
						.getAsJsonObject();
				if (searchResult.get("data").getAsJsonArray().size() > 0) {
					for (JsonElement element : searchResult.get("data")
							.getAsJsonArray()) {
						if (element.getAsJsonObject().get("testScriptName").getAsString()
								.equalsIgnoreCase(scriptName)) {
							Long tcId = new Long(
									element.getAsJsonObject().get("tcID").toString());
							tcIds.add(tcId);
							break;
						}
					}
				}
			}
			if (tcIds.size() > 0) {
				// get suite if exists
				for (Long testCaseId : tcIds) {
					String suiteResponse = integration
							.searchExistSuiteUsingTCID(String.valueOf(testCaseId));
					JsonObject testSuitesResponse = new Gson()
							.fromJson(suiteResponse, JsonElement.class).getAsJsonObject();

					for (JsonElement element : testSuitesResponse.get("data")
							.getAsJsonArray()) {
						if (element.getAsJsonObject().has("tsID"))
							suite = element.getAsJsonObject().get("tsID").getAsString();
					}
					if (suite == null) {
						// execute test case using test case id and get
						// testSuite id (test suite is auto generated and also
						// linked with platform. No need to link with platforn)
						String tcExecuteResponse =
								integration.executeTestCaseToGetTestSuiteId(
										String.valueOf(testCaseId));
						JsonObject testCaseExecuteResponse =
								new Gson().fromJson(tcExecuteResponse, JsonElement.class)
										.getAsJsonObject();

						if (testCaseExecuteResponse.get("success").getAsBoolean()) {
							suite = testCaseExecuteResponse.get("data").getAsJsonArray()
									.get(0).getAsJsonObject().get("id").getAsString();
						}

					}
					// execute testcase on given platform using exists test
					// case id and test suite id
					return integration.executeTestCase(suite, null,
							String.valueOf(testCaseId), status);
				}
			}

			else {
				Long testCaseId = null;
				String testCaseKey = null;

				// create test case folder
				int tcParentFolderId = 1;
				tcParentFolderId = integration.createTestCaseParentFolder();
				String testCaseFolderIdResponse = integration
						.createTestCaseFolder(String.valueOf(tcParentFolderId));

				JsonObject testcaseFolderResponse =
						new Gson().fromJson(testCaseFolderIdResponse, JsonElement.class)
								.getAsJsonObject();

				String testCaseFolderId = null;
				for (JsonElement element : testcaseFolderResponse.get("data")
						.getAsJsonArray()) {
					if (element.getAsJsonObject().has("id"))
						testCaseFolderId =
								element.getAsJsonObject().get("id").getAsString();
				}

				// create test case using script name
				String tcResponse =
						integration.createTestCase(scriptName, testCaseFolderId);
				JsonObject testCaseResponse = new Gson()
						.fromJson(tcResponse, JsonElement.class).getAsJsonObject();
				if (testCaseResponse.get("success").getAsBoolean()) {
					testCaseId = testCaseResponse.get("data").getAsJsonArray().get(0)
							.getAsJsonObject().get("id").getAsLong();
					testCaseKey = testCaseResponse.get("data").getAsJsonArray().get(0)
							.getAsJsonObject().get("entityKey").getAsString();
				}
				String testSuiteFolderId = null;
				// create test suite folder
				int tsParentFolderId = 1;
				tsParentFolderId = integration.createTestSuiteParentFolder();

				String tsFolderResponse = integration
						.createTestSuiteFolder(String.valueOf(tsParentFolderId));

				JsonObject testSuiteFolderResponse = new Gson()
						.fromJson(tsFolderResponse, JsonElement.class).getAsJsonObject();

				for (JsonElement element : testSuiteFolderResponse.get("data")
						.getAsJsonArray()) {
					if (element.getAsJsonObject().has("id"))
						testSuiteFolderId =
								element.getAsJsonObject().get("id").getAsString();
				}

				// create test suite and get test suite id
				String tsResponse = integration.createTestSuite(testSuiteFolderId);
				JsonObject testSuiteResponse = new Gson()
						.fromJson(tsResponse, JsonElement.class).getAsJsonObject();

				String testSuiteKey = null;
				if (testSuiteResponse.get("success").getAsBoolean()) {
					suite = String.valueOf(testSuiteResponse.get("data").getAsJsonArray()
							.get(0).getAsJsonObject().get("id").getAsLong());
					testSuiteKey = testSuiteResponse.get("data").getAsJsonArray().get(0)
							.getAsJsonObject().get("entityKey").getAsString();
				}
				// link suite with given platform
				integration.linkPlatform(suite);

				// link testcase with given suite
				integration.linkTestCaseWithSuite(testSuiteKey, testCaseKey);

				// execute testcase on given platform using auto generated test
				// case id and test suite id
				return integration.executeTestCase(suite, null,
						String.valueOf(testCaseId), status);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return false;

	}

}