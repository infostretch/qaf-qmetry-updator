/*******************************************************************************
 * Copyright 2016 Infostretch Corporation.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qmetry.qaf.automation.integration.qmetry.qmetry6.scheduler.schedulerJsonPojo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Schedule {

	@Expose
	private long schdeuleId;
	@Expose
	private long testsuiteId;
	@Expose
	private String testsuiteName;
	@Expose
	private long buildId;
	@Expose
	private String buildName;
	@Expose
	private long projectId;
	@Expose
	private String projectName;
	@Expose
	private long dropId;
	@Expose
	private Runstats runstats;
	@Expose
	private long releaseId;
	@Expose
	private String releaseName;
	@Expose
	private long platformId;
	@Expose
	private String platformName;
	@Expose
	private long testsuiteRunId;
	@Expose
	private List<Testcase> testcases = new ArrayList<Testcase>();

	/**
	 * @return The schdeuleId
	 */
	public long getSchdeuleId() {
		return schdeuleId;
	}

	/**
	 * @param schdeuleId
	 *            The schdeuleId
	 */
	public void setSchdeuleId(long schdeuleId) {
		this.schdeuleId = schdeuleId;
	}

	public Schedule withSchdeuleId(long schdeuleId) {
		this.schdeuleId = schdeuleId;
		return this;
	}

	/**
	 * @return The testsuiteId
	 */
	public long getTestsuiteId() {
		return testsuiteId;
	}

	/**
	 * @param testsuiteId
	 *            The testsuiteId
	 */
	public void setTestsuiteId(long testsuiteId) {
		this.testsuiteId = testsuiteId;
	}

	public Schedule withTestsuiteId(long testsuiteId) {
		this.testsuiteId = testsuiteId;
		return this;
	}

	/**
	 * @param dropId
	 *            The dropId
	 */
	public void setDropId(long dropId) {
		this.dropId = dropId;
	}

	public long getDropId() {
		return dropId;
	}

	/**
	 * @return The testsuiteName
	 */
	public String getTestsuiteName() {
		return testsuiteName;
	}

	/**
	 * @param testsuiteName
	 *            The testsuiteName
	 */
	public void setTestsuiteName(String testsuiteName) {
		this.testsuiteName = testsuiteName;
	}

	public Schedule withTestsuiteName(String testsuiteName) {
		this.testsuiteName = testsuiteName;
		return this;
	}

	/**
	 * @return The buildId
	 */
	public long getBuildId() {
		return buildId;
	}

	/**
	 * @param buildId
	 *            The buildId
	 */
	public void setBuildId(long buildId) {
		this.buildId = buildId;
	}

	public Schedule withBuildId(long buildId) {
		this.buildId = buildId;
		return this;
	}

	/**
	 * @return The buildName
	 */
	public String getBuildName() {
		return buildName;
	}

	/**
	 * @param buildName
	 *            The buildName
	 */
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public Schedule withBuildName(String buildName) {
		this.buildName = buildName;
		return this;
	}

	/**
	 * @return The projectId
	 */
	public long getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId
	 *            The projectId
	 */
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public Schedule withProjectId(long projectId) {
		this.projectId = projectId;
		return this;
	}

	/**
	 * @return The projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            The projectName
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Schedule withProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	/**
	 * @return The runstats
	 */
	public Runstats getRunstats() {
		return runstats;
	}

	/**
	 * @param runstats
	 *            The runstats
	 */
	public void setRunstats(Runstats runstats) {
		this.runstats = runstats;
	}

	public Schedule withRunstats(Runstats runstats) {
		this.runstats = runstats;
		return this;
	}

	/**
	 * @return The releaseId
	 */
	public long getReleaseId() {
		return releaseId;
	}

	/**
	 * @param releaseId
	 *            The releaseId
	 */
	public void setReleaseId(long releaseId) {
		this.releaseId = releaseId;
	}

	public Schedule withReleaseId(long releaseId) {
		this.releaseId = releaseId;
		return this;
	}

	/**
	 * @return The releaseName
	 */
	public String getReleaseName() {
		return releaseName;
	}

	/**
	 * @param releaseName
	 *            The releaseName
	 */
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}

	public Schedule withReleaseName(String releaseName) {
		this.releaseName = releaseName;
		return this;
	}

	/**
	 * @return The platformId
	 */
	public long getPlatformId() {
		return platformId;
	}

	/**
	 * @param platformId
	 *            The platformId
	 */
	public void setPlatformId(long platformId) {
		this.platformId = platformId;
	}

	public Schedule withPlatformId(long platformId) {
		this.platformId = platformId;
		return this;
	}

	/**
	 * @return The platformName
	 */
	public String getPlatformName() {
		return platformName;
	}

	/**
	 * @param platformName
	 *            The platformName
	 */
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public Schedule withPlatformName(String platformName) {
		this.platformName = platformName;
		return this;
	}

	/**
	 * @return The testsuiteRunId
	 */
	public long getTestsuiteRunId() {
		return testsuiteRunId;
	}

	/**
	 * @param testsuiteRunId
	 *            The testsuiteRunId
	 */
	public void setTestsuiteRunId(long testsuiteRunId) {
		this.testsuiteRunId = testsuiteRunId;
	}

	public Schedule withTestsuiteRunId(long testsuiteRunId) {
		this.testsuiteRunId = testsuiteRunId;
		return this;
	}

	/**
	 * @return The testcases
	 */
	public List<Testcase> getTestcases() {
		return testcases;
	}

	/**
	 * @param testcases
	 *            The testcases
	 */
	public void setTestcases(List<Testcase> testcases) {
		this.testcases = testcases;
	}

	public Schedule withTestcases(List<Testcase> testcases) {
		this.testcases = testcases;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(schdeuleId).append(testsuiteId).append(testsuiteName).append(buildId)
				.append(buildName).append(projectId).append(projectName).append(runstats).append(releaseId)
				.append(releaseName).append(platformId).append(platformName).append(testsuiteRunId).append(testcases)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Schedule) == false) {
			return false;
		}
		Schedule rhs = ((Schedule) other);
		return new EqualsBuilder().append(schdeuleId, rhs.schdeuleId).append(testsuiteId, rhs.testsuiteId)
				.append(testsuiteName, rhs.testsuiteName).append(buildId, rhs.buildId).append(buildName, rhs.buildName)
				.append(projectId, rhs.projectId).append(projectName, rhs.projectName).append(runstats, rhs.runstats)
				.append(releaseId, rhs.releaseId).append(releaseName, rhs.releaseName)
				.append(platformId, rhs.platformId).append(platformName, rhs.platformName)
				.append(testsuiteRunId, rhs.testsuiteRunId).append(testcases, rhs.testcases).isEquals();
	}

}
