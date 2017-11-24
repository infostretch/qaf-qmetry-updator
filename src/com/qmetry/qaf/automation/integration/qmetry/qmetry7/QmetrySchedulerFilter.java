package com.qmetry.qaf.automation.integration.qmetry.qmetry7;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.keys.ApplicationProperties;
import com.qmetry.qaf.automation.step.client.TestNGScenario;
import com.qmetry.qaf.automation.util.FileUtil;
import com.qmetry.qaf.automation.util.PropertyUtil;
import com.qmetry.qaf.automation.util.StringUtil;

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

	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> arg0,
			ITestContext context) {
		try {
			String scheduleXmlFile =
					ApplicationProperties.INTEGRATION_PARAM_QMETRY_SCHEDULE_FILE
							.getStringVal();// System.getProperty("qmetry.schedule.file");

			if (StringUtils.isNotBlank(scheduleXmlFile)) {
				arg0 = applyQmetrySecheduledTCsFilter(arg0, context, scheduleXmlFile);
				props.setProperty(ApplicationProperties.INTEGRATION_TOOL_QMETRY.name(),
						"1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arg0;
	}

	public List<IMethodInstance> applyQmetrySecheduledTCsFilter(
			List<IMethodInstance> list, ITestContext context, String xmlFile) {
		String scheduleXmlFile =
				ApplicationProperties.INTEGRATION_PARAM_QMETRY_SCHEDULE_FILE
						.getStringVal();// System.getProperty("qmetry.schedule.file");

		ArrayList<JsonObject> tcLst = new ArrayList<>();
		List<IMethodInstance> filteredList = new ArrayList<IMethodInstance>();
		JsonObject scheule;
		try {
			scheule = new GsonBuilder().create().fromJson(
					FileUtil.readFileToString(new File(scheduleXmlFile), "UTF-8"),
					JsonObject.class);

			JsonArray tcs = scheule.get("schedule").getAsJsonObject().get("testcases")
					.getAsJsonArray();
			for (int i = 0; i < tcs.size(); i++) {
				JsonObject tc = tcs.get(i).getAsJsonObject();
				String xmltcid = String.valueOf(tc.get("testcaseId").getAsLong());
				String xmlscriptname = tc.get("testScriptName").getAsString();
				logger.info("Qmetry scheduled TC: " + xmltcid + " " + xmlscriptname);
				tcLst.add(tc);
				Iterator<IMethodInstance> iter = list.iterator();
				while (iter.hasNext()) {

					IMethodInstance iMethodInstance = iter.next();
					TestNGScenario method = (TestNGScenario) iMethodInstance.getMethod();
					logger.debug("SchedulerFilter testNG method: " + method);
					if (isScriptNameMaching(method, tc) || isRunIdMaching(method, tc)
							|| isTCIDMaching(method, tc)) {

						logger.info("SchedulerFilter including testNG method: " + method);
						filteredList.add(iMethodInstance);
						break;
					}
				}
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Map<String, String> params =
		// context.getCurrentXmlTest().getAllParameters();
		// int platform = QmetryWSUtil.getInstance().platform;
		// String qmetryplatform = platform == 116 ? "*iehta"
		// : platform == 118 ? "*googlechrome" : "*firefox";
		// params.put("browser", qmetryplatform);
		// context.getCurrentXmlTest().setParameters(params);
		return filteredList;

	}

	private boolean isScriptNameMaching(TestNGScenario method, JsonObject qtc) {
		String xmlscriptname = qtc.get("testScriptName").getAsString();
		if (StringUtil.isBlank(xmlscriptname)) {
			return false;
		}
		Map<String, Object> params = method.getMetaData();
		String scriptNames[] = (params.containsKey("testScriptName")
				? (String) params.get("name") + ","
						+ (String) params.get("testScriptName")
				: (String) params.get("name")).split(",");

		for (String scriptName : scriptNames) {
			if (xmlscriptname.equalsIgnoreCase(scriptName)) {
				return true;
			}
		}

		return false;
	}

	private boolean isRunIdMaching(TestNGScenario method, JsonObject qtc) {
		Map<String, Object> params = method.getMetaData();
		String[] runids = ((null != params) && params.containsKey("runId"))
				? ((String) params.get("runId")).split(",") : null;
		if ((null == runids) || (null == qtc.get("tcrunId"))
				|| (qtc.get("tcrunId").getAsInt() <= 0)) {
			return false;
		}
		String xmlrunid = String.valueOf(qtc.get("tcrunId").getAsLong());
		for (String runid : runids) {
			if (xmlrunid.equalsIgnoreCase(runid)) {
				return true;
			}
		}

		return false;
	}

	private boolean isTCIDMaching(TestNGScenario method, JsonObject qtc) {
		long xmltcid = qtc.get("testcaseId").getAsLong();
		Map<String, Object> params = method.getMetaData();

		if ((null != params) && params.containsKey("TC_ID")) {
			String p = (String) params.get("TC_ID");
			Qmetry7RestClient.getInstance();
			QMetryRestWebservice integration = Qmetry7RestClient.getIntegration();
			String tcIDNumber = integration.getTCIDusingAttribute(p, "entityKey");
			Integer tcids[] = QMetry7ResultUpdator.extractNums(tcIDNumber);
			for (Integer tcid : tcids) {
				if (xmltcid == tcid.longValue()) {
					return true;
				}
			}
		}
		Integer[] tcidsFromName =
				QMetry7ResultUpdator.extractNums((String) params.get("name"));
		for (Integer tcid : tcidsFromName) {
			if (xmltcid == tcid.longValue()) {
				return true;
			}
		}

		return false;
	}

}