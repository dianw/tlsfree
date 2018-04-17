package org.enkrip.core.enc;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class KeyUtilsTest {

	@Test
	public void testGenerateAESSecretKey() {
		SecretKey secretKey = KeyUtils.generateAESSecretKey(256); // generate 256 bit AES secret key
		assertThat(secretKey.getAlgorithm()).isEqualTo("AES");
		assertThat(secretKey.getEncoded()).hasSize(32); // 32 bytes
	}

	@Test
	public void testGenerateKeyPairFromPrivateKey() throws NoSuchAlgorithmException {
		KeyPair keyPair = KeyUtils.generateRSAKeyPair(1024);
		KeyPair generatedKeyPair = KeyUtils.generateKeyPairFromPrivateKey(keyPair.getPrivate());
		assertThat(generatedKeyPair.getPublic()).isEqualTo(keyPair.getPublic());

		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA");
		KeyPair ecKeyPair = keyPairGenerator.generateKeyPair();
		assertThatThrownBy(() -> {
			KeyUtils.generateKeyPairFromPrivateKey(ecKeyPair.getPrivate());
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testReadAndWriteEncryptedKeyPair() throws IOException, OperatorCreationException, PKCSException {
		char[] password = "sUp3RS3cret!!".toCharArray();
		KeyPair keyPair = KeyUtils.generateRSAKeyPair(1024);
		StringWriter pemWriter = new StringWriter();
		KeyUtils.writeKeyPair(pemWriter, keyPair, password);
		KeyPair keyPair2 = KeyUtils.readKeyPair(new StringReader(pemWriter.toString()), password);
		assertThat(keyPair2.getPublic()).isEqualTo(keyPair.getPublic());
		assertThat(keyPair2.getPrivate()).isEqualTo(keyPair2.getPrivate());
	}

	@Test
	public void testReadAndWriteKeyPair() throws IOException, OperatorCreationException, PKCSException {
		KeyPair keyPair = KeyUtils.generateRSAKeyPair(1024);
		StringWriter pemWriter = new StringWriter();
		KeyUtils.writeKeyPair(pemWriter, keyPair, null);
		KeyPair keyPair2 = KeyUtils.readKeyPair(new StringReader(pemWriter.toString()), null);
		assertThat(keyPair2.getPublic()).isEqualTo(keyPair.getPublic());
		assertThat(keyPair2.getPrivate()).isEqualTo(keyPair2.getPrivate());
	}

	@Test
	public void testWrapAndUnwrapKey() {
		KeyPair encryptionKeyPair = KeyUtils.generateRSAKeyPair(1024);
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
		KeyPair keyPair = KeyUtils.generateRSAKeyPair(1024);
		SecretKey encryptionKey = KeyUtils.generateAESSecretKey(256);
		byte[] wrappedSecretKey = KeyUtils.wrapPrivateKey(keyPair.getPrivate(), encryptionKey);
		PrivateKey privateKey = KeyUtils.unwrapPrivateKeyKey(wrappedSecretKey, keyPair.getPrivate().getAlgorithm(), encryptionKey);
		assertThat(privateKey).isEqualTo(keyPair.getPrivate());
	}
}
