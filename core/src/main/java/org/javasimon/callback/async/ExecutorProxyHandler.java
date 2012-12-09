package org.javasimon.callback.async;

import org.javasimon.proxy.DelegatingMethodInvocation;
import org.javasimon.proxy.DelegatingProxyHandler;

/**
 * Proxy factory which can be used make any class asynchronous.
 * @author gerald
 * @param <T> 
 */
public class ExecutorProxyHandler<T> extends DelegatingProxyHandler<T> {
	/**
	 * Executor used for invoking methods on delegate object
	 */
	private Executor executor;
	/**
	 * Constructor
	 * @param delegate Delegate object
	 */
	public ExecutorProxyHandler(T delegate) {
		this(delegate, Executors.async());
	}

	/**
	 * Constructor
	 * @param delegate Delegate object
	 * @param executor Executor used, see {@link Executors}
	 */
	public ExecutorProxyHandler(T delegate, Executor executor) {
		super(delegate);
		this.executor = executor;
	}
	/**
	 * Get used executor
	 * @return Executor
	 */
	public Executor getExecutor() {
		return executor;
	}
	/**
	 * Set used executor
	 * @param executor Executor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	@Override
	protected Object invoke(DelegatingMethodInvocation<T> delegatingMethodInvocation) throws Throwable {
		return executor.execute(delegatingMethodInvocation);
	}
}
