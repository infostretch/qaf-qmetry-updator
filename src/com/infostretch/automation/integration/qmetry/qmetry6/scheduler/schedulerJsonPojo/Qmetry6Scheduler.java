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
package com.infostretch.automation.integration.qmetry.qmetry6.scheduler.schedulerJsonPojo;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Qmetry6Scheduler {

	@Expose
	private Schedule schedule;

	/**
	 * @return The schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule
	 *            The schedule
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Qmetry6Scheduler withSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(schedule).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Qmetry6Scheduler) == false) {
			return false;
		}
		Qmetry6Scheduler rhs = ((Qmetry6Scheduler) other);
		return new EqualsBuilder().append(schedule, rhs.schedule).isEquals();
	}

}
