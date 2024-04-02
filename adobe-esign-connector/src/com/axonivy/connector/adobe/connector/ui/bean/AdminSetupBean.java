package com.axonivy.connector.adobe.connector.ui.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.axonivy.connector.adobe.connector.enums.AdobeVariable;
import com.axonivy.connector.adobe.connector.service.AdminSetupService;

@ManagedBean
@ViewScoped
public class AdminSetupBean {
	public String getVariableName(AdobeVariable var) {
		return var.getVariableName();
	}

	public String getRedirectUri() {
		return AdminSetupService.createRedirectUrl();
	}
}
