package org.tlsfree.key;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateCrtKey;

import org.junit.Test;
import org.tlsfree.crypto.RSAKeyUtils;

public class RSAKeyUtilsTest {
	@Test
	public void testGenerateKeyPair() {
		KeyPair keyPair = RSAKeyUtils.generateKeyPair(1024);
		assertThat(keyPair.getPrivate()).isInstanceOf(RSAPrivateCrtKey.class);

		KeyPair keyPair2 = RSAKeyUtils.generatePairFromPrivateKey(keyPair.getPrivate());
		assertThat(keyPair.getPrivate().getEncoded()).isEqualTo(keyPair2.getPrivate().getEncoded());
		assertThat(keyPair.getPublic().getEncoded()).isEqualTo(keyPair2.getPublic().getEncoded());
	}
}
