package org.enkrip.account;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.codenergic.theskeleton.core.data.RestData;

@AutoValue
@JsonDeserialize(builder = AutoValue_UserAccountRestData.Builder.class)
public abstract class UserAccountRestData implements RestData {
	public static Builder builder() {
		return new AutoValue_UserAccountRestData.Builder();
	}

	public static Builder builder(UserAccountEntity userAccount, List<UserAccountContactEntity> contacts) {
		Builder builder = builder()
			.accountId(userAccount.getId())
			.acmeAccountLocation(userAccount.getAccountLocation())
			.publicKey(Base64.encodeBase64String(userAccount.getAccountPublicKey()))
			.publicKeySha1Hash(DigestUtils.sha1Hex(userAccount.getAccountPublicKey()));
		for (UserAccountContactEntity contact : contacts) {
			builder.contactsBuilder().add(contact.getEmail());
		}
		return builder;
	}

	public abstract String getAccountId();

	@Nullable
	public abstract String getAcmeAccountLocation();

	public abstract ImmutableSet<String> getContacts();

	@Nullable
	public abstract String getPublicKey();

	@Nullable
	public abstract String getPublicKeySha1Hash();

	@AutoValue.Builder
	public interface Builder extends RestData.Builder {
		Builder accountId(String accountId);

		Builder acmeAccountLocation(String getAcmeAccountLocation);

		UserAccountRestData build();

		Builder contacts(Set<String> contacts);

		ImmutableSet.Builder<String> contactsBuilder();

		Builder publicKey(String getPublicKey);

		Builder publicKeySha1Hash(String getPublicKeySha1Hash);
	}
}
