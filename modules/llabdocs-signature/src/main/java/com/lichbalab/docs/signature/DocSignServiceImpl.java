package com.lichbalab.docs.signature;

import java.io.IOException;
import java.io.InputStream;

import com.lichbalab.certificate.Certificate;
import com.lichbalab.cmc.mapper.CertificateDtoMapper;
import com.lichbalab.cmc.service.CertificateService;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocSignServiceImpl implements DocSignService {

    private final CertificateService certificateService;


    @Autowired
    public DocSignServiceImpl (CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public DSSDocument signPdf(InputStream doc, String certAlias) throws IOException {

        Certificate certificate = CertificateDtoMapper.dtoToCertificate(certificateService.getCertByAlias(certAlias));
        try (SignatureTokenConnection signingToken = new SignatureTokenLLab(certificate)) {
            DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);

            // tag::demo[]
            // import eu.europa.esig.dss.pades.PAdESSignatureParameters;
            // import eu.europa.esig.dss.enumerations.SignatureLevel;
            // import eu.europa.esig.dss.enumerations.DigestAlgorithm;
            // import eu.europa.esig.dss.validation.CommonCertificateVerifier;
            // import eu.europa.esig.dss.pades.signature.PAdESService;
            // import eu.europa.esig.dss.model.ToBeSigned;
            // import eu.europa.esig.dss.model.SignatureValue;
            // import eu.europa.esig.dss.model.DSSDocument;

            // Preparing parameters for the PAdES signature
            PAdESSignatureParameters parameters = new PAdESSignatureParameters();
            // We choose the level of the signature (-B, -T, -LT, -LTA).
            parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
            // We set the digest algorithm to use with the signature algorithm. You must use the
            // same parameter when you invoke the method sign on the token. The default value is
            // SHA256
            parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);

            // We set the signing certificate
            parameters.setSigningCertificate(privateKey.getCertificate());
            //parameters.setSigningCertificate(SignUtil.getDssCertificateToken(certificate));

            // We set the certificate chain
            parameters.setCertificateChain(SignUtil.getDssCertificateChain(certificate));

            // Create common certificate verifier
            CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
            // Create PAdESService for signature
            PAdESService service = new PAdESService(commonCertificateVerifier);

            // Get the SignedInfo segment that need to be signed.
            InMemoryDocument toSignDocument = new InMemoryDocument(doc, null, MimeTypeEnum.PDF);
            ToBeSigned       dataToSign     = service.getDataToSign(toSignDocument, parameters);

            // This function obtains the signature value for signed information using the
            // private key and specified algorithm
            DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
            SignatureValue  signatureValue  = signingToken.sign(dataToSign, digestAlgorithm, privateKey);

            // Optionally or for debug purpose :
            // Validate the signature value against the original dataToSign
            //assertTrue(service.isValidSignatureValue(dataToSign, signatureValue, privateKey.getCertificate()));

            // We invoke the padesService to sign the document with the signature value obtained in
            // the previous step.
            return service.signDocument(toSignDocument, parameters, signatureValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}