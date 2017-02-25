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
package org.tlsfree.certificate.wrapper;

import java.math.BigInteger;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class X509CertificateWrapper {
	private X509Certificate certificate;

	public X509CertificateWrapper(X509Certificate certificate) {
		this.certificate = certificate;
	}

	@JsonIgnore
	public X509Certificate getX509Certificate() {
		return certificate;
	}

	public byte[] getEncoded() throws CertificateEncodingException {
		return certificate.getEncoded();
	}

	public byte[] getSigAlgParams() {
		return certificate.getSigAlgParams();
	}

	public byte[] getSignature() {
		return certificate.getSignature();
	}

	public byte[] getTBSCertificate() throws CertificateEncodingException {
		return certificate.getTBSCertificate();
	}

	public KeyWrapper getPublicKey() {
		return new KeyWrapper(certificate.getPublicKey());
	}

	public X500Principal getIssuerX500Principal() {
		return certificate.getIssuerX500Principal();
	}

	public X500Principal getSubjectX500Principal() {
		return certificate.getSubjectX500Principal();
	}

	public String getSigAlgOID() {
		return certificate.getSigAlgOID();
	}

	public Principal getSubjectDN() {
		return certificate.getSubjectDN();
	}

	public Principal getIssuerDN() {
		return certificate.getIssuerDN();
	}

	public Date getNotAfter() {
		return certificate.getNotAfter();
	}

	public Date getNotBefore() {
		return certificate.getNotBefore();
	}

	public BigInteger getSerialNumber() {
		return certificate.getSerialNumber();
	}

	public String getSigAlgName() {
		return certificate.getSigAlgName();
	}

	public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
		return certificate.getIssuerAlternativeNames();
	}

	public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
		return certificate.getSubjectAlternativeNames();
	}
}
