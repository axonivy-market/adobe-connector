package com.axonivy.connector.adobe.acrobat.sign.connector.enums;

import ch.ivyteam.ivy.environment.Ivy;

public enum AdobeVariable {
	BASE_URI("adobe-acrobat-sign-connector.baseUri"),
	HOST("adobe-acrobat-sign-connector.host"),
	INTEGRATION_KEY("adobe-acrobat-sign-connector.integrationKey"),
	RETURN_PAGE("adobe-acrobat-sign-connector.returnPage"),
	PERMISSIONS("adobe-acrobat-sign-connector.permissions"),
	CLIENT_ID("adobe-acrobat-sign-connector.clientId"),
	CLIENT_SECRET("adobe-acrobat-sign-connector.clientSecret"),
	APP_ID("adobe-acrobat-sign-connector.appId"),
	SECRET_KEY("adobe-acrobat-sign-connector.secretKey"),
	USE_APP_PERMISSIONS("adobe-acrobat-sign-connector.useAppPermissions"),
	CODE("adobe-acrobat-sign-connector.code"),
	USE_USER_PASS_FLOW_ENABLED("adobe-acrobat-sign-connector.useUserPassFlow.enabled"),
	USE_USER_PASS_FLOW_USER("adobe-acrobat-sign-connector.useUserPassFlow.user"),
	USE_USER_PASS_FLOW_PASS("adobe-acrobat-sign-connector.useUserPassFlow.pass"),
	OAUTH_TOKEN("adobe-acrobat-sign-connector.oauthToken"),
	ACCESS_TOKEN("adobe-acrobat-sign-connector.accessToken"),
	AUTHENTICATION_URI("adobe-acrobat-sign-connector.authenticationUri");

	private String variableName;

	private AdobeVariable(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getValue() {
		return Ivy.var().get(variableName);
	}

	/**
	 * Sets new value to the Variable
	 * @param newValue
	 * @return old value that was set before. Empty string if it was not defined.
	 */
	public String updateValue(String newValue) {
		return Ivy.var().set(variableName, newValue);
	}
}
