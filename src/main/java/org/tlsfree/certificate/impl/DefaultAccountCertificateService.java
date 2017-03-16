package org.tlsfree.certificate.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;
import org.tlsfree.certificate.AccountCertificateService;
import org.tlsfree.certificate.CertificateException;
import org.tlsfree.registration.Registration;

@Service
public class DefaultAccountCertificateService implements AccountCertificateService {
	@Override
	public List<X509Certificate> enrollCertificates(Registration registration, byte[] csr) {
		try {
			PKCS10CertificationRequest request = new PKCS10CertificationRequest(csr);
			boolean valid = request.isSignatureValid(new JcaContentVerifierProviderBuilder()
					.setProvider("BC")
					.build(request.getSubjectPublicKeyInfo()));
			if (!valid) {
				throw new CertificateException("Invalid CSR signature");
			}
			org.shredzone.acme4j.Registration acmeRegistration = registration.getAcmeRegistration();
			org.shredzone.acme4j.Certificate certificate = acmeRegistration.requestCertificate(request.getEncoded());
			X509Certificate[] chain = certificate.downloadChain();
			X509Certificate cert = certificate.download();

			List<X509Certificate> certs = new ArrayList<>();
			certs.add(cert);
			certs.addAll(Arrays.asList(chain));
			return certs;
		} catch (Exception e) {
			throw new CertificateException(e);
		}
	}
}
