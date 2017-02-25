package org.tlsfree.core.security;

import java.io.IOException;

import org.bouncycastle.cms.CMSException;
import org.shredzone.acme4j.exception.AcmeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.tlsfree.account.Account;
import org.tlsfree.accountregistration.AccountRegistrationService;
import org.tlsfree.registration.Registration;

public class AuthenticationSuccessListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private AccountRegistrationService registrationService;

	public AuthenticationSuccessListener(AccountRegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
		Authentication authentication = event.getAuthentication();
		if (!(authentication.getPrincipal() instanceof AccountUserDetails)) return;

		AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
		Account account = userDetails.getAccount();

		try {
			logger.debug("Registering ACME account for account: {}", account.getId());
			Registration registration = registrationService.createRegistration(account.getId());
			userDetails.setRegistration(registration).setAcmeSession(registration.getAcmeSession());
		} catch (CMSException | IOException | AcmeException e) {
			logger.error("Failed registering acme account for account: {}", account.getId(), e);
		}
	}
}
