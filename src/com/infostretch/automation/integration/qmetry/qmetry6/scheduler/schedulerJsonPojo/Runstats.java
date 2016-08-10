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

package com.infostretch.automation.integration.qmetry.qmetry6.scheduler.schedulerJsonPojo;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Runstats {

	@Expose
	private long uncovered;
	@Expose
	private long blocked;
	@Expose
	private long failed;
	@Expose
	private long notrun;
	@Expose
	private long passed;
	@Expose
	private long empty;
	@Expose
	private long emptyfolder;

	/**
	 * @return The uncovered
	 */
	public long getUncovered() {
		return uncovered;
	}

	/**
	 * @param uncovered
	 *            The uncovered
	 */
	public void setUncovered(long uncovered) {
		this.uncovered = uncovered;
	}

	public Runstats withUncovered(long uncovered) {
		this.uncovered = uncovered;
		return this;
	}

	/**
	 * @return The blocked
	 */
	public long getBlocked() {
		return blocked;
	}

	/**
	 * @param blocked
	 *            The blocked
	 */
	public void setBlocked(long blocked) {
		this.blocked = blocked;
	}

	public Runstats withBlocked(long blocked) {
		this.blocked = blocked;
		return this;
	}

	/**
	 * @return The failed
	 */
	public long getFailed() {
		return failed;
	}

	/**
	 * @param failed
	 *            The failed
	 */
	public void setFailed(long failed) {
		this.failed = failed;
	}

	public Runstats withFailed(long failed) {
		this.failed = failed;
		return this;
	}

	/**
	 * @return The notrun
	 */
	public long getNotrun() {
		return notrun;
	}

	/**
	 * @param notrun
	 *            The notrun
	 */
	public void setNotrun(long notrun) {
		this.notrun = notrun;
	}

	public Runstats withNotrun(long notrun) {
		this.notrun = notrun;
		return this;
	}

	/**
	 * @return The passed
	 */
	public long getPassed() {
		return passed;
	}

	/**
	 * @param passed
	 *            The passed
	 */
	public void setPassed(long passed) {
		this.passed = passed;
	}

	public Runstats withPassed(long passed) {
		this.passed = passed;
		return this;
	}

	/**
	 * @return The empty
	 */
	public long getEmpty() {
		return empty;
	}

	/**
	 * @param empty
	 *            The empty
	 */
	public void setEmpty(long empty) {
		this.empty = empty;
	}

	public Runstats withEmpty(long empty) {
		this.empty = empty;
		return this;
	}

	/**
	 * @return The emptyfolder
	 */
	public long getEmptyfolder() {
		return emptyfolder;
	}

	/**
	 * @param emptyfolder
	 *            The emptyfolder
	 */
	public void setEmptyfolder(long emptyfolder) {
		this.emptyfolder = emptyfolder;
	}

	public Runstats withEmptyfolder(long emptyfolder) {
		this.emptyfolder = emptyfolder;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(uncovered).append(blocked).append(failed).append(notrun).append(passed)
				.append(empty).append(emptyfolder).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Runstats) == false) {
			return false;
		}
		Runstats rhs = ((Runstats) other);
		return new EqualsBuilder().append(uncovered, rhs.uncovered).append(blocked, rhs.blocked)
				.append(failed, rhs.failed).append(notrun, rhs.notrun).append(passed, rhs.passed)
				.append(empty, rhs.empty).append(emptyfolder, rhs.emptyfolder).isEquals();
	}

}
