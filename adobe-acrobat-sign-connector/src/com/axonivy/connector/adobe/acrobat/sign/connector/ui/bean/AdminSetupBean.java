package com.axonivy.connector.adobe.acrobat.sign.connector.ui.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.axonivy.connector.adobe.acrobat.sign.connector.enums.AdobeVariable;
import com.axonivy.connector.adobe.acrobat.sign.connector.service.AdminSetupService;

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
