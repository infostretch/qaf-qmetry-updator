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

package com.qmetry.qaf.automation.integration.qmetry.qmetry6.scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.integration.qmetry.QmetryTestCase;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.QMetryRestWebservice;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.Qmetry6RestClient;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.patch.Qmetry6WsUtil;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.scheduler.schedulerJsonPojo.Schedule;
import com.qmetry.qaf.automation.integration.qmetry.qmetry6.scheduler.schedulerJsonPojo.Testcase;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.step.client.TestNGScenario;
import com.qmetry.qaf.automation.util.PropertyUtil;
import com.qmetry.qaf.automation.util.StringUtil;

/**
 * TestNG listener class.<br/>
 * Filters test case for Rally test set and QMetry6 test scheduler. It will
 * match with TC_ID provided in annotation. In case not found annotation it will
 * match test method name.
 * 
 * @see QmetryTestCase
 * @see RallyTestCase
 * @author anjali
 */
public class Qmetry6SchedulerFilter implements IMethodInterceptor {
	final PropertyUtil props = ConfigurationManager.getBundle();

	private final Log logger = LogFactoryImpl.getLog(Qmetry6SchedulerFilter.class);

	private ArrayList<Testcase> tcLst;
	public static Map<String, Testcase> tcMap = null;

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> arg0, ITestContext context) {
		try {
			String scheduleXmlFile = ApplicationProperties.INTEGRATION_PARAM_QMETRY_SCHEDULE_FILE.getStringVal();

			if (StringUtils.isNotBlank(scheduleXmlFile)) {
				arg0 = applyQmetrySecheduledTCsFilter(arg0, context, scheduleXmlFile);
				props.setProperty(ApplicationProperties.INTEGRATION_TOOL_QMETRY.name(), "1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arg0;
	}

	public List<IMethodInstance> applyQmetrySecheduledTCsFilter(List<IMethodInstance> list, ITestContext context,
			String jsonFile) {

		JAXBContext jc;

		List<IMethodInstance> filteredList = new ArrayList<IMethodInstance>();

		tcLst = new ArrayList<Testcase>();
		try {

			jc = JAXBContext.newInstance(Schedule.class);

			Unmarshaller unmarshell = jc.createUnmarshaller();

			// Set the Unmarshaller media type to JSON or XML
			unmarshell.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");

			// Set it to true if you need to include the JSON root element in
			// the
			// JSON input
			unmarshell.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);

			JAXBElement<Schedule> scheduleUnmarshal = null;

			StreamSource json = new StreamSource(new File(jsonFile));
			scheduleUnmarshal = unmarshell.unmarshal(json, Schedule.class);

			Schedule schedules = scheduleUnmarshal.getValue();

			List<Testcase> testcase = schedules.getTestcases();

			@SuppressWarnings("unused")
			Qmetry6RestClient wsUtil = Qmetry6RestClient.getInstance();
			QMetryRestWebservice integration = Qmetry6RestClient.getIntegration();
			integration.setPlatform(String.valueOf(schedules.getPlatformId()));
			integration.setSuite(String.valueOf(schedules.getTestsuiteId()));

			tcMap = new HashMap<String, Testcase>();

			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_PRJ.key,
					String.valueOf(schedules.getProjectId()));
			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_REL.key,
					String.valueOf(schedules.getReleaseId()));
			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_CYCLE.key,
					String.valueOf(schedules.getBuildId()));
			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUIT.key,
					String.valueOf(schedules.getTestsuiteId()));
			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_SUITERUNID.key,
					String.valueOf(schedules.getTestsuiteRunId()));
			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_DROP.key,
					String.valueOf(schedules.getDropId()));
			ConfigurationManager.getBundle().setProperty(ApplicationProperties.INTEGRATION_PARAM_QMETRY_PLATFORM.key,
					String.valueOf(schedules.getPlatformId()));

			Iterator<Testcase> testcaseIterator = testcase.iterator();
			while (testcaseIterator.hasNext()) {
				Testcase tc = testcaseIterator.next();
				String xmltcid = String.valueOf(tc.getTestcaseId());
				String xmlscriptname = tc.getTestcaseName();
				logger.info("Qmetry6 scheduled TC: " + xmltcid + " " + xmlscriptname);
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
			int platform = Integer.parseInt(String.valueOf(schedules.getPlatformId()));
			String qmetryplatform = platform == 116 ? "*iehta" : platform == 118 ? "*googlechrome" : "*firefox";
			params.put("browser", qmetryplatform);
			context.getCurrentXmlTest().setParameters(params);
			return filteredList;
		} catch (Exception e) {
			logger.error(e);
		}
		for (IMethodInstance l : list) {
			logger.info(l.getMethod());
		}
		return list;
	}

	private boolean isScriptNameMaching(TestNGScenario method, Testcase qtc) {
		String xmlscriptname = qtc.getTestcaseName();
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
		if ((null == runids) || (null == String.valueOf(qtc.getTcrunId()) || (qtc.getTcrunId() <= 0))) {
			return false;
		}
		String xmlrunid = String.valueOf(qtc.getTcrunId());
		for (String runid : runids) {
			if (xmlrunid.equalsIgnoreCase(runid)) {
				return true;
			}
		}
		return false;
	}

	private boolean isTCIDMaching(TestNGScenario method, Testcase qtc) {
		long xmltcid = qtc.getTestcaseId();

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

}
