package org.enkrip.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.hibernate.validator.constraints.Email;

@Entity
@Table(name = "enkrip_user_account_contact")
public class UserAccountContactEntity extends AbstractAuditingEntity {
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_account_id", nullable = false)
	private UserAccountEntity userAccount;
	@Email
	@NotNull
	@Column(nullable = false)
	private String email;

	public String getEmail() {
		return email;
	}

	public UserAccountContactEntity setEmail(String email) {
		this.email = email;
		return this;
	}

	public UserAccountEntity getUserAccount() {
		return userAccount;
	}

	public UserAccountContactEntity setUserAccount(UserAccountEntity userAccount) {
		this.userAccount = userAccount;
		return this;
	}
}
