package org.enkrip.core.enc;

import org.junit.Test;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.KeyPair;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MasterKeyConfigTest {
	private MasterKeyConfig masterKeyConfig = new MasterKeyConfig();
	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Test
	public void testMasterKeyPairGenerateOnStartup() throws IOException {
		MasterKeyConfig.MasterKeyConfigProperties properties = masterKeyConfig.configurationProperties();
		properties.setGenerateOnStartup(true);
		KeyPair keyPair = masterKeyConfig.masterKeyPair(properties, resourceLoader);
		assertThat(keyPair).isNotNull();
	}

	@Test
	public void testMasterKeyPairLoadMasterKeyWithError() throws IOException {
		MasterKeyConfig.MasterKeyConfigProperties properties = new MasterKeyConfig.MasterKeyConfigProperties();
		properties.setGenerateOnStartup(false);
		properties.setLocation("");
		assertThatThrownBy(() -> masterKeyConfig.masterKeyPair(properties, resourceLoader)).isInstanceOf(IllegalArgumentException.class);

		properties.setLocation("file:" + UUID.randomUUID().toString());
		assertThatThrownBy(() -> masterKeyConfig.masterKeyPair(properties, resourceLoader)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testMasterKeyPairLoadMaster() throws IOException {
		File tempFile = File.createTempFile("enkrip-test-" + UUID.randomUUID().toString(), ".pem");
		try (Writer keyWriter = new FileWriter(tempFile)) {
			KeyPairUtils.writeKeyPair(KeyPairUtils.createKeyPair(512), keyWriter);
		}
		MasterKeyConfig.MasterKeyConfigProperties properties = new MasterKeyConfig.MasterKeyConfigProperties();
		properties.setGenerateOnStartup(false);
		properties.setLocation("file:" + tempFile.getAbsolutePath());

		KeyPair keyPair = masterKeyConfig.masterKeyPair(properties, resourceLoader);
		assertThat(keyPair).isNotNull();
	}
}
