package com.lichbalab.docs.signature;

import eu.europa.esig.dss.simplereport.SimpleReportFacade;
import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import eu.europa.esig.dss.ws.validation.dto.DataToValidateDTO;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignatureValidationServiceLLabImpl implements SignatureValidationServiceLLab {

    public RemoteDocumentValidationService validationService;

    @Autowired
    public SignatureValidationServiceLLabImpl(RemoteDocumentValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public WSReportsDTO validateSignature(byte[] signedDocument) {
        DataToValidateDTO dataToValidateDTO = new DataToValidateDTO();
        RemoteDocument remoteDocument = new RemoteDocument();
        remoteDocument.setBytes(signedDocument);
        dataToValidateDTO.setSignedDocument(remoteDocument);

        return validationService.validateDocument(dataToValidateDTO);
    }

    public String validateSignatureSimpleHtmlReport(byte[] signedDocument) {
        SimpleReportFacade simpleReportFacade = SimpleReportFacade.newFacade();
        WSReportsDTO reportsDTO = validateSignature(signedDocument);
        try {
            return simpleReportFacade.generateHtmlReport(reportsDTO.getSimpleReport());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
