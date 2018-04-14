package org.enkrip.account;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends AuditingEntityRepository<UserAccountEntity> {
	Page<UserAccountEntity> findByUserId(String userId, Pageable pageable);
}
