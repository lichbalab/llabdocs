package com.lichbalab.docs.api.mapper;

import com.lichbalab.docs.api.model.AdesSignatureValidationResult;
import com.lichbalab.docs.api.model.DocumentAdesSignatureValidationResult;
import com.lichbalab.docs.api.model.DocumentQesSignatureValidationResult;
import com.lichbalab.docs.api.model.QesSignatureValidationResult;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.simplereport.jaxb.XmlDetails;
import eu.europa.esig.dss.simplereport.jaxb.XmlMessage;
import eu.europa.esig.dss.simplereport.jaxb.XmlSignatureLevel;
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
    public ValidationResponseMapper(@Qualifier("signatureIndicationMessageSource") MessageSource signatureIndicationMessageSource) {
        this.signatureIndicationMessageSource = signatureIndicationMessageSource;
    }


    public DocumentAdesSignatureValidationResult toAdesValidationResult(WSReportsDTO reportsDTO, Locale locale) {
        DocumentAdesSignatureValidationResult result = new DocumentAdesSignatureValidationResult();

        List<AdesSignatureValidationResult> signatures = new ArrayList<>();
        XmlDiagnosticData data = reportsDTO.getDiagnosticData();
        if (data != null && data.getSignatures() != null) {
            signatures.addAll(data.getSignatures().stream()
                    .map(ValidationResponseMapper::buildAdesValidationSignatureFromDigestSignature)
                    .toList());
        }

        XmlSimpleReport report = reportsDTO.getSimpleReport();
        List<XmlToken> tokens = report.getSignatureOrTimestampOrEvidenceRecord();

        for (XmlToken token : tokens) {
            if (!(token instanceof eu.europa.esig.dss.simplereport.jaxb.XmlSignature)) {
                continue;
            }

            for (AdesSignatureValidationResult signature : signatures) {
                if (signature.getSignatureId().equals(token.getId())) {
                    enrichAdesValidationResultWithReportSignature(signature, (eu.europa.esig.dss.simplereport.jaxb.XmlSignature) token, locale);
                }
            }
        }
        result.setSignatures(signatures);
        result.setDocumentName(report.getDocumentName());

        DocumentAdesSignatureValidationResult.ValidationSummary summary = new DocumentAdesSignatureValidationResult.ValidationSummary();
        summary.setValidSignatures(report.getValidSignaturesCount());
        summary.setTotalSignatures(report.getSignaturesCount());
        result.setSummary(summary);

        return result;
    }

    public DocumentQesSignatureValidationResult toQesValidationResult(WSReportsDTO reportsDTO, Locale locale) {
        DocumentQesSignatureValidationResult result = new DocumentQesSignatureValidationResult();

        List<QesSignatureValidationResult> signatures = new ArrayList<>();
        XmlDiagnosticData data = reportsDTO.getDiagnosticData();
        if (data != null && data.getSignatures() != null) {
            signatures.addAll(data.getSignatures().stream()
                    .map(ValidationResponseMapper::buildQesValidationSignatureFromDigestSignature)
                    .toList());
        }

        XmlSimpleReport report = reportsDTO.getSimpleReport();
        List<XmlToken> tokens = report.getSignatureOrTimestampOrEvidenceRecord();

        for (XmlToken token : tokens) {
            if (!(token instanceof eu.europa.esig.dss.simplereport.jaxb.XmlSignature)) {
                continue;
            }

            for (QesSignatureValidationResult signature : signatures) {
                if (signature.getSignatureId().equals(token.getId())) {
                    enrichQesValidationResultWithReportSignature(signature, (eu.europa.esig.dss.simplereport.jaxb.XmlSignature) token, locale);
                }
            }
        }
        result.setSignatures(signatures);
        result.setDocumentName(report.getDocumentName());

        DocumentAdesSignatureValidationResult.ValidationSummary summary = new DocumentAdesSignatureValidationResult.ValidationSummary();
        summary.setValidSignatures(report.getValidSignaturesCount());
        summary.setTotalSignatures(report.getSignaturesCount());
        result.setSummary(summary);

        return result;
    }

    private static AdesSignatureValidationResult buildAdesValidationSignatureFromDigestSignature(XmlSignature signature) {
        AdesSignatureValidationResult result = new AdesSignatureValidationResult();
        initValidationSignature(result, signature);
        return result;
    }

    private static QesSignatureValidationResult buildQesValidationSignatureFromDigestSignature(XmlSignature signature) {
        QesSignatureValidationResult result = new QesSignatureValidationResult();
        initValidationSignature(result, signature);
        return result;
    }

    private static void initValidationSignature(AdesSignatureValidationResult result, XmlSignature signature) {
        result.setSignatureId(signature.getId());

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
            AdesSignatureValidationResult.CertificateInfo certInfo = buildCertificateInfo(
                    signature.getSigningCertificate().getCertificate());
            result.setSigningCertificate(certInfo);
        }

        // Set certificate chain
        if (signature.getCertificateChain() != null) {
            result.setCertificateChain(signature.getCertificateChain().stream()
                    .map(cert -> buildCertificateInfo(cert.getCertificate()))
                    .toList());
        }

    }

    private static AdesSignatureValidationResult.CertificateInfo buildCertificateInfo(XmlCertificate certificate) {
        AdesSignatureValidationResult.CertificateInfo certInfo = new AdesSignatureValidationResult.CertificateInfo();

        certificate.getSubjectDistinguishedName().stream().filter(xdm -> xdm.getFormat().equals(RFC2253)).findAny()
                .ifPresent(xmlSubjectName -> certInfo.setSubject(xmlSubjectName.getValue()));

        certificate.getIssuerDistinguishedName().stream().filter(xdm -> xdm.getFormat().equals(RFC2253)).findAny()
                .ifPresent(xmlIssuerName -> certInfo.setIssuer(xmlIssuerName.getValue()));
        certInfo.setSerialNumber(String.valueOf(certificate.getSerialNumber()));

        if (certificate.getNotBefore() != null) {
            certInfo.setValidFrom(certificate.getNotBefore());
        }

        if (certificate.getNotAfter() != null) {
            certInfo.setValidTo(certificate.getNotAfter());
        }

        return certInfo;
    }


    private void enrichAdesValidationResultWithReportSignature(
            AdesSignatureValidationResult result,
            eu.europa.esig.dss.simplereport.jaxb.XmlSignature signature,
            Locale locale) {
        result.setIndication(signature.getIndication().name());
        if (signature.getSubIndication() != null) {
            result.setIndicationDetails(signatureIndicationMessageSource.getMessage(
                    signature.getSubIndication().name(),
                    new Object[]{}, "", locale
            ));
        }

        AdesSignatureValidationResult.ValidationDetails details = new AdesSignatureValidationResult.ValidationDetails();
        XmlDetails xmlDetails = signature.getAdESValidationDetails();
        if (xmlDetails != null) {
            details.setErrors(xmlDetails.getError().stream().map(XmlMessage::getValue).toList());
            details.setWarns(xmlDetails.getWarning().stream().map(XmlMessage::getValue).toList());
            details.setInfos(xmlDetails.getWarning().stream().map(XmlMessage::getValue).toList());
        }
        result.setSignatureDetails(details);
    }

    private void enrichQesValidationResultWithReportSignature(
            QesSignatureValidationResult result,
            eu.europa.esig.dss.simplereport.jaxb.XmlSignature signature,
            Locale locale) {

        enrichAdesValidationResultWithReportSignature(result, signature, locale);

        QesSignatureValidationResult.QualificationInfo qualification = new QesSignatureValidationResult.QualificationInfo();
        XmlSignatureLevel xmlSignatureLevel = signature.getSignatureLevel();
        if (xmlSignatureLevel != null) {
            qualification.setLevel(xmlSignatureLevel.getValue().getReadable());
            qualification.setDescription(xmlSignatureLevel.getValue().getLabel());
        }
        AdesSignatureValidationResult.ValidationDetails validationDetails = new AdesSignatureValidationResult.ValidationDetails();
        XmlDetails qsDetails = signature.getQualificationDetails();
        if (qsDetails != null) {
            validationDetails.setErrors(qsDetails.getError().stream().map(XmlMessage::getValue).toList());
            validationDetails.setWarns(qsDetails.getWarning().stream().map(XmlMessage::getValue).toList());
            validationDetails.setInfos(qsDetails.getWarning().stream().map(XmlMessage::getValue).toList());
        }
        qualification.setDetails(validationDetails);
        result.setQualification(qualification);
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
