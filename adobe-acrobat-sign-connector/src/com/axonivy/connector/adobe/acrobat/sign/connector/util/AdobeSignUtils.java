package com.axonivy.connector.adobe.acrobat.sign.connector.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import api.rest.v6.client.SigningUrlResponseSigningUrlSetInfos;

public class AdobeSignUtils {

	/**
	 * Retrieves the e-signature URL associated with a specific signer's email.
	 *
	 * This method iterates through a list of signing URL information, checking each
	 * set for a match with the provided email. If a match is found, the
	 * corresponding e-signature URL is returned. If no match is found, or if the
	 * input parameters are invalid (null or empty), an empty string is returned.
	 *
	 * @param signerEmail                The email address of the signer whose
	 *                                   e-sign URL is being requested. This must
	 *                                   not be null or blank.
	 * @param responseSigningUrlSetInfos A list of
	 *                                   {@link SigningUrlResponseSigningUrlSetInfos}
	 *                                   which contains the signing URLs and
	 *                                   associated signer emails. This list must
	 *                                   not be empty.
	 * @return The e-sign URL as a String. Returns an empty string if no match is
	 *         found or if input parameters are invalid.
	 */
	public static String getESignUrlByEmail(String signerEmail,
			List<SigningUrlResponseSigningUrlSetInfos> responseSigningUrlSetInfos) {
		var signingURI = EMPTY;
		if (isBlank(signerEmail) || CollectionUtils.isEmpty(responseSigningUrlSetInfos)) {
			return signingURI;
		}
		for (var signingInfos : responseSigningUrlSetInfos) {
			for (var signingUrls : CollectionUtils.emptyIfNull(signingInfos.getSigningUrls())) {
				if (equalsIgnoreCase(signingUrls.getEmail(), signerEmail)) {
					signingURI = signingUrls.getEsignUrl();
				}
			}
		}
		return signingURI;
	}

}
