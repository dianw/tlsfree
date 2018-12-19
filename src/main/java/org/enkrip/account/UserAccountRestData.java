package org.enkrip.account;

import java.util.Set;

import javax.annotation.Nullable;

import org.codenergic.theskeleton.core.data.RestData;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImmutableUserAccountRestData.Builder.class)
public interface UserAccountRestData extends RestData {
//	public static Builder builder() {
//		return new AutoValue_UserAccountRestData.Builder();
//	}

//	public static Builder builder(UserAccountEntity userAccount, List<UserAccountContactEntity> contacts) {
//		Builder builder = builder()
//			.accountId(userAccount.getId())
//			.acmeAccountLocation(userAccount.getAccountLocation())
//			.publicKey(Base64.encodeBase64String(userAccount.getAccountPublicKey()))
//			.publicKeySha1Hash(DigestUtils.sha1Hex(userAccount.getAccountPublicKey()));
//		for (UserAccountContactEntity contact : contacts) {
//			builder.contactsBuilder().add(contact.getEmail());
//		}
//		return builder;
//	}

	String getAccountId();

	@Nullable
	String getAcmeAccountLocation();

	Set<String> getContacts();

	@Nullable
	String getPublicKey();

	@Nullable
	String getPublicKeySha1Hash();

//	@AutoValue.Builder
//	public interface Builder extends RestData.Builder {
//		Builder accountId(String accountId);
//
//		Builder acmeAccountLocation(String getAcmeAccountLocation);
//
//		UserAccountRestData build();
//
//		Builder contacts(Set<String> contacts);
//
//		ImmutableSet.Builder<String> contactsBuilder();
//
//		Builder publicKey(String getPublicKey);
//
//		Builder publicKeySha1Hash(String getPublicKeySha1Hash);
//	}
}
