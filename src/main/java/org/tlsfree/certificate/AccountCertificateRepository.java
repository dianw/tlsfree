package org.tlsfree.certificate;

import org.springframework.stereotype.Repository;
import org.tlsfree.core.persistence.AuditingEntityRepository;

@Repository
public interface AccountCertificateRepository extends AuditingEntityRepository<AccountCertificate> {

}
