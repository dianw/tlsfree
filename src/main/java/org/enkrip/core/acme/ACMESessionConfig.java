package org.enkrip.core.acme;

import org.shredzone.acme4j.Session;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ACMESessionConfig {
	@Bean
	public Session acmeSession(SessionConfigProperties properties) {
		return new Session(properties.serverUri);
	}

	@Bean
	@ConfigurationProperties("enkrip.acme")
	public SessionConfigProperties acmeSessionConfigProperties() {
		return new SessionConfigProperties();
	}

	public static class SessionConfigProperties {
		String serverUri = "acme://letsencrypt.org/staging";

		public void setServerUri(String serverUri) {
			this.serverUri = serverUri;
		}
	}
}
