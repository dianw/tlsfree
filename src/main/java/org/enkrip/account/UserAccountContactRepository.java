package org.enkrip.account;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountContactRepository extends AuditingEntityRepository<UserAccountContactEntity> {
	void deleteByUserAccountId(String accountId);

	Page<UserAccountContactEntity> findByUserAccountId(String accountId, Pageable pageable);
}
