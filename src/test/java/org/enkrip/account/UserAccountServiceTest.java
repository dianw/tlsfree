package org.enkrip.account;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.codenergic.theskeleton.user.UserEntity;
import org.enkrip.account.impl.UserAccountServiceImpl;
import org.enkrip.core.enc.KeyUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shredzone.acme4j.Login;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.connector.Connection;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.provider.AcmeProvider;
import org.shredzone.acme4j.toolbox.JSON;
import org.shredzone.acme4j.toolbox.JSONBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserAccountServiceTest {
	private UserAccountService userAccountService;
	private KeyPair keyPair = KeyUtils.generateRSAKeyPair(1024);
	@Mock
	private Session acmeSession;
	@Mock
	private UserAccountRepository userAccountRepository;
	@Mock
	private UserAccountContactRepository userAccountContactRepository;

	private UserAccountEntity createUserAccount() {
		return userAccountService.createUserAccount(keyPair, new UserAccountEntity(), KeyUtils.generateRSAKeyPair(512))
			.setUser(new UserEntity().setId("123"))
			.setAccountLocation("http://testing");
	}

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		userAccountService = new UserAccountServiceImpl(keyPair, acmeSession, userAccountRepository, userAccountContactRepository);
		when(acmeSession.provider()).thenReturn(new AcmeProvider() {
			@Override
			public boolean accepts(URI serverUri) {
				return false;
			}

			@Override
			public Connection connect() {
				return new Connection() {
					@Override
					public void close() {

					}

					@Override
					public Collection<URL> getLinks(String relation) {
						return null;
					}

					@Override
					public URL getLocation() {
						try {
							return URI.create("http://123").toURL();
						} catch (MalformedURLException e) {
							throw new IllegalStateException(e);
						}
					}

					@Override
					public String getNonce() {
						return "";
					}

					@Override
					public void handleRetryAfter(String message) throws AcmeException {

					}

					@Override
					public List<X509Certificate> readCertificates() throws AcmeException {
						return null;
					}

					@Override
					public JSON readJsonResponse() throws AcmeException {
						return JSON.empty();
					}

					@Override
					public void resetNonce(Session session) throws AcmeException {

					}

					@Override
					public void sendRequest(URL url, Session session) throws AcmeException {

					}

					@Override
					public int sendSignedRequest(URL url, JSONBuilder claims, Login login) throws AcmeException {
						return 0;
					}

					@Override
					public int sendSignedRequest(URL url, JSONBuilder claims, Session session, KeyPair keypair) throws AcmeException {
						return 0;
					}
				};
			}

			@Override
			public Challenge createChallenge(Login login, JSON data) {
				return null;
			}

			@Override
			public JSON directory(Session session, URI serverUri) throws AcmeException {
				return null;
			}

			@Override
			public URL resolve(URI serverUri) {
				return null;
			}
		});
		when(acmeSession.login(any(), any())).then(invocation ->
			new Login(invocation.getArgument(0), invocation.getArgument(1), acmeSession));
	}

	@Test
	public void testDeactivateAccount() throws AcmeException {
		when(userAccountRepository.findOne("123")).thenReturn(createUserAccount());
		userAccountService.deactivateAccount("123", "123");
		verify(userAccountRepository).delete("123");
		verify(userAccountContactRepository).deleteByUserAccountId("123");
	}

	@Test
	public void testFindAccountContactsByAccount() {
		when(userAccountContactRepository.findByUserAccountId("123", null))
			.thenReturn(new PageImpl<>(Collections.singletonList(new UserAccountContactEntity().setEmail("123"))));
		Page<UserAccountContactEntity> contacts = userAccountService.findAccountContactsByAccount("123", null);
		assertThat(contacts).hasSize(1);
		UserAccountContactEntity contact = contacts.iterator().next();
		assertThat(contact.getEmail()).isEqualTo("123");
		assertThat(contact.getUserAccount()).isNull();
		verify(userAccountContactRepository).findByUserAccountId("123", null);
	}

	@Test
	public void testFindLoginByUserAccount() throws AcmeException {
		when(userAccountRepository.findOne("123")).thenReturn(createUserAccount());
		Login login = userAccountService.findLoginByUserAccount("123", "123");
		assertThat(login.getSession()).isEqualTo(acmeSession);
		verify(userAccountRepository).findOne("123");
	}

	@Test
	public void testFindUserAccountById() throws AcmeException {
		when(userAccountRepository.findOne("123")).thenReturn(createUserAccount());
		UserAccountEntity account = userAccountService.findUserAccountById("123");
		assertThat(account.getUser()).isNotNull();
		verify(userAccountRepository).findOne("123");
	}

	@Test
	public void testFindUserAccountsByUser() {
		when(userAccountRepository.findByUserId("123", null)).thenReturn(new PageImpl<>(Collections.emptyList()));
		Page<UserAccountEntity> accounts = userAccountService.findUserAccountsByUser("123", null);
		assertThat(accounts).isEmpty();
		verify(userAccountRepository).findByUserId("123", null);
	}

	@Test
	public void testRegisterNewUserAccount() throws AcmeException {
		UserAccountEntity account = userAccountService.registerNewUserAccount(new UserEntity(), Collections.singletonList(new UserAccountContactEntity().setEmail("test")));
		assertThat(account.getUser()).isNotNull();
		assertThat(account.getAccountLocation()).isEqualTo(acmeSession.provider().connect().getLocation().toExternalForm());
		assertThat(account.getAccountPublicKey()).isNotEmpty();
		assertThat(account.getAccountKey()).isNotEmpty();
		assertThat(account.getAccountKeySecret()).isNotEmpty();
		verify(userAccountRepository).save(account);
		verify(userAccountContactRepository).save(anyList());
	}

	@Test
	public void testUpdateUserAccountContacts() throws AcmeException {
		when(userAccountRepository.findOne("123")).thenReturn(createUserAccount());
		UserAccountEntity account = userAccountService.updateUserAccountContacts("123", "123", Collections.singletonList(new UserAccountContactEntity().setEmail("123123")));
		assertThat(account).isNotNull();
		verify(userAccountRepository).findOne("123");
		verify(userAccountContactRepository).deleteByUserAccountId("123");
		verify(userAccountContactRepository).save(anyList());
	}

	@Test
	public void testUpdateUserAccountKeyPair() throws AcmeException {
		when(userAccountRepository.findOne("123")).thenReturn(createUserAccount());
		UserAccountEntity account = userAccountService.updateUserAccountKeyPair("123", "123");
		assertThat(account).isNotNull();
		verify(userAccountRepository).findOne("123");
	}
}
