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
package com.infostretch.automation.integration.qmetry.testnglistener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import com.infostretch.automation.core.ConfigurationManager;
import com.infostretch.automation.integration.qmetry.QmetryTestCase;
import com.infostretch.automation.integration.qmetry.QmetryWSUtil;
import com.infostretch.automation.keys.ApplicationProperties;
import com.infostretch.automation.step.client.TestNGScenario;
import com.infostretch.automation.util.PropertyUtil;
import com.infostretch.automation.util.StringUtil;
import com.qmetry.schedule.jax.Schedule;
import com.qmetry.schedule.jax.Schedules;
import com.qmetry.schedule.jax.Testcase;
import com.qmetry.schedule.jax.Testcases;
import com.qmetry.schedule.jax.Testsuite;

/**
 * TestNG listener class.<br/>
 * Filters test case for Rally test set and QMetry test scheduler. It will match
 * with TC_ID provided in annotation. In case not found annotation it will match
 * test method name.
 * 
 * @see QmetryTestCase
 * @see RallyTestCase
 * @author chirag
 */
public class QmetrySchedulerFilter implements IMethodInterceptor {
	final PropertyUtil props = ConfigurationManager.getBundle();
	private final Log logger = LogFactoryImpl.getLog(QmetrySchedulerFilter.class);
	public static Map<String, Testcase> tcMap = null;

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> arg0, ITestContext context) {
		try {
			String scheduleXmlFile = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SCHEDULE_FILE.getStringVal();// System.getProperty("qmetry.schedule.file");

			if (StringUtils.isNotBlank(scheduleXmlFile)) {
				arg0 = applyQmetrySecheduledTCsFilter(arg0, context, scheduleXmlFile);
				props.setProperty(ApplicationProperties.INTEGRATION_TOOL_QMETRY.name(), "1");
			} else if (ApplicationProperties.INTEGRATION_TOOL_QMETRY.getBoolenVal(false)) {

				int suitid = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.getIntVal(0);
				if (suitid > 0) {
					QmetryWSUtil wsUtil = QmetryWSUtil.getInstance();
					int qmetryplatformid = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM.getIntVal(0);
					wsUtil.suit = suitid;
					wsUtil.platform = qmetryplatformid;

					String prj = ApplicationProperties.INTEGRATION_PARAM_QMETRY_PRJ.getStringVal();
					String rel = ApplicationProperties.INTEGRATION_PARAM_QMETRY_REL.getStringVal();
					String build = ApplicationProperties.INTEGRATION_PARAM_QMETRY_BLD.getStringVal();
					logger.info("Qmetry scheduled prj: " + prj + " rel : " + rel + " build: " + build);

					wsUtil.setScope(prj, rel, build);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arg0;
	}

	public List<IMethodInstance> applyQmetrySecheduledTCsFilter(List<IMethodInstance> list, ITestContext context,
			String xmlFile) {

		ArrayList<Testcase> tcLst = null;
		List<IMethodInstance> filteredList = new ArrayList<IMethodInstance>();

		String pkg = props.getString("qmetry.jax.pkg", "com.qmetry.schedule.jax");

		// create a JAXBContext capable of handling classes generated into
		// the testrunner.jax package
		JAXBContext jc;
		tcLst = new ArrayList<Testcase>();

		try {
			jc = JAXBContext.newInstance(pkg);
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();
			// unmarshal a xml file into a tree of Java content
			// objects composed of classes from the testrunner.jax package.
			Schedules schedules = null;
			schedules = (Schedules) u.unmarshal(new File(xmlFile));
			Schedule schedule = schedules.getSchedule();
			Testsuite suit = schedule.getTestsuite();

			Testcases tcs = suit.getTestcases();
			QmetryWSUtil wsUtil = QmetryWSUtil.getInstance();
			wsUtil.platform = suit.getPlatformid().intValue();
			wsUtil.suit = suit.getTestsuiteid().intValue();
			tcMap = new HashMap<String, Testcase>();
			String prj = suit.getProjectname();
			String rel = suit.getReleasename();
			String build = suit.getBuildname();
			logger.info("Qmetry scheduled prj: " + prj + " rel : " + rel + " build: " + build);
			wsUtil.setScope(prj, rel, build);
			for (Testcase tc : tcs.getTestcase()) {
				String xmltcid = String.valueOf(tc.getTestcaseid().longValue());
				String xmlscriptname = tc.getTestscriptname();
				logger.info("Qmetry scheduled TC: " + xmltcid + " " + xmlscriptname);
				tcLst.add(tc);
				Iterator<IMethodInstance> iter = list.iterator();
				while (iter.hasNext()) {

					IMethodInstance iMethodInstance = iter.next();
					TestNGScenario method = (TestNGScenario) iMethodInstance.getMethod();
					logger.debug("SchedulerFilter testNG method: " + method);
					if (isScriptNameMaching(method, tc) || isRunIdMaching(method, tc) || isTCIDMaching(method, tc)) {

						logger.info("SchedulerFilter including testNG method: " + method);
						filteredList.add(iMethodInstance);
						tcMap.put(method.getSignature(), tc);
						break;
					}
				}
			}

			Map<String, String> params = context.getCurrentXmlTest().getAllParameters();
			int platform = QmetryWSUtil.getInstance().platform;
			String qmetryplatform = platform == 116 ? "*iehta" : platform == 118 ? "*googlechrome" : "*firefox";
			params.put("browser", qmetryplatform);
			context.getCurrentXmlTest().setParameters(params);
			return filteredList;
		} catch (Exception e) {
			logger.error(e);
		}

		return list;
	}

	private boolean isScriptNameMaching(TestNGScenario method, Testcase qtc) {
		String xmlscriptname = qtc.getTestscriptname();
		if (StringUtil.isBlank(xmlscriptname)) {
			return false;
		}
		Map<String, Object> params = method.getMetaData();
		String scriptNames[] = (params.containsKey("testScriptName")
				? (String) params.get("name") + "," + (String) params.get("testScriptName")
				: (String) params.get("name")).split(",");

		for (String scriptName : scriptNames) {
			if (xmlscriptname.equalsIgnoreCase(scriptName)) {
				return true;
			}
		}

		return false;
	}

	private boolean isRunIdMaching(TestNGScenario method, Testcase qtc) {
		Map<String, Object> params = method.getMetaData();
		String[] runids = ((null != params) && params.containsKey("runId")) ? ((String) params.get("runId")).split(",")
				: null;
		if ((null == runids) || (null == qtc.getTestcaserunid()) || (qtc.getTestcaserunid().intValue() <= 0)) {
			return false;
		}
		String xmlrunid = String.valueOf(qtc.getTestcaserunid().longValue());
		for (String runid : runids) {
			if (xmlrunid.equalsIgnoreCase(runid)) {
				return true;
			}
		}

		return false;
	}

	private boolean isTCIDMaching(TestNGScenario method, Testcase qtc) {
		long xmltcid = qtc.getTestcaseid().longValue();
		Map<String, Object> params = method.getMetaData();

		if ((null != params) && params.containsKey("TC_ID")) {
			Double tcids[] = StringUtil.extractNums((String) params.get("TC_ID"));
			for (Double tcid : tcids) {
				if (xmltcid == tcid.longValue()) {
					return true;
				}
			}
		}
		Double[] tcidsFromName = StringUtil.extractNums((String) params.get("name"));
		for (Double tcid : tcidsFromName) {
			if (xmltcid == tcid.longValue()) {
				return true;
			}
		}

		return false;
	}

	private Testcases setUpAndGetTCsFromScheduleXml() {
		// create a JAXBContext capable of handling classes generated into
		// the testrunner.jax package
		JAXBContext jc;
		Testcases tcs = null;
		String xmlFile = System.getProperty("qmetry.schedule.file");

		String pkg =

				props.getString("qmetry.jax.pkg", "com.qmetry.schedule.jax");
		try {
			jc = JAXBContext.newInstance(pkg);
			// create an Unmarshaller
			Unmarshaller u = jc.createUnmarshaller();
			// unmarshal a xml file into a tree of Java content
			// objects composed of classes from the testrunner.jax package.
			Schedules schedules = null;
			schedules = (Schedules) u.unmarshal(new File(xmlFile));
			Schedule schedule = schedules.getSchedule();
			Testsuite suit = schedule.getTestsuite();

			tcs = suit.getTestcases();
			QmetryWSUtil wsUtil = QmetryWSUtil.getInstance();
			wsUtil.platform = suit.getPlatformid().intValue();
			wsUtil.suit = suit.getTestsuiteid().intValue();

			String prj = suit.getProjectname();
			String rel = suit.getReleasename();
			String build = suit.getBuildname();
			logger.info("Qmetry scheduled prj: " + prj + " rel : " + rel + " build: " + build);
			wsUtil.setScope(prj, rel, build);

		} catch (Exception e) {
			logger.error(e);
		}
		return tcs;
	}
}
