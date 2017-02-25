package org.tlsfree.registration.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.tlsfree.registration.Registration;
import org.tlsfree.registration.RegistrationRepository;
import org.tlsfree.registration.RegistrationService;

@Service
public class DefaultRegistrationService implements RegistrationService {
	@Inject
	private RegistrationRepository registrationRepository;

	@Override
	public Registration getRegistrationById(Long registrationId) {
		return registrationRepository.findOne(registrationId);
	}

	@Override
	public Registration getRegistrationByAccountIdAndPublicKeyId(Long accountId, Long pubKeyId) {
		return registrationRepository.findByAccountIdAndPublicKeyId(accountId, pubKeyId);
	}

	@Override
	public Registration saveRegistration(Registration registration) {
		return registrationRepository.save(registration);
	}
}
