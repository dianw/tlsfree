package org.enkrip.core.enc;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
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

public class KeyUtils {
	private static final String ASYMMETRIC_ENCRYPTION_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	private static final String SYMMETRIC_ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final SecureRandom RANDOM = new SecureRandom();

	private KeyUtils() {
	}

	private static byte[] nextRandomBytes(int size) {
		byte[] bytes = new byte[size];
		RANDOM.nextBytes(bytes);
		return bytes;
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

	public static final KeyPair generateKeyPairFromPrivateKey(PrivateKey privateKey) {
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

	public static final PrivateKey unwrapPrivateKeyKey(byte[] keyToBeUnwrapped, String wrappedKeyAlg, SecretKey decryptionKey) {
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

	public static final SecretKey unwrapSecretKey(byte[] keyToBeUnwrapped, String wrappedKeyAlg, PrivateKey decryptionKey) {
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

	public static final byte[] wrapPrivateKey(PrivateKey keyToBeWrapped, SecretKey encryptionKey) {
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

	public static final byte[] wrapSecretKey(SecretKey keyToBeWrapped, PublicKey encryptionKey) {
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
}
