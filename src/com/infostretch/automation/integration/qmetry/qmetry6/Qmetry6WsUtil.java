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

package com.infostretch.automation.integration.qmetry.qmetry6;

import java.io.File;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infostretch.automation.core.ConfigurationManager;
import com.infostretch.automation.keys.ApplicationProperties;
import com.infostretch.automation.util.FileUtil;
import com.infostretch.automation.util.PropertyUtil;
import com.infostretch.automation.ws.rest.RestTestBase;

public class Qmetry6WsUtil {
	QmetryRestWrapper rest = new QmetryRestWrapper();
	Logger log;
	static String token;
	static QMetryRestWebservice integration = new QMetryRestWebservice();
	PropertyUtil props;
	private final String user = ApplicationProperties.INTEGRATION_PARAM_QMETRY_USER.getStringVal();
	private final String pwd = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PWD.getStringVal();
	private final String serviceUrl = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SERVICE_URL.getStringVal();
	public String platform;
	public String suit;
	private String prj, rel, build, suite, suiteRunId, drop, cycle;

	public Qmetry6WsUtil() {

		props = ConfigurationManager.getBundle();
		log = Logger.getLogger(this.getClass());
		log.info("Init :: Qmetry6WSUtil.");
		try {
			token = doLogin();
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
					+ " platform: " + platform + " drop: " + drop);

			// }

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
		String token = jsonRequest.get("usertoken").getAsString();
		ConfigurationManager.getBundle().setProperty("report.dump.token", token);

		return token;
	}

	private static class SingletonHolder {
		public static final Qmetry6WsUtil INSTANCE = new Qmetry6WsUtil();
	}

	public static Qmetry6WsUtil getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public boolean executeTestCaseUsingRunId(long tcRunId, String status) {
		log.info(String.format(
				"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcrunid: [%s], status: [%s]",
				token, suite, platform, tcRunId, status));
		return integration.executeTestCaseUsingRunId(serviceUrl, token, prj, rel, cycle, suite, suiteRunId,
				String.valueOf(tcRunId), drop, String.valueOf(platform), status);
	}

	public boolean executeTestCase(long tcid, String status) {
		try {
			log.info(String.format(
					"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcid: [%s], status: [%s]",
					token, suite, platform, tcid, status));
			return integration.executeTestCase(serviceUrl, token, prj, rel, cycle, suite, suiteRunId,
					String.valueOf(tcid), String.valueOf(platform), drop, status);
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
			return integration.attachTestLogsUsingRunId(token, prj, rel, cycle, testCaseRunId, f);
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
}