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
package org.tlsfree.core.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class KeyPairConfig {
	private RelaxedPropertyResolver env;
	private ResourceLoader resourceLoader;

	public KeyPairConfig(Environment env, ResourceLoader resourceLoader) {
		this.env = new RelaxedPropertyResolver(env, "acme.account.key.");
		this.resourceLoader = resourceLoader;
	}

	@Bean
	public KeyPair serverKeyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String pemString = env.getProperty("pem");
		String pemLocation = env.getProperty("location", "classpath:/key.pem");
		String pemPassword = env.getProperty("passphrase");

		InputStream pemInput = null;
		PEMParser pemParser = null;

		try {
			if (pemString == null) {
				pemInput = resourceLoader.getResource(pemLocation).getInputStream();
				pemParser = new PEMParser(new BufferedReader(new InputStreamReader(pemInput)));
			} else {
				pemParser = new PEMParser(new StringReader(pemString));
			}

			Object object = pemParser.readObject();

			if (object instanceof PEMKeyPair) {
				return getKeyPairFromPem((PEMKeyPair) object);
			} else if (object instanceof PEMEncryptedKeyPair) {
				PEMEncryptedKeyPair pemEncryptedKeyPair = (PEMEncryptedKeyPair) object;
				return getKeyPairFromPem(pemEncryptedKeyPair.decryptKeyPair(new BcPEMDecryptorProvider(pemPassword.toCharArray())));
			} else {
				throw new IOException("Invalid PEM file");
			}
		} finally {
			if (pemParser != null) pemParser.close();
			if (pemInput != null) pemInput.close();
		}
	}

	private KeyPair getKeyPairFromPem(PEMKeyPair pemKeyPair) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();
		SubjectPublicKeyInfo publicKeyInfo = pemKeyPair.getPublicKeyInfo();
		
		if (!PKCSObjectIdentifiers.rsaEncryption.equals(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
			throw new NoSuchAlgorithmException("Only RSA algorithm are supported");
		}

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded()));
		PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyInfo.getEncoded()));
		
		return new KeyPair(publicKey, privateKey);
	}
}
