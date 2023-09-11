package com.axonivy.connector.adobe.esign.connector.auth.oauth;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ch.ivyteam.ivy.environment.Ivy;

public class VarTokenStore {
	private final String varName;

	public static VarTokenStore get(String varName) {
		return new VarTokenStore(varName);
	}
	
	VarTokenStore(String varName) {
		this.varName = varName;
	}
	
	public Token getToken() {
		String tokenVar = Ivy.var().get(varName);
		Gson gson = new Gson();
		Token token = null;
		try {
			token = gson.fromJson(tokenVar, Token.class);
		} catch (JsonSyntaxException e) {
			Ivy.log().warn("Couldn't create Token object from variable", e);
		}
		return token;
	}
	
	public void setToken(Token token) {
		Gson gson = new Gson();
		String tokenString = gson.toJson(token);
		Ivy.var().set(varName, tokenString);
	}
}
