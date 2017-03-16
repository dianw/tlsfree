package org.tlsfree.certificate;

@SuppressWarnings("serial")
public class CertificateException extends RuntimeException {
	public CertificateException() {
		super();
	}

	public CertificateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CertificateException(String message, Throwable cause) {
		super(message, cause);
	}

	public CertificateException(String message) {
		super(message);
	}

	public CertificateException(Throwable cause) {
		super(cause);
	}
}
