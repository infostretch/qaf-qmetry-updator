/*******************************************************************************
* QMetry Automation Framework provides a powerful and versatile platform to author Test Cases in 
*                Behavior Driven, Keyword Driven or Code Driven approach
*               
*    Copyright 2016 Infostretch Corporation
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    any later version.
*
*               See the NOTICE file in root folder of distributed with this work for
*               additional information regarding copyright ownership
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*    along with this program in the name of LICENSE. 
*    
*    It is located at the root folder of the distribution.
*                If not, see https://opensource.org/licenses/gpl-3.0.html
********************************************************************************/
package com.infostretch.automation.integration.qmetry;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.infostretch.automation.integration.TestCaseResultUpdator;
import com.infostretch.automation.integration.TestCaseRunResult;
import com.infostretch.automation.integration.qmetry.QmetryWebserviceParameter.QmetryWSParameters;
import com.infostretch.automation.integration.qmetry.testnglistener.QmetrySchedulerFilter;
import com.infostretch.automation.keys.ApplicationProperties;
import com.infostretch.automation.util.FileUtil;
import com.infostretch.automation.util.StringComparator;
import com.infostretch.automation.util.StringUtil;
import com.qmetry.schedule.jax.Testcase;

/**
 * Implementation of {@link TestCaseResultUpdator} to update results on QMetry
 * 
 * @author chirag
 */
public class QmetryUpdator implements TestCaseResultUpdator {

	private static final Log logger = LogFactoryImpl.getLog(QmetryUpdator.class);

	@Override
	public boolean updateResult(Map<String, ? extends Object> params, TestCaseRunResult result, String log) {
		File[] attachments = null;
		if (params.containsKey("skip") && (Boolean) params.get("skip")) {
			return false;
		}
		int id = 0;
		boolean isRunid = false;
		if ((null != QmetrySchedulerFilter.tcMap)
				&& QmetrySchedulerFilter.tcMap.containsKey((String) params.get("sign"))) {
			Testcase tc = QmetrySchedulerFilter.tcMap.get((String) params.get("sign"));
			if ((tc.getTestcaserunid() != null) && (tc.getTestcaserunid().intValue() > 0)) {
				id = tc.getTestcaserunid().intValue();
				isRunid = true;
			} else {
				id = tc.getTestcaseid().intValue();
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
		logger.info("Updating result [" + result.toQmetry() + "] for [" + (String) params.get("sign") + "] using "
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

	private QmetryWSUtil util = QmetryWSUtil.getInstance();

	private void updateResult(int id, TestCaseRunResult result, boolean isRunid) {
		boolean retVal = isRunid ? util.executeTestCaseUsingRunId(id, result.toQmetry())
				: util.executeTestCase(id, result.toQmetry());
		;
		logger.info("Update result staus using " + (isRunid ? "runid " : "tcid ") + id + " is: " + retVal);

	}

	private void addAttachments(String log, String methodName, int id, boolean isRunid) {
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

	private void addAttachments(int id, boolean isRunid, File... file) {
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
		return "QMetry5";
	}

}
