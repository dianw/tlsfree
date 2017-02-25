/*
 * Copyright 2016 Dian Aditya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tlsfree.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tlsfree.blob.BlobStore;
import org.tlsfree.core.persistence.AbstractAuditingEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@Table(name = "account")
public class Account extends AbstractAuditingEntity {
	private String username;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private String email;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "private_key_blob_id")
	private BlobStore privateKey;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "public_key_blob_id")
	private BlobStore publicKey;
	@Column(name = "account_non_expired")
	private boolean accountNonExpired = true;
	@Column(name = "account_non_locked")
	private boolean accountNonLocked = true;
	@Column(name = "credentials_non_expired")
	private boolean credentialsNonExpired = true;
	@JsonProperty(access = Access.READ_ONLY)
	private boolean enabled = true;
	@JsonProperty(access = Access.READ_ONLY)
	private boolean administrator = false;

	public String getUsername() {
		return username;
	}

	public Account setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public Account setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Account setEmail(String email) {
		this.email = email;
		return this;
	}

	public BlobStore getPrivateKey() {
		return privateKey;
	}

	public Account setPrivateKey(BlobStore privateKey) {
		this.privateKey = privateKey;
		return this;
	}

	public BlobStore getPublicKey() {
		return publicKey;
	}

	public Account setPublicKey(BlobStore publicKey) {
		this.publicKey = publicKey;
		return this;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public Account setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
		return this;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public Account setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
		return this;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public Account setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Account setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public Account setAdministrator(boolean administrator) {
		this.administrator = administrator;
		return this;
	}
}
