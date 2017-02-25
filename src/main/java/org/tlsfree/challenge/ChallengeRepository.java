package org.tlsfree.challenge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.tlsfree.core.persistence.AuditingEntityRepository;

@Repository
public interface ChallengeRepository extends AuditingEntityRepository<Challenge> {
	Challenge findByKey(String key);

	Page<Challenge> findByAuthorizationId(Long id, Pageable pageable);
}
