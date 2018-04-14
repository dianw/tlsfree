package org.enkrip.core.enc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyPair;

import org.apache.commons.lang3.StringUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class MasterKeyConfig {
	private static final int MASTER_KEY_SIZE = 4096;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	@ConfigurationProperties("enkrip.master-key")
	public MasterKeyConfigProperties configurationProperties() {
		return new MasterKeyConfigProperties();
	}

	private KeyPair generateMasterKey() throws IOException {
		File keyFile = File.createTempFile("enkrip-" + System.currentTimeMillis(), ".pem");
		logger.info("Generating key-pair [{}]", keyFile.getAbsolutePath());
		KeyPair keyPair = KeyPairUtils.createKeyPair(MASTER_KEY_SIZE);
		try (FileWriter fileWriter = new FileWriter(keyFile)) {
			KeyPairUtils.writeKeyPair(keyPair, fileWriter);
		}
		logger.info("Key-pair generated");
		return keyPair;
	}

	private KeyPair loadMasterKey(String stringLocation, ResourceLoader resourceLoader) throws IOException {
		Resource keyResource = resourceLoader.getResource(stringLocation);
		if (!keyResource.exists()) {
			throw new IllegalArgumentException("Cannot find master key file in [" + keyResource.getURL() + "]");
		}
		logger.info("Loading key-pair [{}]", keyResource.getURL());
		try (Reader keyReader = new InputStreamReader(keyResource.getInputStream())) {
			return KeyPairUtils.readKeyPair(keyReader);
		} finally {
			logger.info("Key-pair loaded");
		}
	}

	@Bean
	@Primary
	public KeyPair masterKeyPair(MasterKeyConfigProperties properties, ResourceLoader resourceLoader) throws IOException {
		if (properties.generateOnStartup) {
			return generateMasterKey();
		}
		if (StringUtils.isBlank(properties.location)) {
			throw new IllegalArgumentException("Parameter [enkrip.master-key.location] is mandatory");
		}
		return loadMasterKey(properties.location, resourceLoader);
	}

	public static class MasterKeyConfigProperties {
		private boolean generateOnStartup = true;
		private String location;

		public void setGenerateOnStartup(boolean generateOnStartup) {
			this.generateOnStartup = generateOnStartup;
		}

		public void setLocation(String location) {
			this.location = location;
		}
	}
}
