package com.qmetry.qaf.automation.integration.qmetry.qmetry6.testhelper;

public enum Status {
	Passed("Passed"), Failed("Failed"), NotRun("Not Run");
	final String text;

	/**
	 * @param text
	 * @return
	 */
	Status(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}