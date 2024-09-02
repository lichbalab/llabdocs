import com.lichbalab.certificate.Certificate;
import com.lichbalab.cmc.sdk.client.CmcClient;

import java.util.List;

public class CmcClientTest implements CmcClient {

    private Certificate certificate;

    public CmcClientTest(Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public Certificate getCertificateByAlias(String alias) {
        return certificate;
    }

    @Override
    public List<Certificate> getCertificates() {
        return List.of();
    }

    @Override
    public Certificate addCertificate(Certificate certificate) {
        return null;
    }

    @Override
    public void deleteCertificate(String alias) {

    }
}
