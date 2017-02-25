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
package org.tlsfree.authorization;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.shredzone.acme4j.Status;
import org.tlsfree.core.persistence.AbstractAuditingEntity;
import org.tlsfree.registration.Registration;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "acme_authorization")
public class Authorization extends AbstractAuditingEntity {
	@JsonIgnore
	@Transient
	private org.shredzone.acme4j.Authorization authorization;
	private String domain;
	@JsonIgnore
	@Lob
	@Column(name = "authorization_uri")
	private String authorizationUri;
	@Column(name = "acme_status")
	@Enumerated(EnumType.STRING)
	private Status status;
	private Date expires;
	@ManyToOne
	@JoinColumn(name = "registration_id")
	private Registration registration;

	public org.shredzone.acme4j.Authorization getAcmeAuthorization() {
		return authorization;
	}

	public Authorization setAcmeAuthorization(org.shredzone.acme4j.Authorization authorization) {
		this.authorization = authorization;
		this.domain = authorization.getDomain();
		this.authorizationUri = authorization.getLocation().toString();
		this.status = authorization.getStatus();
		this.expires = authorization.getExpires();
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public Authorization setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public String getAuthorizationUri() {
		return authorizationUri;
	}

	public Authorization setAuthorizationUri(String authorizationUri) {
		this.authorizationUri = authorizationUri;
		return this;
	}

	public Status getAcmeStatus() {
		return status;
	}

	public Authorization setAcmeStatus(Status status) {
		this.status = status;
		return this;
	}

	public Date getExpires() {
		return expires;
	}

	public Authorization setExpires(Date expires) {
		this.expires = expires;
		return this;
	}

	public Registration getRegistration() {
		return registration;
	}

	public Authorization setRegistration(Registration registration) {
		this.registration = registration;
		return this;
	}
}
