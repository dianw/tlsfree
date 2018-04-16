package org.enkrip.account;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;

import javax.crypto.SecretKey;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.user.UserEntity;
import org.enkrip.core.enc.KeyUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.shredzone.acme4j.Login;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserAccountService {
	default UserAccountEntity createUserAccount(KeyPair masterKey, UserAccountEntity userAccount, KeyPair keyPair) {
		SecretKey secretKey = KeyUtils.generateAESSecretKey(256);
		return userAccount.setAccountPublicKey(keyPair.getPublic().getEncoded())
				.setAccountKey(KeyUtils.wrapPrivateKey(keyPair.getPrivate(), secretKey))
				.setAccountKeySecret(KeyUtils.wrapSecretKey(secretKey, masterKey.getPublic()));
	}

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	void deactivateAccount(@NotNull String userId, @NotNull String accountId) throws AcmeException;

	@PreAuthorize("isAuthenticated()")
	Page<UserAccountContactEntity> findAccountContactsByAccount(String accountId, Pageable pageable);

	default Login findLoginByUserAccount(KeyPair masterKey, Session acmeSession, UserAccountEntity userAccount) {
		PrivateKey masterDecryptionKey = masterKey.getPrivate();
		SecretKey secretKey = KeyUtils.unwrapSecretKey(userAccount.getAccountKeySecret(), "AES", masterDecryptionKey);
		PrivateKey accountKey = KeyUtils.unwrapPrivateKeyKey(userAccount.getAccountKey(), "RSA", secretKey);
		KeyPair accountKeyPair = KeyUtils.generateKeyPairFromPrivateKey(accountKey);
		try {
			return acmeSession.login(new URL(userAccount.getAccountLocation()), accountKeyPair);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	Login findLoginByUserAccount(String userId, String accountId) throws AcmeException;

	@PreAuthorize("isAuthenticated()")
	@PostAuthorize("#returnObject.user.id == principal.id")
	UserAccountEntity findUserAccountById(@NotNull String accountId) throws AcmeException;

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	Page<UserAccountEntity> findUserAccountsByUser(@NotNull String userId, Pageable pageable);

	@PreAuthorize("isAuthenticated() and #user.Id == principal.id")
	UserAccountEntity registerNewUserAccount(@NotNull UserEntity user,
			@NotEmpty List<UserAccountContactEntity> contacts) throws AcmeException;

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	UserAccountEntity updateUserAccountContacts(@NotNull String userId, @NotNull String accountId,
			@NotEmpty List<UserAccountContactEntity> contacts) throws AcmeException;

	@PreAuthorize("isAuthenticated() and #userId == principal.id")
	UserAccountEntity updateUserAccountKeyPair(@NotNull String userId, @NotNull String accountId) throws AcmeException;
}
