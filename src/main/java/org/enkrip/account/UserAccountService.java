package org.enkrip.account;

import javax.crypto.SecretKey;
import javax.validation.constraints.NotNull;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;

import org.codenergic.theskeleton.user.UserEntity;
import org.enkrip.core.enc.KeyUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserAccountService {
	default Account createAcmeAccount(KeyPair masterKey, Session acmeSession, UserAccountEntity userAccount) throws AcmeException {
		PrivateKey masterDecryptionKey = masterKey.getPrivate();
		SecretKey secretKey = KeyUtils.unwrapSecretKey(userAccount.getAccountKeySecret(), "AES", masterDecryptionKey);
		PrivateKey accountKey = KeyUtils.unwrapPrivateKeyKey(userAccount.getAccountKey(), "RSA", secretKey);
		KeyPair accountKeyPair = KeyUtils.generateKeyPairFromPrivateKey(accountKey);
		return new AccountBuilder()
			.onlyExisting()
			.useKeyPair(accountKeyPair)
			.create(acmeSession);
	}

	default UserAccountEntity createUserAccount(KeyPair masterKey, UserAccountEntity userAccount, KeyPair keyPair) {
		SecretKey secretKey = KeyUtils.generateAESSecretKey(256);
		return userAccount
			.setAccountPublicKey(keyPair.getPublic().getEncoded())
			.setAccountKey(KeyUtils.wrapPrivateKey(keyPair.getPrivate(), secretKey))
			.setAccountKeySecret(KeyUtils.wrapSecretKey(secretKey, masterKey.getPublic()));
	}

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	void deactivateAccount(@NotNull String userId, @NotNull String accountId) throws AcmeException;

	@PreAuthorize("isAuthenticated()")
	Page<UserAccountContactEntity> findAccountContactsByAccount(String accountId, Pageable pageable);

	@PreAuthorize("isAuthenticated()")
	@PostAuthorize("#returnObject.user.id == principal.id")
	UserAccountEntity findUserAccountById(@NotNull String accountId) throws AcmeException;

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	Page<UserAccountEntity> findUserAccountsByUser(@NotNull String userId, Pageable pageable);

	@PreAuthorize("isAuthenticated() and #user.Id == principal.id")
	UserAccountEntity registerNewUserAccount(@NotNull UserEntity user, @NotEmpty List<UserAccountContactEntity> contacts) throws AcmeException;

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	UserAccountEntity updateUserAccountContacts(@NotNull String userId, @NotNull String accountId, @NotEmpty List<UserAccountContactEntity> contacts) throws AcmeException;

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	UserAccountEntity updateUserAccountKeyPair(@NotNull String userId, @NotNull String accountId) throws AcmeException;
}
