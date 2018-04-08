package org.enkrip.core.acme;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.shredzone.acme4j.Session;

import java.net.URI;

public class ACMESessionConfigTest {
	private ACMESessionConfig acmeSessionConfig = new ACMESessionConfig();

	@Test
	public void testAcmeSession() {
		ACMESessionConfig.SessionConfigProperties properties = acmeSessionConfig.acmeSessionConfigProperties();
		properties.setServerUri("acme://letsencrypt.org/staging");
		Session session = acmeSessionConfig.acmeSession(properties);
		Assertions.assertThat(session.getServerUri()).isEqualTo(URI.create(properties.serverUri));
	}
}
