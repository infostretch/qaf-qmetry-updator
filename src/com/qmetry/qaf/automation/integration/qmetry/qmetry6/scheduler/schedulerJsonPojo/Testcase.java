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

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Testcase {

	@Expose
	private long tcrunId;
	@Expose
	private long testcaseId;
	@Expose
	private long tcversion;
	@Expose
	private String testcaseName;

	/**
	 * @return The tcrunId
	 */
	public long getTcrunId() {
		return tcrunId;
	}

	/**
	 * @param tcrunId
	 *            The tcrunId
	 */
	public void setTcrunId(long tcrunId) {
		this.tcrunId = tcrunId;
	}

	public Testcase withTcrunId(long tcrunId) {
		this.tcrunId = tcrunId;
		return this;
	}

	/**
	 * @return The testcaseId
	 */
	public long getTestcaseId() {
		return testcaseId;
	}

	/**
	 * @param testcaseId
	 *            The testcaseId
	 */
	public void setTestcaseId(long testcaseId) {
		this.testcaseId = testcaseId;
	}

	public Testcase withTestcaseId(long testcaseId) {
		this.testcaseId = testcaseId;
		return this;
	}

	/**
	 * @return The tcversion
	 */
	public long getTcversion() {
		return tcversion;
	}

	/**
	 * @param tcversion
	 *            The tcversion
	 */
	public void setTcversion(long tcversion) {
		this.tcversion = tcversion;
	}

	public Testcase withTcversion(long tcversion) {
		this.tcversion = tcversion;
		return this;
	}

	/**
	 * @return The testcaseName
	 */
	public String getTestcaseName() {
		return testcaseName;
	}

	/**
	 * @param testcaseName
	 *            The testcaseName
	 */
	public void setTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
	}

	public Testcase withTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(tcrunId).append(testcaseId).append(tcversion).append(testcaseName)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Testcase) == false) {
			return false;
		}
		Testcase rhs = ((Testcase) other);
		return new EqualsBuilder().append(tcrunId, rhs.tcrunId).append(testcaseId, rhs.testcaseId)
				.append(tcversion, rhs.tcversion).append(testcaseName, rhs.testcaseName).isEquals();
	}

}
