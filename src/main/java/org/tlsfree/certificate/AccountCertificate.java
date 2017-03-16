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
package org.tlsfree.certificate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tlsfree.account.Account;
import org.tlsfree.blob.BlobStore;
import org.tlsfree.core.persistence.AbstractAuditingEntity;
import org.tlsfree.registration.Registration;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "account_certificate")
public class AccountCertificate extends AbstractAuditingEntity {
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	@ManyToOne
	@JoinColumn(name = "registration_id")
	private Registration registration;
	private String domain;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "certificate_blob_id")
	private BlobStore certificate;
	@JsonIgnore
	@Column(name = "certificate_uri")
	private String certificateUri;
	private boolean revoked = false;

	public Account getAccount() {
		return account;
	}

	public AccountCertificate setAccount(Account account) {
		this.account = account;
		return this;
	}

	public Registration getRegistration() {
		return registration;
	}

	public AccountCertificate setRegistration(Registration registration) {
		this.registration = registration;
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public AccountCertificate setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public BlobStore getCertificate() {
		return certificate;
	}

	public AccountCertificate setCertificate(BlobStore certificate) {
		this.certificate = certificate;
		return this;
	}

	public String getCertificateUri() {
		return certificateUri;
	}

	public AccountCertificate setCertificateUri(String certificateUri) {
		this.certificateUri = certificateUri;
		return this;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public AccountCertificate setRevoked(boolean revoked) {
		this.revoked = revoked;
		return this;
	}
}
