package org.tlsfree.blob.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.tlsfree.blob.BlobStore;
import org.tlsfree.blob.BlobStoreRepository;
import org.tlsfree.blob.BlobStoreService;

@Service
public class DefaultBlobStoreService implements BlobStoreService {
	@Inject
	private BlobStoreRepository blobStoreRepository;

	@Override
	public BlobStore getBlobById(Long blobStoreId) {
		return blobStoreRepository.findOne(blobStoreId);
	}

	@Override
	public BlobStore saveBlob(BlobStore blobStore) {
		return blobStoreRepository.save(blobStore);
	}

}
