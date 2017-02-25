package org.tlsfree.core.security;

import java.util.Collection;

import org.shredzone.acme4j.Session;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;
import org.tlsfree.account.Account;
import org.tlsfree.registration.Registration;

@SuppressWarnings("serial")
public class AccountUserDetails extends User {
	private final Account account;
	private Registration registration;
	private Session session;

	public AccountUserDetails(Account account, Collection<GrantedAuthority> authorities) {
		super(account.getUsername(),
				account.getPassword(),
				account.isEnabled(),
				account.isAccountNonExpired(),
				account.isCredentialsNonExpired(),
				account.isAccountNonLocked(),
				authorities);
		Assert.notNull(account);
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}

	public Session getAcmeSession() {
		return session;
	}

	public Registration getRegistration() {
		return registration;
	}

	protected AccountUserDetails setAcmeSession(Session session) {
		this.session = session;
		return this;
	}

	protected AccountUserDetails setRegistration(Registration registration) {
		this.registration = registration;
		return this;
	}
}
