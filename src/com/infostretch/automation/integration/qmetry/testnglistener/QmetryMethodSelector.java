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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.testng.IMethodSelector;
import org.testng.IMethodSelectorContext;
import org.testng.ITestNGMethod;

import com.infostretch.automation.core.ConfigurationManager;
import com.infostretch.automation.integration.qmetry.QmetryWSUtil;
import com.qmetry.schedule.jax.Schedule;
import com.qmetry.schedule.jax.Schedules;
import com.qmetry.schedule.jax.Testcase;
import com.qmetry.schedule.jax.Testcases;
import com.qmetry.schedule.jax.Testsuite;

/**
 * Method selector to select scheduled method by QMetry
 * 
 * @author chirag
 */
public class QmetryMethodSelector implements IMethodSelector {

	/**
	 * Used anywhere, Can it be removed?...
	 */
	List<String> secheduledTCs;
	private static final long serialVersionUID = -2381971507029432565L;

	public QmetryMethodSelector() {
		secheduledTCs = getQmetrySecheduledTCs();
	}

	@Override
	public boolean includeMethod(IMethodSelectorContext context, ITestNGMethod method, boolean isTestMethod) {
		List<String> dLst = Arrays.asList(method.getMethodsDependedUpon());
		boolean dInclude = false;
		for (String depedent : dLst) {
			if (secheduledTCs.contains(depedent)) {
				dInclude = true;
				break;
			}
		}
		boolean include = !isTestMethod || secheduledTCs.contains(method.getMethodName()) || dInclude;
		if (!include) {
			context.setStopped(true);
		}
		return include;

	}

	@Override
	public void setTestMethods(List<ITestNGMethod> testMethods) {
		Iterator<ITestNGMethod> iter = testMethods.iterator();
		while (iter.hasNext()) {
			ITestNGMethod m = iter.next();
			if (m.isTest() && !secheduledTCs.contains(m.getMethodName())) {
				iter.remove();
			}

		}
	}

	public ArrayList<String> getQmetrySecheduledTCs() {
		String xmlFile = System.getProperty("qmetry.schedule.file");

		String pkg =

				ConfigurationManager.getBundle().getString("qmetry.jax.pkg", "com.qmetry.schedule.jax");

		ArrayList<String> tcLst = null;

		if (StringUtils.isNotBlank(xmlFile)) {
			// create a JAXBContext capable of handling classes generated into
			// the testrunner.jax package
			JAXBContext jc;
			tcLst = new ArrayList<String>();

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

				String prj = suit.getProjectname();
				String rel = suit.getReleasename();
				String build = suit.getBuildname();
				System.out.println("Qmetry scheduled prj: " + prj + " rel : " + rel + " build: " + build);
				wsUtil.setScope(prj, rel, build);
				for (Testcase tc : tcs.getTestcase()) {
					String tcid = String.valueOf(tc.getTestcaseid().intValue());
					System.out.println("Qmetry scheduled TC: " + tcid);
					tcLst.add(tcid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return tcLst;
	}

}
