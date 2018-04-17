package org.enkrip.account.impl;

import java.security.KeyPair;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.codenergic.theskeleton.user.UserEntity;
import org.enkrip.account.UserAccountContactEntity;
import org.enkrip.account.UserAccountContactRepository;
import org.enkrip.account.UserAccountEntity;
import org.enkrip.account.UserAccountRepository;
import org.enkrip.account.UserAccountService;
import org.enkrip.core.enc.KeyUtils;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Login;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserAccountServiceImpl implements UserAccountService {
	private final KeyPair masterKey;
	private final Session acmeSession;
	private final UserAccountRepository userAccountRepository;
	private final UserAccountContactRepository userAccountContactRepository;

	public UserAccountServiceImpl(KeyPair masterKey, Session acmeSession, UserAccountRepository userAccountRepository,
								  UserAccountContactRepository userAccountContactRepository) {
		this.masterKey = masterKey;
		this.acmeSession = acmeSession;
		this.userAccountRepository = userAccountRepository;
		this.userAccountContactRepository = userAccountContactRepository;
	}

	@Override
	@Transactional
	public void deactivateAccount(String userId, String accountId) throws AcmeException {
		UserAccountEntity userAccount = findUserAccountById(userId, accountId);
		userAccount.getAccount().deactivate();
		userAccountContactRepository.deleteByUserAccountId(accountId);
		userAccountRepository.delete(accountId);
	}

	@Override
	public Page<UserAccountContactEntity> findAccountContactsByAccount(String accountId, Pageable pageable) {
		return userAccountContactRepository.findByUserAccountId(accountId, pageable);
	}

	@Override
	public Login findLoginByUserAccount(String userId, String accountId) throws AcmeException {
		UserAccountEntity userAccount = findUserAccountById(userId, accountId);
		return findLoginByUserAccount(masterKey, acmeSession, userAccount);
	}

	@Override
	public UserAccountEntity findUserAccountById(String accountId) throws AcmeException {
		UserAccountEntity userAccount = userAccountRepository.findOne(accountId);
		Validate.notNull(userAccount);
		return userAccount.setAccount(findLoginByUserAccount(masterKey, acmeSession, userAccount).getAccount());
	}

	private UserAccountEntity findUserAccountById(String userId, String accountId) throws AcmeException {
		UserAccountEntity userAccount = findUserAccountById(accountId);
		Validate.notNull(userAccount);
		Validate.isTrue(userId.equals(userAccount.getUser().getId()));
		return userAccount;
	}

	@Override
	public Page<UserAccountEntity> findUserAccountsByUser(String userId, Pageable pageable) {
		return userAccountRepository.findByUserId(userId, pageable);
	}

	@Override
	@Transactional
	public UserAccountEntity registerNewUserAccount(UserEntity user, List<UserAccountContactEntity> contacts) throws AcmeException {
		KeyPair keyPair = KeyUtils.generateRSAKeyPair(4096);
		UserAccountEntity userAccount = createUserAccount(masterKey, new UserAccountEntity(), keyPair)
			.setUser(user);
		AccountBuilder acmeAccountBuilder = new AccountBuilder()
			.useKeyPair(keyPair)
			.agreeToTermsOfService();
		for (UserAccountContactEntity contact : contacts) {
			contact.setUserAccount(userAccount);
			acmeAccountBuilder.addContact(StringUtils.join("mailto:", contact.getEmail()));
		}
		Account acmeAccount = acmeAccountBuilder.create(acmeSession);
		userAccount.setAccount(acmeAccount)
			.setAccountLocation(acmeAccount.getLocation().toExternalForm());
		userAccountRepository.save(userAccount);
		userAccountContactRepository.save(contacts);
		return userAccount;
	}

	@Override
	@Transactional
	public UserAccountEntity updateUserAccountContacts(String userId, String accountId, List<UserAccountContactEntity> contacts) throws AcmeException {
		UserAccountEntity userAccount = findUserAccountById(userId, accountId);
		userAccountContactRepository.deleteByUserAccountId(accountId);
		Account.EditableAccount editableAcmeAccount = userAccount.getAccount().modify();
		for (UserAccountContactEntity contact : contacts) {
			contact.setUserAccount(userAccount);
			editableAcmeAccount.addContact(StringUtils.join("mailto:", contact.getEmail()));
		}
		editableAcmeAccount.commit();
		userAccountContactRepository.save(contacts);
		return userAccount;
	}

	@Override
	@Transactional
	public UserAccountEntity updateUserAccountKeyPair(String userId, String accountId) throws AcmeException {
		UserAccountEntity userAccount = findUserAccountById(userId, accountId);
		KeyPair keyPair = KeyUtils.generateRSAKeyPair(4096);
		userAccount.getAccount().changeKey(keyPair);
		createUserAccount(masterKey, userAccount, keyPair);
		return userAccount;
	}
}
