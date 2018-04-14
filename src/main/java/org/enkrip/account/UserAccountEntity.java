package org.enkrip.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.user.UserEntity;
import org.shredzone.acme4j.Account;

@Entity
@Table(name = "enkrip_user_account")
public class UserAccountEntity extends AbstractAuditingEntity {
	@Transient
	private Account account;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	@Lob
	@NotNull
	@Column(name = "account_location", nullable = false)
	private String accountLocation;
	@Lob
	@NotNull
	@Column(name = "account_key", nullable = false)
	private byte[] accountKey;
	@Lob
	@NotNull
	@Column(name = "account_public_key", nullable = false)
	private byte[] accountPublicKey;
	@Lob
	@NotNull
	@Column(name = "account_key_secret", nullable = false)
	private byte[] accountKeySecret;

	public Account getAccount() {
		return account;
	}

	public UserAccountEntity setAccount(Account account) {
		this.account = account;
		return this;
	}

	public byte[] getAccountKey() {
		return accountKey;
	}

	public UserAccountEntity setAccountKey(byte[] accountKey) {
		this.accountKey = accountKey;
		return this;
	}

	public byte[] getAccountKeySecret() {
		return accountKeySecret;
	}

	public UserAccountEntity setAccountKeySecret(byte[] accountKeySecret) {
		this.accountKeySecret = accountKeySecret;
		return this;
	}

	public String getAccountLocation() {
		return accountLocation;
	}

	public UserAccountEntity setAccountLocation(String accountLocation) {
		this.accountLocation = accountLocation;
		return this;
	}

	public byte[] getAccountPublicKey() {
		return accountPublicKey;
	}

	public UserAccountEntity setAccountPublicKey(byte[] accountPublicKey) {
		this.accountPublicKey = accountPublicKey;
		return this;
	}

	public UserEntity getUser() {
		return user;
	}

	public UserAccountEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}
}
