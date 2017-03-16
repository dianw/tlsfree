package org.tlsfree.core.security;

import org.shredzone.acme4j.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.tlsfree.account.Account;
import org.tlsfree.registration.Registration;

public final class SecurityUtils {
	private SecurityUtils() {
	}

	public static Account getCurrentAccount() {
		AccountUserDetails accountUserDetails = getCurrentAccountUserDetails();
		if (accountUserDetails == null)
			return null;

		return accountUserDetails.getAccount();
	}

	public static Registration getCurrentRegistration() {
		AccountUserDetails accountUserDetails = getCurrentAccountUserDetails();
		if (accountUserDetails == null)
			return null;

		return accountUserDetails.getRegistration();
	}

	public static Session getCurrentAcmeSession() {
		AccountUserDetails accountUserDetails = getCurrentAccountUserDetails();
		if (accountUserDetails == null)
			return null;

		return accountUserDetails.getAcmeSession();
	}

	private static AccountUserDetails getCurrentAccountUserDetails() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		if (authentication == null)
			return null;

		if (authentication.getPrincipal() instanceof AccountUserDetails) {
			return (AccountUserDetails) authentication.getPrincipal();
		}

		return null;
	}
}
