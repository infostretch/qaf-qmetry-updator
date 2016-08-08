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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Use this annotation to override default parameters at class or test level. If
 * Scheduler xml file is provided it will use this parameters for filter.
 * 
 * @author chirag
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ METHOD, TYPE })
public @interface QmetryTestCase {
	/**
	 * Test case id to be mapped with QMetry test case.
	 * 
	 * @return
	 */
	/**
	 * map with QMetry test case id. separate with comma if more than one test
	 * case id to map
	 */
	String TC_ID() default "";

	/**
	 * @return
	 */
	String build() default "";

	String project() default "";

	String release() default "";

	/**
	 * map with QMetry test case run id. separate with comma if more than one
	 * run id to map
	 * 
	 * @return
	 */
	String runId() default "";

	String testScriptName() default "";

	/**
	 * mark for not to map with QMetry
	 * 
	 * @return
	 */
	boolean skip() default false;
}
