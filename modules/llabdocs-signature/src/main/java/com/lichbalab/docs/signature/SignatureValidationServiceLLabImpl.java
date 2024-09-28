package com.lichbalab.docs.signature;

import eu.europa.esig.dss.simplereport.SimpleReportFacade;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import eu.europa.esig.dss.ws.validation.dto.DataToValidateDTO;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

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
