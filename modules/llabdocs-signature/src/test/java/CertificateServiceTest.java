import com.lichbalab.certificate.dto.CertificateDto;
import com.lichbalab.cmc.mapper.CertificateMapper;
import com.lichbalab.cmc.repository.CertificateRepository;
import com.lichbalab.cmc.service.CertificateService;

public class CertificateServiceTest extends CertificateService {

    private CertificateDto certificate;

    public CertificateServiceTest(CertificateDto certificate) {
        super(null, null);
        this.certificate = certificate;
    }

    public CertificateServiceTest(CertificateRepository certificateRepository, CertificateMapper mapper) {
        super(certificateRepository, mapper);
    }

    @Override
    public CertificateDto getCertByAlias(String alias) {
        return certificate;
    }
}
