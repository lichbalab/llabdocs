package com.lichbalab.docs.signature;

import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;

public interface SignatureValidationServiceLLab {

    WSReportsDTO validateSignature(byte[] signedDocument, String documentName);

    String validateSignatureSimpleHtmlReport(byte[] signedDocument, String documentName);
}
