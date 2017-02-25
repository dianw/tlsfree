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
package org.tlsfree.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RSA key utilities
 * 
 * @author dianw
 *
 */
public class RSAKeyUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAKeyUtils.class);
	public static final int DEFAULT_KEY_SIZE = 4096;

	private RSAKeyUtils() {
	}

	/**
	 * Generates 4096 bits RSA key pair
	 * 
	 * @return the RSA key pair or null if error
	 */
	public static final KeyPair generateKeyPair() {
		return generateKeyPair(DEFAULT_KEY_SIZE);
	}

	/**
	 * Generates RSA key pair
	 * 
	 * @param keySize
	 * @return the RSA key pair or null if error
	 */
	public static final KeyPair generateKeyPair(int keySize) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keySize);

			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.warn(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Generates public key from given private key bytes
	 * 
	 * @param privateKey
	 * @return the RSA key pair or null if error
	 */
	public static final KeyPair generatePairFromPrivateKey(byte[] privateKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey pk = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));

			return generatePairFromPrivateKey(pk);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOGGER.warn(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Generates public key from given private key
	 * 
	 * @param privateKey
	 * @return the RSA key pair or null if error
	 */
	public static final KeyPair generatePairFromPrivateKey(PrivateKey privateKey) {
		try {
			if (!(privateKey instanceof RSAPrivateCrtKey)) throw new IllegalArgumentException("Not a RSA private key");

			RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) privateKey;

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory
					.generatePublic(new RSAPublicKeySpec(privateCrtKey.getModulus(), privateCrtKey.getPublicExponent()));

			return new KeyPair(publicKey, privateKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOGGER.warn(e.getMessage(), e);
		}

		return null;
	}
}
