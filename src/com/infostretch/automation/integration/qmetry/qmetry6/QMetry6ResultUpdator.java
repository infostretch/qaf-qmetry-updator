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
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.infostretch.automation.integration.TestCaseResultUpdator;
import com.infostretch.automation.integration.TestCaseRunResult;
import com.infostretch.automation.integration.qmetry.QmetryWebserviceParameter.QmetryWSParameters;
import com.infostretch.automation.integration.qmetry.qmetry6.scheduler.Qmetry6SchedulerFilter;
import com.infostretch.automation.integration.qmetry.qmetry6.scheduler.schedulerJsonPojo.Testcase;
import com.infostretch.automation.keys.ApplicationProperties;
import com.infostretch.automation.util.FileUtil;
import com.infostretch.automation.util.StringComparator;
import com.infostretch.automation.util.StringUtil;

/**
 * Implementation of {@link TestCaseResultUpdator} to update results on QMetry
 * 
 * @author anjali
 */
public class QMetry6ResultUpdator implements TestCaseResultUpdator {

	private static final Log logger = LogFactoryImpl.getLog(QMetry6ResultUpdator.class);

	@Override
	public boolean updateResult(Map<String, ? extends Object> params, TestCaseRunResult result, String log) {
		File[] attachments = null;
		long id = 0;
		boolean isRunid = false;
		if ((null != Qmetry6SchedulerFilter.tcMap)
				&& Qmetry6SchedulerFilter.tcMap.containsKey((String) params.get("sign"))) {
			Testcase tc = Qmetry6SchedulerFilter.tcMap.get((String) params.get("sign"));
			if ((tc != null) && (tc.getTcrunId() > 0)) {
				id = tc.getTcrunId();
				isRunid = true;
			} else {
				id = tc.getTestcaseId();
			}
		} else {
			id = getRunId(params);
			if (id > 0) {
				isRunid = true;
			} else {
				id = getTCID(params);
			}
		}
		if (id == 0) {
			logger.error("no valid qmetry testcase mapping id found for " + (String) params.get("sign"));
			return false;
		}
		logger.info("Updating result [" + result.toQmetry6() + "] for [" + (String) params.get("sign") + "] using "
				+ (isRunid ? "runid [" : "tcid [") + id + "]");

		updateResult(id, result, isRunid);
		if (ApplicationProperties.INTEGRATION_TOOL_QMETRY_UPLOADATTACHMENTS.getBoolenVal(true)) {
			addAttachments(log, (String) params.get("name"), id, isRunid);
			try {
				attachments = (File[]) params.get(QmetryWSParameters.Attachments.name());
				addAttachments(id, isRunid, attachments);
			} catch (Exception e) {
				logger.error(e);
			}

		}
		return true;
	}

	private Qmetry6WsUtil util = Qmetry6WsUtil.getInstance();

	private void updateResult(long id, TestCaseRunResult result, boolean isRunid) {
		boolean retVal = isRunid ? util.executeTestCaseUsingRunId(id, result.toQmetry6())
				: util.executeTestCase(id, result.toQmetry6());
		;
		logger.info("Update result staus using " + (isRunid ? "runid " : "tcid ") + id + " is: " + retVal);

	}

	private void addAttachments(String log, String methodName, long id, boolean isRunid) {
		if (StringUtil.isNotBlank(log)) {
			try {
				File logFile = FileUtil.createTempFile("log_" + System.currentTimeMillis(), "htm");
				FileUtil.writeStringToFile(logFile, log);
				addAttachments(id, isRunid, logFile);

			} catch (IOException e) {
				logger.error(e);
			}
		}
		File dir = new File(ApplicationProperties.SCREENSHOT_DIR.getStringVal(""));
		if (dir.exists()) {
			File[] screenshots = FileUtil.listFilesAsArray(dir, methodName, StringComparator.Prefix, true);
			addAttachments(id, isRunid, screenshots);
		}
	}

	private void addAttachments(long id, boolean isRunid, File... file) {
		if ((file != null) && (file.length > 0)) {
			for (File f : file) {

				int retval = isRunid ? util.attachFileUsingRunId(id, 0, f) : util.attachFile(id, 0, f);
				logger.info("upload staus for [" + f.getName() + "]using " + (isRunid ? "runid[" : "tcid[") + id
						+ "] is: " + retval);
			}
		}

	}

	private int getRunId(Map<String, ? extends Object> params) {
		Double[] runids = ((null != params) && params.containsKey("testScriptName"))
				? StringUtil.extractNums((String) params.get("runId")) : null;
		if (((null != runids) && (runids.length > 0))) {
			return runids[0].intValue();
		}
		return 0;
	}

	private int getTCID(Map<String, ? extends Object> params) {
		Double tcids[] = (null != params) && params.containsKey("TC_ID")
				? StringUtil.extractNums((String) params.get("TC_ID"))
				: StringUtil.extractNums((String) params.get("name"));

		if ((tcids != null) && (tcids.length > 0)) {
			return tcids[0].intValue();
		}
		return 0;
	}

	@Override
	public String getToolName() {
		return "QMetry";
	}

}
