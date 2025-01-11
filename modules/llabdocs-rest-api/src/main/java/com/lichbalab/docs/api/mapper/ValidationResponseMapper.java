package com.lichbalab.docs.api.mapper;

import com.lichbalab.docs.api.model.DocumentSignatureValidationResult;
import com.lichbalab.docs.api.model.SignatureValidationResult;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDistinguishedName;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static javax.security.auth.x500.X500Principal.RFC2253;

@Service
public class ValidationResponseMapper {

    private final MessageSource signatureIndicationMessageSource;

    @Autowired
    public ValidationResponseMapper( @Qualifier("signatureIndicationMessageSource") MessageSource signatureIndicationMessageSource) {
        this.signatureIndicationMessageSource = signatureIndicationMessageSource;
    }

    public DocumentSignatureValidationResult toValidationResult(WSReportsDTO reportsDTO, Locale locale) {
        DocumentSignatureValidationResult result = new DocumentSignatureValidationResult();

        List<SignatureValidationResult> signatures = new ArrayList<>();
        XmlDiagnosticData data = reportsDTO.getDiagnosticData();
        if (data != null && data.getSignatures() != null) {
            signatures.addAll(data.getSignatures().stream().map(ValidationResponseMapper::buildValidationSignatureFromDigestSignature).toList());
        }

        XmlSimpleReport report = reportsDTO.getSimpleReport();
        List<XmlToken> tokens = report.getSignatureOrTimestampOrEvidenceRecord();

        for (XmlToken token : tokens) {
            if (!(token instanceof eu.europa.esig.dss.simplereport.jaxb.XmlSignature)) {
                continue;
            }

            for (SignatureValidationResult signature : signatures) {
                if (signature.getSignatureId().equals(token.getId())){
                    enrichValidationResultWithReportSignature(signature, (eu.europa.esig.dss.simplereport.jaxb.XmlSignature) token, locale);
                }
            }
        }
        result.setSignatures(signatures);
        result.setDocumentName(report.getDocumentName());

        DocumentSignatureValidationResult.ValidationSummary summary = new DocumentSignatureValidationResult.ValidationSummary();
        summary.setValidSignatures(report.getValidSignaturesCount());
        summary.setTotalSignatures(report.getSignaturesCount());
        result.setSummary(summary);

        return result;
    }

    private void enrichValidationResultWithReportSignature(SignatureValidationResult result, eu.europa.esig.dss.simplereport.jaxb.XmlSignature signature, Locale locale) {
        result.setIndication(signature.getIndication().name());
        if (signature.getSubIndication() != null) {
            result.setIndicationDetails(signatureIndicationMessageSource.getMessage(
                    signature.getSubIndication().name(),
                    new Object[]{}, "", locale
            ));
        }

        SignatureValidationResult.ValidationDetails details = new SignatureValidationResult.ValidationDetails();
        XmlDetails xmlDetails = signature.getAdESValidationDetails();
        if (xmlDetails != null) {
            details.setErrors(xmlDetails.getError().stream().map(XmlMessage::getValue).toList());
            details.setWarns(xmlDetails.getWarning().stream().map(XmlMessage::getValue).toList());
            details.setInfos(xmlDetails.getWarning().stream().map(XmlMessage::getValue).toList());
        }
        result.setSignatureDetails(details);
    }

