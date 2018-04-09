package org.enkrip.core.enc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.shredzone.acme4j.util.KeyPairUtils;

public class KeyUtilsTest {

	@Test
	public void testGenerateAESSecretKey() {
		SecretKey secretKey = KeyUtils.generateAESSecretKey(256); // generate 256 bit AES secret key
		assertThat(secretKey.getAlgorithm()).isEqualTo("AES");
		assertThat(secretKey.getEncoded()).hasSize(32); // 32 bytes
	}

	@Test
	public void testGenerateKeyPairFromPrivateKey() {
		KeyPair keyPair = KeyPairUtils.createKeyPair(1024);
		KeyPair generatedKeyPair = KeyUtils.generateKeyPairFromPrivateKey(keyPair.getPrivate());
		assertThat(generatedKeyPair.getPublic()).isEqualTo(keyPair.getPublic());

		Security.addProvider(new BouncyCastleProvider());
		KeyPair ecKeyPair = KeyPairUtils.createECKeyPair("secp256r1");
		assertThatThrownBy(() -> {
			KeyUtils.generateKeyPairFromPrivateKey(ecKeyPair.getPrivate());
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testWrapAndUnwrapKey() {
		KeyPair encryptionKeyPair = KeyPairUtils.createKeyPair(1024);
		SecretKey secretKey = KeyUtils.generateAESSecretKey(256);
		assertThat(secretKey.getEncoded()).hasSize(32);
		byte[] wrappedSecretKey = KeyUtils.wrapSecretKey(secretKey, encryptionKeyPair.getPublic());
		assertThat(wrappedSecretKey).hasSize(128);
		SecretKey unwrappedSecretKey = KeyUtils.unwrapSecretKey(wrappedSecretKey, secretKey.getAlgorithm(),
				encryptionKeyPair.getPrivate());
		assertThat(unwrappedSecretKey).isEqualTo(secretKey);
	}
	
	@Test
	public void testWrapAndUnwrapPrivateKey() {
		KeyPair keyPair = KeyPairUtils.createKeyPair(1024);
		SecretKey encryptionKey = KeyUtils.generateAESSecretKey(256);
		byte[] wrappedSecretKey = KeyUtils.wrapPrivateKey(keyPair.getPrivate(), encryptionKey);
		PrivateKey privateKey = KeyUtils.unwrapPrivateKeyKey(wrappedSecretKey, keyPair.getPrivate().getAlgorithm(), encryptionKey);
		assertThat(privateKey).isEqualTo(keyPair.getPrivate());
	}
}
