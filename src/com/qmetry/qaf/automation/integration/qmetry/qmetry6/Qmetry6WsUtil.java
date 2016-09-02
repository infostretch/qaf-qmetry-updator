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
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.util.FileUtil;
import com.qmetry.qaf.automation.util.PropertyUtil;
import com.qmetry.qaf.automation.ws.rest.RestTestBase;

/**
 * @author anjali.bangoriya
 */
public class Qmetry6WsUtil {

	private Logger log;
	private static String token;
	private static QMetryRestWebservice integration = new QMetryRestWebservice();
	private PropertyUtil props;
	private final String user = ApplicationProperties.INTEGRATION_PARAM_QMETRY_USER.getStringVal();
	private final String pwd = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PWD.getStringVal();
	private final String serviceUrl = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SERVICE_URL.getStringVal();
	public String platform;
	private String prj, rel, build;
	public String suite;
	private String suiteRunId;
	private String drop;
	private String cycle;
	private String scope;

	private static class SingletonHolder {
		public static final Qmetry6WsUtil INSTANCE = new Qmetry6WsUtil();
	}

	public static Qmetry6WsUtil getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public Qmetry6WsUtil() {
		props = ConfigurationManager.getBundle();
		log = Logger.getLogger(this.getClass());
		log.info("Init :: Qmetry6WSUtil.");
		try {
			doLogin();
			log.info("token: " + token);
			System.setProperty("token", token);

			prj = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PRJ.getStringVal();
			rel = ApplicationProperties.INTEGRATION_PARAM_QMETRY_REL.getStringVal();
			build = ApplicationProperties.INTEGRATION_PARAM_QMETRY_BLD.getStringVal();
			cycle = ApplicationProperties.INTEGRATION_PARAM_QMETRY_CYCLE.getStringVal();
			suite = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.getStringVal();
			suiteRunId = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUITERUNID.getStringVal();
			platform = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM.getStringVal();
			drop = ApplicationProperties.INTEGRATION_PARAM_QMETRY_DROP.getStringVal();
			log.info("Qmetry6 scheduled prj: " + prj + " rel : " + rel + " build: " + build + " suite: " + suite
					+ " platform: " + platform + " drop: " + drop + "cycle:" + cycle);
			scope = prj + ":" + rel + ":" + cycle;

		} catch (Exception ex) {
			log.error("Error during init Qmetry6WSUtil: ", ex);
		}

	}

	private String doLogin() {
		String authentication1 = "username=" + user + "&password=" + pwd;
		String response = new RestTestBase().getWebResource(serviceUrl, "/rest/login").getRequestBuilder()
				.header("Accept", "application/json").header("Content-Type", "application/x-www-form-urlencoded")
				.post(String.class, authentication1);
		Gson gson = new Gson();
		JsonObject jsonRequest = gson.fromJson(response, JsonElement.class).getAsJsonObject();
		if (jsonRequest.has("usertoken")) {
			token = jsonRequest.get("usertoken").getAsString();
			props.setProperty("report.dump.token", token);
		} else {
			System.err.println("QMetry credentials are invalid. Please contatct QMetry team");
		}

		return token;
	}

	public boolean executeTestCaseUsingRunId(long tcRunId, String status) {
		log.info(String.format(
				"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcrunid: [%s], status: [%s]",
				token, suite, platform, tcRunId, status));
		return integration.executeTestCaseUsingRunId(serviceUrl, token, scope, suite, suiteRunId,
				String.valueOf(tcRunId), drop, String.valueOf(platform), status);
	}

	public boolean executeTestCase(long tcid, String status) {
		try {
			log.info(String.format(
					"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcid: [%s], status: [%s]",
					token, suite, platform, tcid, status));
			return integration.executeTestCase(serviceUrl, token, scope, suite, suiteRunId, String.valueOf(tcid),
					String.valueOf(platform), drop, status);
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}

	public int attachFileUsingRunId(long testCaseRunId, int stepId, File f) {
		try {
			String attachmentType = FileUtil.getContentType(f);
			String content = FileUtil.getBase64String(f);
			log.debug("attachmentType: local file: " + f.getAbsolutePath() + " type: " + attachmentType + " content"
					+ content);
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
			log.debug("attachmentType: local file: " + f.getAbsolutePath() + " type: " + attachmentType + " content"
					+ content);
		} catch (Exception e) {
			log.error(e);
		}
		return 0;
	}

	public boolean executeTestCaseWithoutID(String scriptName, String status) {
		try {
			log.info(String.format(
					"QMetry executeTestCase with params: token: [%s],  platform: [%s],scriptName: [%s], status: [%s]",
					token, platform, scriptName, status));

			List<Long> tcIds = integration.searchExistsTestCase(serviceUrl, token, scope, scriptName);
			if (tcIds.size() > 0) {
				// get suite if exists
				for (Long tcId : tcIds) {
					suite = integration.searchExistSuiteUsingTCID(String.valueOf(tcId));
					if (suite == null) {
						// execute test case using test case id and get
						// testSuite id (test suite is auto generated and also
						// linked with platform. No need to link with platforn)
						JsonObject testSuiteResponse = integration.executeTestCaseToGetTestSuiteId(String.valueOf(tcId),
								platform);
						if (testSuiteResponse.get("success").getAsBoolean()) {
							suite = testSuiteResponse.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id")
									.getAsString();
						}

					}
					// execute testcase on given platform using exists test
					// case id and test suite id
					return integration.executeTestCase(serviceUrl, token, scope, suite, null, String.valueOf(tcId),
							String.valueOf(platform), drop, status);
				}
			}

			else {
				Long testCaseId = null;
				String testCaseKey = null;

				// create test case folder
				String testCaseFolderId = integration.createTestCaseFolder();

				// create test case using script name
				JsonObject testCaseResponse = integration.createTestCase(scriptName, platform, testCaseFolderId);
				if (testCaseResponse.get("success").getAsBoolean()) {
					testCaseId = testCaseResponse.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id")
							.getAsLong();
					testCaseKey = testCaseResponse.get("data").getAsJsonArray().get(0).getAsJsonObject()
							.get("entityKey").getAsString();
				}

				// create test suite folder
				String testSuiteFolderId = integration.createTestSuiteFolder();

				// create test suite and get test suite id
				JsonObject testSuiteResponse = integration.createTestSuite(testSuiteFolderId);
				String testSuiteKey = null;
				if (testSuiteResponse.get("success").getAsBoolean()) {
					suite = String.valueOf(testSuiteResponse.get("data").getAsJsonArray().get(0).getAsJsonObject()
							.get("id").getAsLong());
					testSuiteKey = testSuiteResponse.get("data").getAsJsonArray().get(0).getAsJsonObject()
							.get("entityKey").getAsString();
				}
				// link suite with given platform
				integration.linkPlatform(suite, String.valueOf(platform));

				// link testcase with given suite
				integration.linkTestCaseWithSuite(testSuiteKey, testCaseKey);

				// execute testcase on given platform using auto generated test
				// case id and test suite id
				return integration.executeTestCase(serviceUrl, token, scope, suite, suiteRunId,
						String.valueOf(testCaseId), String.valueOf(platform), drop, status);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return false;

	}

}