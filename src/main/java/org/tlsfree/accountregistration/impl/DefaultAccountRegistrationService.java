package org.tlsfree.accountregistration.impl;

import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.util.Arrays;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tlsfree.account.Account;
import org.tlsfree.account.AccountService;
import org.tlsfree.accountregistration.AccountRegistrationService;
import org.tlsfree.blob.BlobStore;
import org.tlsfree.blob.BlobStoreService;
import org.tlsfree.crypto.PrivateKeyEncryptionUtils;
import org.tlsfree.crypto.RSAKeyUtils;
import org.tlsfree.registration.Registration;
import org.tlsfree.registration.RegistrationService;

@Service
public class DefaultAccountRegistrationService implements AccountRegistrationService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	private AccountService accountService;
	@Inject
	private BlobStoreService blobStoreService;
	@Inject
	private RegistrationService registrationService;
	@Inject
	private KeyPair serverKeyPair;

	@Value("${acme.url}")
	private String acmeUrl;

	@Override
	public Registration createRegistration(Long accountId) throws AcmeException, CMSException, IOException {
		Account account = accountService.getAccountById(accountId);

		boolean newAccount = account.getPrivateKey() == null;
		Session session = getAcmeSessionByAccountId(accountId);

		org.shredzone.acme4j.Registration reg;
		Registration registration;
		if (newAccount) {
			logger.debug("Registering new let's encrypt account {}", account.getId());
			account = generateAccountKeyPair(accountId);

			reg = registerAccount(account, session);
			registration = saveRegistration(reg, session, account);
		} else {
			Long pubKeyId = account.getPublicKey().getId();
			registration = registrationService.getRegistrationByAccountIdAndPublicKeyId(accountId, pubKeyId);

			if (registration != null) {
				logger.debug("Account already exists, binding existing one");
				reg = org.shredzone.acme4j.Registration.bind(session, URI.create(registration.getRegistrationUri()));
				registration.setAcmeRegistration(reg);
			} else {
				reg = registerAccount(account, session);
				registration = saveRegistration(reg, session, account);
			}
		}

		return registration;
	}

	@Override
	public Registration getRegistrationById(Long registrationId) throws CMSException, IOException {
		Registration registration = registrationService.getRegistrationById(registrationId);
		Session session = getAcmeSessionByAccountId(registration.getAccount().getId());

		org.shredzone.acme4j.Registration reg =
				org.shredzone.acme4j.Registration.bind(session, URI.create(registration.getRegistrationUri()));
		registration.setAcmeSession(session).setAcmeRegistration(reg);

		return registration;
	}

	private Session getAcmeSessionByAccountId(Long accountId) throws CMSException, IOException {
		KeyPair keyPair = getOrGenerateAccountKeyPair(accountId);
		return new Session(acmeUrl, keyPair);
	}

	private KeyPair getOrGenerateAccountKeyPair(Long accountId) throws CMSException, IOException {
		Account account = accountService.getAccountById(accountId);
		if (account.getPrivateKey() == null) {
			logger.debug("Account ({}) doesn't have key pair, generating key pair", accountId);
			account = generateAccountKeyPair(accountId);
		}
		byte[] encryptedPrivKey = account.getPrivateKey().getDataBlob();
		KeyPair keyPair = PrivateKeyEncryptionUtils.decrypt(serverKeyPair.getPrivate(), encryptedPrivKey);
		if (!Arrays.equals(account.getPublicKey().getDataBlob(), keyPair.getPublic().getEncoded())) {
			logger.warn("Decrypted public key doesn't match with stored one in database");
		}
		return keyPair;
	}

	private Account generateAccountKeyPair(Long accountId) throws CMSException, IOException {
		Account account = accountService.getAccountById(accountId);
		logger.debug("Generating key pair for account {}", accountId);
		KeyPair keyPair = RSAKeyUtils.generateKeyPair();
		// set account public key
		BlobStore pubKeyBlob = blobStoreService.saveBlob(new BlobStore().setDataBlob(keyPair.getPublic().getEncoded()));
		account.setPublicKey(pubKeyBlob);
		// encrypt private key with application key
		byte[] encryptedPrivKey = PrivateKeyEncryptionUtils.encrypt(serverKeyPair.getPublic(), keyPair.getPrivate());
		BlobStore privKeyBlob = blobStoreService.saveBlob(new BlobStore().setDataBlob(encryptedPrivKey));
		account.setPrivateKey(privKeyBlob);
		logger.debug("Storing encrypted private key and public key to database");
		return account;
	}

	private org.shredzone.acme4j.Registration registerAccount(Account account, Session session) throws AcmeException {
		return new RegistrationBuilder()
				.addContact(StringUtils.join("mailto:", account.getEmail()))
				.create(session);
	}

	private Registration saveRegistration(org.shredzone.acme4j.Registration reg, Session session, Account account) {
		Registration registration = new Registration()
				.setAccount(account)
				.setPublicKey(account.getPublicKey())
				.setAcmeRegistration(reg)
				.setAcmeSession(session);

		return registrationService.saveRegistration(registration);
	}
}
