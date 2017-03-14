/*
 * Copyright 2016 Dian Aditya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tlsfree.crypto;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.OutputEncryptor;

/**
 * Utility class for encrypting and decrypting CMS enveloped-data content
 *
 * @author dianw
 *
 */
public final class CMSEncryptionUtils {
	private CMSEncryptionUtils() {
	}

	/**
	 * Encrypt data into ASN.1 encoded of CMS enveloped-data
	 *
	 * @param data
	 * @param subjectKeyId
	 * @param encryptionKey
	 * @return ASN.1 encoded representation of CMS enveloped data
	 * @throws CMSException
	 * @throws IOException
	 */
	public static final byte[] encrypt(byte[] data, byte[] subjectKeyId, PublicKey encryptionKey) throws CMSException, IOException {
		CMSTypedData typedData = new CMSProcessableByteArray(data);

		CMSEnvelopedDataGenerator envelopedDataGenerator = new CMSEnvelopedDataGenerator();
		envelopedDataGenerator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(subjectKeyId, encryptionKey));

		OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC).build();
		CMSEnvelopedData envelopedData = envelopedDataGenerator.generate(typedData, encryptor);

		return envelopedData.getEncoded();
	}

	/**
	 * Decrypt ASN.1 encoded CMS enveloped-data
	 *
	 * @param data
	 * @param subjectKeyId
	 * @param decryptionKey
	 * @return content of CMS enveloped-data
	 * @throws CMSException
	 */
	public static final byte[] decrypt(byte[] data, byte[] subjectKeyId, PrivateKey decryptionKey) throws CMSException {
		CMSEnvelopedData envelopedData = new CMSEnvelopedData(data);
		RecipientInformationStore recipientInfos = envelopedData.getRecipientInfos();

		RecipientInformation recipientInfo = recipientInfos.get(new KeyTransRecipientId(subjectKeyId));
		if (recipientInfo == null) throw new CMSException("There are no match recipient info");

		return recipientInfo.getContent(new JceKeyTransEnvelopedRecipient(decryptionKey));
	}
}
