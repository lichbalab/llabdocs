import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.docs.signature.DocSignService;
import com.lichbalab.docs.signature.DocSignServiceImpl;
import com.lichbalab.docs.signature.SignatureValidationServiceLLab;
import com.lichbalab.docs.signature.SignatureValidationServiceLLabImpl;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.ws.validation.common.RemoteDocumentValidationService;
import eu.europa.esig.dss.ws.validation.dto.WSReportsDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

@SpringBootTest(classes = TestApplication.class)
public class SignatureValidationServiceLLabTest {

    @Autowired
    public SignatureValidationServiceLLab signatureValidationServiceLLab;

    @Test
    void verifySignature() throws IOException {
        File signCertFile = new File("src/test/resources/certs/llabdocs.com.pem");
        Certificate signCert = CertificateUtils.buildFromPEM(new FileReader(signCertFile));
        FileInputStream doc = new FileInputStream("src/test/resources/docs/test_doc_for_sign.pdf");
        CmcClientTest certService = new CmcClientTest(signCert);
        DocSignService signService = new DocSignServiceImpl(certService);
        DSSDocument signedDoc = signService.signPdf(doc, "alias");
/*
        RemoteDocumentValidationService rds = new RemoteDocumentValidationService();
        rds.setVerifier(new CommonCertificateVerifier());

        SignatureValidationServiceLLab signatureValidationServiceLLab = new SignatureValidationServiceLLabImpl(rds);
*/
        WSReportsDTO report;
        try (InputStream is = signedDoc.openStream()) {
            report = signatureValidationServiceLLab.validateSignature(is.readAllBytes());
        }

        Assertions.assertNotNull(report, "Failed to verify signature.");
    }

    @Test
    void verifySignatureReportHtml() throws IOException {
        File signCertFile = new File("src/test/resources/certs/llabdocs.com.pem");
        Certificate signCert = CertificateUtils.buildFromPEM(new FileReader(signCertFile));
        FileInputStream doc = new FileInputStream("src/test/resources/docs/test_doc_for_sign.pdf");
        CmcClientTest certService = new CmcClientTest(signCert);
        DocSignService signService = new DocSignServiceImpl(certService);
        DSSDocument signedDoc = signService.signPdf(doc, "alias");

/*
        RemoteDocumentValidationService rds = new RemoteDocumentValidationService();
        rds.setVerifier(new CommonCertificateVerifier());
        SignatureValidationServiceLLab signatureValidationServiceLLab = new SignatureValidationServiceLLabImpl(rds);
*/
        String report;
        try (InputStream is = signedDoc.openStream()) {
            report = signatureValidationServiceLLab.validateSignatureSimpleHtmlReport(is.readAllBytes());
        }

        Assertions.assertNotNull(report, "Failed to verify signature.");
    }
}