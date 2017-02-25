package org.tlsfree.account;

@SuppressWarnings("serial")
public class AccountNotFoundException extends RuntimeException {
	public AccountNotFoundException() {
		super();
	}

	public AccountNotFoundException(String message) {
		super(message);
	}
}