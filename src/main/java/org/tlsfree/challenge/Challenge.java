package org.tlsfree.challenge;

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
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.tlsfree.authorization.Authorization;
import org.tlsfree.core.persistence.AbstractAuditingEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "acme_challenge")
public class Challenge extends AbstractAuditingEntity {
	@ManyToOne
	@JoinColumn(name = "authorization_id")
	private Authorization authorization;
	@Transient
	@JsonIgnore
	private org.shredzone.acme4j.challenge.Challenge challenge;
	@Column(name = "type_")
	private String type;
	@Lob
	@Column(name = "challenge_uri")
	private String challengeUri;
	@Column(name = "acme_status")
	@Enumerated(EnumType.STRING)
	private Status status;
	@Column(name = "key_")
	private String key;
	@Column(name = "value_")
	private String value;
	@Column(name = "validated_date")
	private Date validatedDate;

	public Authorization getAuthorization() {
		return authorization;
	}

	public Challenge setAuthorization(Authorization authorization) {
		this.authorization = authorization;
		return this;
	}

	public org.shredzone.acme4j.challenge.Challenge getAcmeChallenge() {
		return challenge;
	}

	public Challenge setAcmeChallenge(org.shredzone.acme4j.challenge.Challenge challenge) {
		this.challenge = challenge;
		this.type = challenge.getType();
		this.challengeUri = challenge.getLocation().toString();
		this.status = challenge.getStatus();
		this.validatedDate = challenge.getValidated();

		if (challenge instanceof Http01Challenge) {
			Http01Challenge http01Challenge = (Http01Challenge) challenge;
			this.key = http01Challenge.getToken();
			this.value = http01Challenge.getAuthorization();
		} else if (challenge instanceof Dns01Challenge) {
			Dns01Challenge dns01Challenge = (Dns01Challenge) challenge;
			this.key = dns01Challenge.getDigest();
			this.value = dns01Challenge.getDigest();
		}

		return this;
	}

	public String getType() {
		return type;
	}

	public Challenge setType(String type) {
		this.type = type;
		return this;
	}

	public String getChallengeUri() {
		return challengeUri;
	}

	public Challenge setChallengeUri(String challengeUri) {
		this.challengeUri = challengeUri;
		return this;
	}

	public Status getAcmeStatus() {
		return status;
	}

	public Challenge setAcmeStatus(Status status) {
		this.status = status;
		return this;
	}

	public String getKey() {
		return key;
	}

	public Challenge setKey(String key) {
		this.key = key;
		return this;
	}

	public String getValue() {
		return value;
	}

	public Challenge setValue(String value) {
		this.value = value;
		return this;
	}

	public Date getValidatedDate() {
		return validatedDate;
	}

	public Challenge setValidatedDate(Date validatedDate) {
		this.validatedDate = validatedDate;
		return this;
	}
}