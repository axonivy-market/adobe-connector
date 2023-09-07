package com.axonivy.connector.adobe.esign.connector.auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import ch.ivyteam.ivy.bpm.error.BpmPublicErrorBuilder;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.FeatureConfig;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2BearerFilter;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2RedirectErrorBuilder;
import ch.ivyteam.ivy.rest.client.oauth2.OAuth2TokenRequester.AuthContext;
import ch.ivyteam.ivy.rest.client.oauth2.uri.OAuth2CallbackUriBuilder;

/**
 * @since 9.2
 */
public class OAuth2Feature implements Feature {

  public static interface Property {

    String CLIENT_ID = "AUTH.clientId";
    String CLIENT_SECRET = "AUTH.clientSecret";
    String AUTHORIZATION_CODE = "AUTH.code";
    String SCOPE = "AUTH.scope";
    String AUTH_BASE_URI = "AUTH.baseUri";
    String AUTH_INTEGRATION_KEY = "AUTH.integrationKey";
  }

  public static final String SESSION_TOKEN = "adobe.esign.authCode";

  @Override
  public boolean configure(FeatureContext context) {
	
    var config = new FeatureConfig(context.getConfiguration(), OAuth2Feature.class);
	var docuSignUri = new OAuth2UriProperty(config, Property.AUTH_BASE_URI,
			"https://api.eu2.echosign.com/oauth/v2");
	var oauth2 = new OAuth2BearerFilter(
			ctxt -> requestToken(ctxt, docuSignUri),
			docuSignUri);
	context.register(oauth2, Priorities.AUTHORIZATION);
	    context.register(BearerTokenAuthorizationFilter.class, Priorities.AUTHORIZATION - 10);
    return true;
  }

  /**
   * Get token.
   *
   * @param ctxt
   * @param uriFactory
   */
  private static Response requestToken(AuthContext ctxt, OAuth2UriProperty uriFactory) {
	  String authCode = ctxt.authCode().orElse("");
	  var refreshToken = ctxt.refreshToken();
	  if (authCode.isEmpty() && refreshToken.isEmpty()) {
		  try {
			authRedirectError(ctxt.config, uriFactory).throwError();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UriBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  var clientId = ctxt.config.readMandatory(Property.CLIENT_ID);
	  var clientSecret = ctxt.config.readMandatory(Property.CLIENT_SECRET);
	  URI tokenUri = uriFactory.getTokenUri();
	  Response response = null;
	  if (!refreshToken.isPresent()) {
		  AccessTokenRequest authRequest = new AccessTokenRequest(authCode, clientId, clientSecret, OAuth2CallbackUriBuilder.create().toUrl().toString());
		  response = ctxt.target
				  .request()
				  .post(Entity.form(authRequest.paramsMap()));
	  } else {
		  RefreshTokenRequest authRequest = new RefreshTokenRequest(refreshToken.get(), clientId, clientSecret);
		  tokenUri = uriFactory.getRefreshUri();
		  response = ctxt.target
				  .request()
				  .post(Entity.form(authRequest.paramsMap()));
	  }
	  return response;
  }
  

  /**
   * Use the JWT grant?
   * @param use
   */
  public static boolean isTrue(Optional<String> use) {
    return use.filter(val -> !val.isBlank() && Boolean.parseBoolean(val)).isPresent();
  }

  public static class AccessTokenRequest {

    public String grant_type;
    public String client_id;
    public String client_secret;
    public String redirect_uri;
    public String code;

    public AccessTokenRequest(String code, String client_id, String client_secret, String redirect_uri) {
      this.grant_type = "authorization_code";
      this.client_id = client_id;
      this.client_secret = client_secret;
      this.redirect_uri = redirect_uri;
      this.code = code;
    }
    
    public MultivaluedMap<String, String> paramsMap() {
    	MultivaluedMap<String, String> values = new MultivaluedHashMap<>();
    	values.put("code", Arrays.asList(code));
    	values.put("client_id", Arrays.asList(client_id));
    	values.put("client_secret", Arrays.asList(client_secret));
    	values.put("redirect_uri", Arrays.asList(redirect_uri));
    	values.put("grant_type", Arrays.asList(grant_type));
    	return values;
    }
    
	@Override
	public String toString() {
		return String.format("""
				code=%s
				client_id=%s&
				client_secret=%s&
				redirect_uri=%s
				grant_type=%s
				""", code, client_id, client_secret, redirect_uri, grant_type);
	}
    
    
  }

  public static class RefreshTokenRequest {

    public String grant_type;
    public String refresh_token;
    public String client_id;
    public String client_secret;

    public RefreshTokenRequest(String refreshToken, String client_id, String client_secret) {
      this.grant_type = "refresh_token";
      this.refresh_token = refreshToken;
      this.client_id = client_id;
      this.client_secret = client_secret;
    }
    
    public MultivaluedMap<String, String> paramsMap() {
    	MultivaluedMap<String, String> values = new MultivaluedHashMap<>();
    	values.put("client_id", Arrays.asList(client_id));
    	values.put("client_secret", Arrays.asList(client_secret));
    	values.put("refresh_token", Arrays.asList(refresh_token));
    	values.put("grant_type", Arrays.asList(grant_type));
    	return values;
    }
    
    @Override
	public String toString() {
		return String.format("""
				client_id=%s&
				client_secret=%s&
				refresh_token=%s
				grant_type=%s
				""", client_id, client_secret, refresh_token, grant_type);
	}
  }

  private static BpmPublicErrorBuilder authRedirectError(FeatureConfig config, OAuth2UriProperty uriFactory) throws IllegalArgumentException, UriBuilderException, URISyntaxException {
    URI redirectUri = OAuth2CallbackUriBuilder.create().toUrl();
    var uri = UriBuilder.fromUri(new URI(Ivy.var().get("adobe-sign-connector.authenticationUri")))
    		.queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("client_id", config.readMandatory(Property.CLIENT_ID))
            .queryParam("scope", getScope(config))
            .build();
    Ivy.log().debug("created oauth URI: " + uri);
    return OAuth2RedirectErrorBuilder
            .create(uri)
            .withMessage("Missing permission from user to act in his name.");
  }

  static String getScope(FeatureConfig config) {
    return config.read(Property.SCOPE).orElse("signature impersonation");
  }
  
}