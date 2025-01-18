import com.lichbalab.certificate.Certificate;
import com.lichbalab.certificate.CertificateUtils;
import com.lichbalab.docs.signature.DocSignService;
import com.lichbalab.docs.signature.DocSignServiceImpl;
import com.lichbalab.docs.signature.SignatureValidationServiceLLab;
import eu.europa.esig.dss.model.DSSDocument;
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

        WSReportsDTO report;
        try (InputStream is = signedDoc.openStream()) {
            report = signatureValidationServiceLLab.validateQesSignature(is.readAllBytes(), "test_doc_for_sign.pdf");
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

        String report;
        try (InputStream is = signedDoc.openStream()) {
            report = signatureValidationServiceLLab.validateSignatureSimpleHtmlReport(is.readAllBytes(), "test_doc_for_sign.pdf");
        }

        Assertions.assertNotNull(report, "Failed to verify signature.");
    }
}