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

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.cms.CMSException;

/**
 * Utility class for encrypting and decrypting private key
 * 
 * @author dianw
 *
 */
public final class PrivateKeyEncryptionUtils {
	public PrivateKeyEncryptionUtils() {
	}

	/**
	 * Encrypt private key
	 * 
	 * @param encryptorKey
	 * @param privateKey
	 * @return encrypted private key
	 * @throws CMSException
	 * @throws IOException
	 */
	public static byte[] encrypt(PublicKey encryptorKey, PrivateKey privateKey) throws CMSException, IOException {
		return CMSEncryptionUtils.encrypt(privateKey.getEncoded(), encryptorKey.getEncoded(), encryptorKey);
	}

	/**
	 * Decrypt private key
	 * 
	 * @param decryptorKey
	 * @param encryptedPrivateKey
	 * @return key pair
	 * @throws CMSException
	 */
	public static KeyPair decrypt(PrivateKey decryptorKey, byte[] encryptedPrivateKey) throws CMSException {
		KeyPair decryptorKeyPair = RSAKeyUtils.generatePairFromPrivateKey(decryptorKey);
		return decryptPrivateKey(decryptorKeyPair, encryptedPrivateKey);
	}

	/**
	 * Decrypt private key
	 * 
	 * @param decryptorKey
	 * @param encryptedPrivateKey
	 * @return key pair
	 * @throws CMSException
	 */
	public static KeyPair decryptPrivateKey(KeyPair decryptorKeyPair, byte[] encryptedPrivateKey) throws CMSException {
		byte[] content = CMSEncryptionUtils.decrypt(encryptedPrivateKey, decryptorKeyPair.getPublic().getEncoded(), decryptorKeyPair.getPrivate());
		return RSAKeyUtils.generatePairFromPrivateKey(content);
	}
}
