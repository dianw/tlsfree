package org.enkrip.core.enc;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

public class KeyUtils {
	private static final String ASYMMETRIC_ENCRYPTION_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	private static final String SYMMETRIC_ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final SecureRandom RANDOM = new SecureRandom();

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private KeyUtils() {
	}

	public static SecretKey generateAESSecretKey(int keySize) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(keySize, RANDOM);
			return keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public static KeyPair generateKeyPairFromPrivateKey(PrivateKey privateKey) {
		Validate.isInstanceOf(RSAPrivateCrtKey.class, privateKey, "Only RSA private key are currently supported");
		RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(privateKey.getAlgorithm());
			PublicKey publicKey = keyFactory
				.generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent()));
			return new KeyPair(publicKey, rsaPrivateKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException(e);
		}
	}

	public static KeyPair generateRSAKeyPair(int keySize) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keySize);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private static byte[] nextRandomBytes(int size) {
		byte[] bytes = new byte[size];
		RANDOM.nextBytes(bytes);
		return bytes;
	}

	public static KeyPair readKeyPair(Reader reader, char[] password) throws IOException, OperatorCreationException, PKCSException {
		try (PEMParser pemParser = new PEMParser(reader)) {
			Object pemObject = pemParser.readObject();
			JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter();
			if (pemObject instanceof PEMKeyPair) {
				return pemKeyConverter.getKeyPair((PEMKeyPair) pemObject);
			}
			if (pemObject instanceof PKCS8EncryptedPrivateKeyInfo) {
				PKCS8EncryptedPrivateKeyInfo encryptedPrivateKey = (PKCS8EncryptedPrivateKeyInfo) pemObject;
				PrivateKeyInfo privateKey = encryptedPrivateKey.decryptPrivateKeyInfo(new JceOpenSSLPKCS8DecryptorProviderBuilder().build(password));
				return generateKeyPairFromPrivateKey(pemKeyConverter.getPrivateKey(privateKey));
			}
			throw new IllegalArgumentException("Invalid PEM file " + pemObject);
		}
	}

	public static PrivateKey unwrapPrivateKeyKey(byte[] keyToBeUnwrapped, String wrappedKeyAlg, SecretKey decryptionKey) {
		try {
			byte[] iv = ArrayUtils.subarray(keyToBeUnwrapped, 0, 16);
			byte[] keyToUnwrap = ArrayUtils.subarray(keyToBeUnwrapped, 16, keyToBeUnwrapped.length);
			Cipher cipher = Cipher.getInstance(SYMMETRIC_ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.UNWRAP_MODE, decryptionKey, new IvParameterSpec(iv));
			return (PrivateKey) cipher.unwrap(keyToUnwrap, wrappedKeyAlg, Cipher.PRIVATE_KEY);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static SecretKey unwrapSecretKey(byte[] keyToBeUnwrapped, String wrappedKeyAlg, PrivateKey decryptionKey) {
		try {
			Cipher cipher = Cipher.getInstance(ASYMMETRIC_ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.UNWRAP_MODE, decryptionKey);
			return (SecretKey) cipher.unwrap(keyToBeUnwrapped, wrappedKeyAlg, Cipher.SECRET_KEY);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException  e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte[] wrapPrivateKey(PrivateKey keyToBeWrapped, SecretKey encryptionKey) {
		try {
			byte[] iv = nextRandomBytes(16);
			Cipher cipher = Cipher.getInstance(SYMMETRIC_ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.WRAP_MODE, encryptionKey, new IvParameterSpec(iv));
			byte[] wrappedPrivateKey = cipher.wrap(keyToBeWrapped);
			return ArrayUtils.addAll(iv, wrappedPrivateKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException | IllegalBlockSizeException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static byte[] wrapSecretKey(SecretKey keyToBeWrapped, PublicKey encryptionKey) {
		try {
			Cipher cipher = Cipher.getInstance(ASYMMETRIC_ENCRYPTION_ALGORITHM);
			cipher.init(Cipher.WRAP_MODE, encryptionKey);
			return cipher.wrap(keyToBeWrapped);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException | IllegalBlockSizeException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static void writeKeyPair(Writer writer, KeyPair keyPair, char[] password) throws IOException {
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
			if (ArrayUtils.isEmpty(password)) {
				pemWriter.writeObject(keyPair);
			} else {
				JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.AES_256_CBC)
					.setPasssword(password);
				pemWriter.writeObject(new JcaPKCS8Generator(keyPair.getPrivate(), encryptorBuilder.build()));
			}
		} catch (OperatorCreationException e) {
			throw new IllegalStateException(e);
		}
	}
}
