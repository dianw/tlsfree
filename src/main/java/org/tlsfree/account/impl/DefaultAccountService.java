package org.tlsfree.account.impl;

import javax.inject.Inject;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tlsfree.account.Account;
import org.tlsfree.account.AccountNotFoundException;
import org.tlsfree.account.AccountRepository;
import org.tlsfree.account.AccountService;

@Service
public class DefaultAccountService implements AccountService {
	@Inject
	private AccountRepository accountRepository;
	@Inject
	private PasswordEncoder passwordEncoder;

	@Override
	public Account getAccountById(Long id) {
		Account account = accountRepository.findOne(id);
		if (account == null) throw new AccountNotFoundException();
		return account;
	}

	@Override
	public boolean isAccountPasswordMatch(String rawPassword, Account account) {
		return passwordEncoder.matches(rawPassword, account.getPassword());
	}

	@Override
	public Account saveAccount(Account account) {
		account.setId(0L);
		account.setPassword(passwordEncoder.encode(account.getPassword()));

		return accountRepository.save(account);
	}

	@Override
	public Account updateAccountPassword(Long accountId, String newPassword) {
		Account account = accountRepository.findOne(accountId);
		return account.setPassword(passwordEncoder.encode(newPassword));
	}
}