    private static SignatureValidationResult buildValidationSignatureFromDigestSignature(XmlSignature signature) {
        SignatureValidationResult result = new SignatureValidationResult();

        result.setSignatureId(signature.getId());
        /*
        // Set basic signature information
        result.setIndication(signature.getSignatureValidationReport().getConclusion().getIndication().name());
        if (signature.getSignatureValidationReport().getConclusion().getSubIndication() != null) {
            result.setIndicationDetails(signature.getSignatureValidationReport().getConclusion().getSubIndication().name());
        }
*/

        // Set signer info
        if (signature.getSigningCertificate() != null && signature.getSigningCertificate().getCertificate() != null) {
            result.setSigner(signature.getSigningCertificate().getCertificate().getCommonName());
        }
        
        // Set times
        if (signature.getClaimedSigningTime() != null) {
            result.setSigningTime(signature.getClaimedSigningTime());
        }
        result.setValidationTime(new Date());
        
        // Set signing certificate info
        if (signature.getSigningCertificate() != null) {
            SignatureValidationResult.CertificateInfo certInfo = buildCertificateInfo(
                signature.getSigningCertificate().getCertificate());
            result.setSigningCertificate(certInfo);
        }
        
        // Set certificate chain
        if (signature.getCertificateChain() != null) {
            result.setCertificateChain(signature.getCertificateChain().stream()
                .map(cert -> buildCertificateInfo(cert.getCertificate()))
                .toList());
        }
        
        // Set signature details
/*
        SignatureValidationResult.ValidationDetails details = new SignatureValidationResult.ValidationDetails();
        if (signature.getSignatureValidationReport() != null && 
            signature.getSignatureValidationReport().getConclusion() != null) {
            details.setErrors(signature.getSignatureValidationReport().getConclusion().getErrors());
            details.setWarns(signature.getSignatureValidationReport().getConclusion().getWarnings());
            details.setInfos(signature.getSignatureValidationReport().getConclusion().getInfos());
        }
        result.setSignatureDetails(details);
*/

        // Set qualification info
/*
        SignatureValidationResult.QualificationInfo qualificationInfo = new SignatureValidationResult.QualificationInfo();
        if (signature.getSignatureQualification() != null) {
            qualificationInfo.setLevel(signature.getSignatureQualification().name());
            qualificationInfo.setDescription(getQualificationDescription(signature.getSignatureQualification()));
            
            SignatureValidationResult.ValidationDetails qualificationDetails = new SignatureValidationResult.ValidationDetails();
            if (signature.getValidationReport() != null && 
                signature.getValidationReport().getConclusion() != null) {
                qualificationDetails.setErrors(signature.getValidationReport().getConclusion().getErrors());
                qualificationDetails.setWarns(signature.getValidationReport().getConclusion().getWarnings());
                qualificationDetails.setInfos(signature.getValidationReport().getConclusion().getInfos());
            }
            qualificationInfo.setDetails(qualificationDetails);
        }
        result.setQualification(qualificationInfo);
*/

        return result;
    }
    
    private static SignatureValidationResult.CertificateInfo buildCertificateInfo(XmlCertificate certificate) {
        SignatureValidationResult.CertificateInfo certInfo = new SignatureValidationResult.CertificateInfo();
        
        XmlDistinguishedName xmlSubjectName = certificate.getSubjectDistinguishedName().stream().filter(xdm -> xdm.getFormat().equals(RFC2253)).findAny().orElse(null);
        if (xmlSubjectName != null) {
            certInfo.setSubject(xmlSubjectName.getValue());
        }

        XmlDistinguishedName xmlIssuerName = certificate.getIssuerDistinguishedName().stream().filter(xdm -> xdm.getFormat().equals(RFC2253)).findAny().orElse(null);
        if (xmlIssuerName != null) {
            certInfo.setIssuer(xmlIssuerName.getValue());
        }
        certInfo.setSerialNumber(String.valueOf(certificate.getSerialNumber()));
        
        if (certificate.getNotBefore() != null) {
            certInfo.setValidFrom( certificate.getNotBefore());
        }
        
        if (certificate.getNotAfter() != null) {
            certInfo.setValidTo(certificate.getNotAfter());
        }
        
        return certInfo;
    }
    
    private String getQualificationDescription(SignatureQualification qualification) {
        return switch (qualification) {
            case QESIG -> "Qualified Electronic Signature";
            case ADESIG_QC -> "Advanced Electronic Signature with Qualified Certificate";
            case ADESIG -> "Advanced Electronic Signature";
            case INDETERMINATE_QESIG -> "Indeterminate Qualified Electronic Signature";
            case INDETERMINATE_ADESIG_QC -> "Indeterminate Advanced Electronic Signature with Qualified Certificate";
            case INDETERMINATE_ADESIG -> "Indeterminate Advanced Electronic Signature";
            case NOT_ADES -> "Not Advanced Electronic Signature";
            default -> "Unknown Qualification";
        };
    }


}
