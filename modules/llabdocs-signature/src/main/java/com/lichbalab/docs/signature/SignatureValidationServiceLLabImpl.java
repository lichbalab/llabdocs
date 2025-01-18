package com.lichbalab.docs.signature;

import eu.europa.esig.dss.simplereport.SimpleReportFacade;
import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import eu.europa.esig.dss.ws.validation.dto.DataToValidateDTO;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SignatureValidationServiceLLabImpl implements SignatureValidationServiceLLab {

    public RemoteDocumentValidationService qesValidationService;
    public RemoteDocumentValidationService adesValidationService;

    @Autowired
    public SignatureValidationServiceLLabImpl(
            @Qualifier("remoteQesValidationService") RemoteDocumentValidationService qesValidationService,
            @Qualifier("remoteAdesValidationService") RemoteDocumentValidationService adesValidationService
    ) {
        this.qesValidationService = qesValidationService;
        this.adesValidationService = adesValidationService;
    }

    @Override
    public WSReportsDTO validateQesSignature(byte[] signedDocument, String documentName) {
        return qesValidationService.validateDocument(prepareDataToValidate(signedDocument, documentName));
    }

    @Override
    public WSReportsDTO validateAdesSignature(byte[] signedDocument, String documentName) {
        return adesValidationService.validateDocument(prepareDataToValidate(signedDocument, documentName));
    }

    public String validateSignatureSimpleHtmlReport(byte[] signedDocument, String documentName) {
        SimpleReportFacade simpleReportFacade = SimpleReportFacade.newFacade();
        WSReportsDTO reportsDTO = validateQesSignature(signedDocument, documentName);
        try {
            return simpleReportFacade.generateHtmlReport(reportsDTO.getSimpleReport());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private DataToValidateDTO prepareDataToValidate(byte[] signedDocument, String documentName) {
        DataToValidateDTO dataToValidateDTO = new DataToValidateDTO();
        RemoteDocument remoteDocument = new RemoteDocument();
        remoteDocument.setBytes(signedDocument);
        remoteDocument.setName(documentName);
        dataToValidateDTO.setSignedDocument(remoteDocument);
        return dataToValidateDTO;
    }
}
