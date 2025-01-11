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

    public RemoteDocumentValidationService validationService;
    public RemoteDocumentValidationService adesValidationService;

    @Autowired
    public SignatureValidationServiceLLabImpl(
            @Qualifier("remoteValidationService") RemoteDocumentValidationService validationService,
            @Qualifier("remoteAdesValidationService") RemoteDocumentValidationService adesValidationService
    ) {
        this.validationService = validationService;
        this.adesValidationService = adesValidationService;
    }

    @Override
    public WSReportsDTO validateSignature(byte[] signedDocument, String documentName, boolean getQualification) {
        DataToValidateDTO dataToValidateDTO = new DataToValidateDTO();
        RemoteDocument remoteDocument = new RemoteDocument();
        remoteDocument.setBytes(signedDocument);
        remoteDocument.setName(documentName);
        dataToValidateDTO.setSignedDocument(remoteDocument);

        if (getQualification) {
            return validationService.validateDocument(dataToValidateDTO);
        }
        return adesValidationService.validateDocument(dataToValidateDTO);
    }


    public String validateSignatureSimpleHtmlReport(byte[] signedDocument, String documentName) {
        SimpleReportFacade simpleReportFacade = SimpleReportFacade.newFacade();
        WSReportsDTO reportsDTO = validateSignature(signedDocument, documentName, false);
        try {
            return simpleReportFacade.generateHtmlReport(reportsDTO.getSimpleReport());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
