package org.tlsfree.blob;

import javax.transaction.Transactional;

public interface BlobStoreService {
	BlobStore getBlobById(Long blobStoreId);

	@Transactional
	BlobStore saveBlob(BlobStore blobStore);
}
