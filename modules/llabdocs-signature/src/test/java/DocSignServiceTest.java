import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.docs.signature.DocSignService;
import com.lichbalab.docs.signature.DocSignServiceImpl;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.pdfa.PDFAValidationResult;
import eu.europa.esig.dss.pdfa.validation.PDFADocumentValidator;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocSignServiceTest {

    @Test
    void signPdfTest() throws IOException {
        File signCertFile = new File("src/test/resources/certs/test.pem");
        Certificate signCert = CertificateUtils.buildFromPEM(new FileReader(signCertFile));
        FileInputStream doc = new FileInputStream("src/test/resources/docs/test_doc_for_sign.pdf");
        CmcClientTest cmcClientTest = new CmcClientTest(signCert);
        DocSignService signService = new DocSignServiceImpl(cmcClientTest);
        DSSDocument signedDoc = signService.signPdf(doc, "alias");

        Assertions.assertNotNull(signedDoc, "Failed to sign pdf doc.");

        PDFADocumentValidator documentValidator = new PDFADocumentValidator(signedDoc);

        // Extract PDF/A validation result
        // This report contains only validation of a document against PDF/A specification
        // and no signature validation process result
        PDFAValidationResult pdfaValidationResult = documentValidator.getPdfValidationResult();

        // This variable contains the name of the identified PDF/A profile (or closest if validation failed)
        String profileId = pdfaValidationResult.getProfileId();

        // Checks whether the PDF document is compliant to the identified PDF profile
        boolean compliant = pdfaValidationResult.isCompliant();

        // Returns the error messages occurred during the PDF/A verification
        Collection<String> errorMessages = pdfaValidationResult.getErrorMessages();

        // It is also possible to perform the signature validation process and extract the PDF/A validation result from DiagnosticData

        // Configure PDF/A document validator and perform validation of the document
        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        documentValidator.setCertificateVerifier(commonCertificateVerifier);
        Reports reports = documentValidator.validateDocument();

        // Extract the interested information from DiagnosticData
        DiagnosticData diagnosticData = reports.getDiagnosticData();
        profileId = diagnosticData.getPDFAProfileId();
        compliant = diagnosticData.isPDFACompliant();
        errorMessages = diagnosticData.getPDFAValidationErrors();
        // end::pdfa[]

        assertNotNull(profileId);
        assertFalse(compliant);
        assertTrue(Utils.isCollectionNotEmpty(errorMessages));
    }
}