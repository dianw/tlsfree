package org.tlsfree.authorization.impl;

import java.net.URI;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.tlsfree.authorization.Authorization;
import org.tlsfree.authorization.AuthorizationRepository;
import org.tlsfree.authorization.AuthorizationService;
import org.tlsfree.challenge.Challenge;
import org.tlsfree.challenge.ChallengeRepository;
import org.tlsfree.core.security.SecurityUtils;
import org.tlsfree.registration.Registration;

@Service
public class DefaultAuthorizationService implements AuthorizationService {
	@Inject
	private AuthorizationRepository authorizationRepository;
	@Inject
	private ChallengeRepository challengeRepository;

	@Override
	public Authorization authorizeDomain(Authorization authorization, Registration registration) throws AcmeException {
		org.shredzone.acme4j.Registration reg = registration.getAcmeRegistration();

		org.shredzone.acme4j.Authorization auth = reg.authorizeDomain(authorization.getDomain());
		authorization.setAcmeAuthorization(auth).setRegistration(registration);

		return authorizationRepository.save(authorization);
	}

	@Override
	public Authorization findAuthorizationById(Long authorizationId) {
		Authorization authorization = authorizationRepository.findOne(authorizationId);
		org.shredzone.acme4j.Authorization auth = org.shredzone.acme4j.Authorization
				.bind(SecurityUtils.getCurrentAcmeSession(), URI.create(authorization.getAuthorizationUri()));
		authorization.setAcmeAuthorization(auth);
		return authorization;
	}

	@Override
	public Page<Challenge> findChallengesByAuthorizationId(Long authorizationId) {
		Authorization authorization = findAuthorizationById(authorizationId);
		Page<Challenge> challenges = challengeRepository.findByAuthorizationId(authorizationId, null);
		if (challenges.getTotalElements() > 0) {
			return challenges;
		}
		org.shredzone.acme4j.Authorization auth = authorization.getAcmeAuthorization();
		return new PageImpl<>(auth.getChallenges()
				.stream()
				.filter(ch -> (ch instanceof Http01Challenge) || (ch instanceof Dns01Challenge))
				.map(ch -> challengeRepository.save(new Challenge().setAcmeChallenge(ch).setAuthorization(authorization)))
				.collect(Collectors.toList()));
	}

	@Override
	public Session findSessionByAuthorizationId(Long authorizationId) {
		Authorization authorization = findAuthorizationById(authorizationId);
		Registration registration = authorization.getRegistration();
		return registration.getAcmeSession();
	}

	@Override
	public Challenge triggerChallege(String challengeKey) throws AcmeException, InterruptedException {
		Challenge challenge = challengeRepository.findByKey(challengeKey);
		if (challenge == null) return null;
		if (!challenge.getAcmeStatus().equals(Status.PENDING)) return challenge;
		org.shredzone.acme4j.challenge.Challenge ch = org.shredzone.acme4j.challenge.Challenge
				.bind(SecurityUtils.getCurrentAcmeSession(), URI.create(challenge.getChallengeUri()));
		if (!ch.getStatus().equals(Status.PENDING)) {
			challenge.setAcmeChallenge(ch);
			return challenge;
		}
		ch.trigger();
		for (int i = 0; i < 3 && !ch.getStatus().equals(Status.VALID); i++) {
			Thread.sleep(2000);
			ch.update();
		}
		ch.update();
		challenge.setAcmeChallenge(ch);
		return challenge;
	}
}
