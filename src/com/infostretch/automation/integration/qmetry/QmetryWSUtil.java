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

package com.infostretch.automation.integration.qmetry;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.infostretch.automation.core.ConfigurationManager;
import com.infostretch.automation.keys.ApplicationProperties;
import com.infostretch.automation.util.FileUtil;
import com.infostretch.automation.util.PropertyUtil;
import com.infostretch.automation.util.StringUtil;
import com.qmetry.ws.client.QMetryWSLocator;
import com.qmetry.ws.client.QMetryWSSOAPBindingStub;

/**
 * Utility class for Qmetry web service calls. The nested class is referenced no
 * earlier (and therefore loaded no earlier by the class loader) than the moment
 * that getInstance() is called. Thus, this solution is thread-safe without
 * requiring special language constructs (i.e. volatile or synchronized).
 * 
 * @author chirag
 */

public class QmetryWSUtil {
	private QMetryWSSOAPBindingStub stub;
	PropertyUtil props;
	private final String user = ApplicationProperties.INTEGRATION_PARAM_QMETRY_USER.getStringVal();
	private final String pwd = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PWD.getStringVal();
	private final String serviceUrl = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SERVICE_URL.getStringVal();
	Logger log;
	static String token;

	public int platform = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM.getIntVal(0);
	public int suit = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.getIntVal(0);;

	/**
	 * Private constructor prevents instantiation from other classes
	 */
	private QmetryWSUtil() {
		props = ConfigurationManager.getBundle();
		log = Logger.getLogger(this.getClass());
		log.info("Init :: QmetryWSUtil.");
		try {
			System.out.println(serviceUrl);
			URL url = new URL(serviceUrl);
			QMetryWSLocator loc = new QMetryWSLocator();
			loc.setQMetryWSSOAPPort_HttpEndpointAddress(serviceUrl);
			stub = new QMetryWSSOAPBindingStub(url, loc);
			// set authentication information on the service

			stub.setUsername(user);
			stub.setPassword(pwd);

			// Configure the service to maintain an HTTP session cookie
			stub.setMaintainSession(true);
			stub.getUsername();
			token = stub.login(user, pwd);
			log.info("token: " + token);
			if (StringUtil.isBlank(ApplicationProperties.INTEGRATION_PARAM_QMETRY_SCHEDULE_FILE.getStringVal(""))) {
				String prj = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PRJ.getStringVal();
				String rel = ApplicationProperties.INTEGRATION_PARAM_QMETRY_REL.getStringVal();
				String build = ApplicationProperties.INTEGRATION_PARAM_QMETRY_BLD.getStringVal();
				log.info("Qmetry scheduled prj: " + prj + " rel : " + rel + " build: " + build);

				setScope(prj, rel, build);

			}

		} catch (Exception ex) {
			log.error("Error during init QmetryWSUtil: ", ex);
		}
	}

	public void setScope(String prj, String release, String build) {
		try {
			log.info("token: " + token);
			stub.setScope(token, prj, release, build);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public boolean executeTestCase(int tcid, String status) {
		try {
			log.info(String.format(
					"Qmetry executeTestCase with params: token: [%s], suit: [%s], platform: [%s], tcid: [%s], status: [%s]",
					token, suit, platform, tcid, status));
			return stub.executeTestCase(token, suit, platform, tcid, status);
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}

	public int attachFileUsingRunId(int testCaseRunId, int stepId, File f) {
		try {
			String attachmentType = FileUtil.getContentType(f);
			String content = FileUtil.getBase64String(f);// Base64.encode(FileUtils.readFileToByteArray(f));
			log.debug("attachmentType: local file: " + f.getAbsolutePath() + " type: " + attachmentType + " content"
					+ content);
			return stub.addTestLogUsingRunId(token, testCaseRunId, stepId, f.getName(), "Uploaded by ISFW", content,
					attachmentType);
		} catch (Exception e) {
			log.error(e);
		}
		return 0;
	}

	public int attachFile(int testCaseId, int stepId, File f) {
		try {
			String attachmentType = FileUtil.getContentType(f);
			String content = FileUtil.getBase64String(f);// Base64.encode(FileUtils.readFileToByteArray(f));
			log.debug("attachmentType: local file: " + f.getAbsolutePath() + " type: " + attachmentType + " content"
					+ content);
			return stub.addTestLog(token, suit, platform, testCaseId, stepId, f.getName(), "Uploaded by ISFW", content,
					attachmentType);
		} catch (Exception e) {
			log.error(e);
		}
		return 0;
	}

	public boolean executeTestCaseUsingRunId(int runid, String status) {
		try {
			return stub.executeTestCaseUsingRunId(token, runid, status, "Result Updated by ISFW");
		} catch (RemoteException e) {
			log.error(e);
		}
		return false;
	}

	public void executeTestCase(int tcid, String status, String comments) {
		try {
			log.info(String.format(
					"Qmetry executeTestCaseWithComments with params: token: [%s], suit: [%s], platform: [%s], tcid: [%s], status: [%s]",
					token, suit, platform, tcid, status));
			stub.executeTestCaseWithComments(token, suit, platform, tcid, status, comments);
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder {
		public static final QmetryWSUtil INSTANCE = new QmetryWSUtil();
	}

	public static QmetryWSUtil getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public QMetryWSSOAPBindingStub getService() {
		return stub;
	}

}
