package org.javasimon.proxy;

/**
 * Exception thrown when proxy instantiation fails
 */
public final class ProxyException extends RuntimeException {
	public ProxyException(Throwable cause) {
		super(cause);
	}
}
