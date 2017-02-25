package org.tlsfree.key;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.KeyPair;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.tlsfree.crypto.PrivateKeyEncryptionUtils;
import org.tlsfree.crypto.RSAKeyUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrivateKeyEncryptionUtilsTest {
	private KeyPair encryptorKeyPair;
	private KeyPair keyPair;

	@Before
	public void before() {
		encryptorKeyPair = RSAKeyUtils.generateKeyPair(1024);
		keyPair = RSAKeyUtils.generateKeyPair(1024);
	}

	@Test
	public void test1EncryptPrivateKey() throws CMSException, IOException {
		byte[] encryptedPk = PrivateKeyEncryptionUtils.encrypt(encryptorKeyPair.getPublic(), keyPair.getPrivate());

		CMSEnvelopedData cmsEnvelopedData = new CMSEnvelopedData(encryptedPk);
		assertThat(cmsEnvelopedData.getContentEncryptionAlgorithm().getAlgorithm()).isEqualTo(CMSAlgorithm.AES256_CBC);

		RecipientInformationStore recipientInfos = cmsEnvelopedData.getRecipientInfos();
		assertThat(recipientInfos.size()).isEqualTo(1);
		
		RecipientInformation recipientInfo = recipientInfos.iterator().next();
		assertThat(recipientInfo.getRID()).isInstanceOf(KeyTransRecipientId.class);
		assertThat(recipientInfo.getRID()).hasFieldOrPropertyWithValue("subjectKeyIdentifier", encryptorKeyPair.getPublic().getEncoded());
	}

	@Test
	public void test2DecryptPrivateKey() throws CMSException, IOException {
		byte[] encryptedPk = PrivateKeyEncryptionUtils.encrypt(encryptorKeyPair.getPublic(), keyPair.getPrivate());
		KeyPair decryptedKeyPair = PrivateKeyEncryptionUtils.decrypt(encryptorKeyPair.getPrivate(), encryptedPk);
		assertThat(decryptedKeyPair.getPrivate().getEncoded()).isEqualTo(keyPair.getPrivate().getEncoded());
	}

	@Test(expected = CMSException.class)
	public void test3FailedDecryptPrivateKey() throws CMSException, IOException {
		byte[] encryptedPk = PrivateKeyEncryptionUtils.encrypt(encryptorKeyPair.getPublic(), keyPair.getPrivate());
		PrivateKeyEncryptionUtils.decrypt(keyPair.getPrivate(), encryptedPk);
	}
}
