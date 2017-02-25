package org.tlsfree.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KeyPairConfigTest {
	@Inject
	private ApplicationContext context;

	@Inject
	@Named("acmeAccountKeyPair")
	private KeyPair keyPair;

	@Test
	public void testKeyPairBean() {
		KeyPair keyPair = context.getBean("serverKeyPair", KeyPair.class);

		assertThat(this.keyPair).as("Bean name is acmeAccountKeyPair").isEqualTo(keyPair);
	}

	@Test
	public void testKeyAlgIsRSA() {
		assertThat(keyPair.getPrivate()).as("Key algorithm is RSA").isInstanceOf(RSAPrivateKey.class);
	}
}
