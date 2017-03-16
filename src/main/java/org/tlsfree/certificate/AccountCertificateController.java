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

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.tlsfree.certificate.wrapper.X509CertificateWrapper;
import org.tlsfree.core.security.SecurityUtils;
import org.tlsfree.registration.Registration;

@RestController
@RequestMapping("/api/certificates")
public class AccountCertificateController {
	@Inject
	private AccountCertificateService accountCertificateService;

	@RequestMapping(method = RequestMethod.POST, consumes = "application/pkcs10")
	public ResponseEntity<?> enrollCert(@RequestBody byte[] csr) {
		Registration registration = SecurityUtils.getCurrentRegistration();
		List<X509Certificate> certificates = accountCertificateService.enrollCertificates(registration, csr);
		return ResponseEntity.ok(wrapX509Certificates(certificates));
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/x-pem-file")
	public ResponseEntity<?> enrollCert(@RequestBody String csrPem) {
		Registration registration = SecurityUtils.getCurrentRegistration();
		List<X509Certificate> certificates = accountCertificateService.enrollCertificates(registration, csrPem);
		return ResponseEntity.ok(wrapX509Certificates(certificates));
	}

	private List<X509CertificateWrapper> wrapX509Certificates(List<X509Certificate> certificates) {
		return certificates.stream()
				.map(X509CertificateWrapper::new)
				.collect(Collectors.toList());
	}
}
