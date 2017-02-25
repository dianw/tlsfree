/*
 * Copyright 2016 Dian Aditya
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
package org.tlsfree.registration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.shredzone.acme4j.Session;
import org.tlsfree.account.Account;
import org.tlsfree.blob.BlobStore;
import org.tlsfree.core.persistence.AbstractAuditingEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "acme_registration")
public class Registration extends AbstractAuditingEntity {
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	@JsonIgnore
	@Lob
	@Column(name = "registration_uri")
	private String registrationUri;
	@Lob
	@Column(name = "agreement_uri")
	private String agreementUri;
	@JsonIgnore
	@Transient
	private org.shredzone.acme4j.Registration registration;
	@JsonIgnore
	@Transient
	private Session session;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "public_key_blob_id")
	private BlobStore publicKey;
	@Column(name = "accept_agreement")
	private boolean acceptAgreement = false;

	public Account getAccount() {
		return account;
	}

	public Registration setAccount(Account account) {
		this.account = account;
		return this;
	}

	public String getRegistrationUri() {
		return registrationUri;
	}

	public Registration setRegistrationUri(String registrationUri) {
		this.registrationUri = registrationUri;
		return this;
	}

	public String getAgreementUri() {
		return agreementUri;
	}

	public Registration setAgreementUri(String agreementUri) {
		this.agreementUri = agreementUri;
		return this;
	}

	public org.shredzone.acme4j.Registration getAcmeRegistration() {
		return registration;
	}

	public Registration setAcmeRegistration(org.shredzone.acme4j.Registration registration) {
		this.registration = registration;
		this.registrationUri = registration.getLocation().toString();
		this.agreementUri = registration.getAgreement().toString();
		return this;
	}

	public Session getAcmeSession() {
		return session;
	}

	public Registration setAcmeSession(Session session) {
		this.session = session;
		return this;
	}

	public BlobStore getPublicKey() {
		return publicKey;
	}

	public Registration setPublicKey(BlobStore publicKey) {
		this.publicKey = publicKey;
		return this;
	}

	public boolean isAcceptAgreement() {
		return acceptAgreement;
	}

	public Registration setAcceptAgreement(boolean acceptAgreement) {
		this.acceptAgreement = acceptAgreement;
		return this;
	}
}
