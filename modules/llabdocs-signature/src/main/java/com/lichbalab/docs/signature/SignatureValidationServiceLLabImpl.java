package com.lichbalab.docs.signature;

import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import eu.europa.esig.dss.ws.validation.dto.DataToValidateDTO;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.stereotype.Service;

@Service
public class SignatureValidationServiceLLabImpl implements SignatureValidationServiceLLab {

    private final RemoteDocumentValidationService validationService;

    public SignatureValidationServiceLLabImpl() {
        this.validationService = new RemoteDocumentValidationService();
        this.validationService.setVerifier(new CommonCertificateVerifier());
    }

    @Override
    public WSReportsDTO validateSignature(byte[] signedDocument) {
        DataToValidateDTO dataToValidateDTO = new DataToValidateDTO();
        RemoteDocument remoteDocument = new RemoteDocument();
        remoteDocument.setBytes(signedDocument);
        dataToValidateDTO.setSignedDocument(remoteDocument);

        return validationService.validateDocument(dataToValidateDTO);
    }
}
