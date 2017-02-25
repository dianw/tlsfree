package org.tlsfree.authorization;

import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.tlsfree.challenge.Challenge;
import org.tlsfree.core.persistence.ReadOnlyTransactional;
import org.tlsfree.registration.Registration;

@Transactional
public interface AuthorizationService {
	Authorization authorizeDomain(Authorization authorization, Registration registration) throws AcmeException;

	@ReadOnlyTransactional
	Authorization findAuthorizationById(Long authorizationId);

	Page<Challenge> findChallengesByAuthorizationId(Long authorizationId);

	@ReadOnlyTransactional
	Session findSessionByAuthorizationId(Long authorizationId);

	@ReadOnlyTransactional
	Challenge triggerChallege(String challengeKey) throws AcmeException, InterruptedException;
}
