/*
 * Copyright 2016-2017 Dian Aditya.
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

import java.io.StringReader;
import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.tlsfree.registration.Registration;

@FunctionalInterface
public interface AccountCertificateService {
	default List<X509Certificate> enrollCertificates(Registration registration, String csrPem) {
		try (PEMParser parser = new PEMParser(new StringReader(csrPem))) {
			Object pemObject = parser.readObject();
			if (!(pemObject instanceof PKCS10CertificationRequest)) {
				throw new CertificateException("Invalid PEM file");
			}
			PKCS10CertificationRequest request = (PKCS10CertificationRequest) pemObject;
			return enrollCertificates(registration, request.getEncoded());
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}

	List<X509Certificate> enrollCertificates(Registration registration, byte[] csr);
}
