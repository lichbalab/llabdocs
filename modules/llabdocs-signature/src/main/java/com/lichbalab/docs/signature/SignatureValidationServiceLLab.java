package com.lichbalab.docs.signature;

import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;

public interface SignatureValidationServiceLLab {

    WSReportsDTO validateQesSignature(byte[] signedDocument, String documentName);

    WSReportsDTO validateAdesSignature(byte[] signedDocument, String documentName);

    String validateSignatureSimpleHtmlReport(byte[] signedDocument, String documentName);

}