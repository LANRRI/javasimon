package org.javasimon.proxy;

import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.javasimon.source.MonitorSource;
import org.javasimon.source.StopwatchTemplate;

/**
 * Stopwatch proxy factory can generate a proxy to wrap an existing class and
 * monitor how its performance.
 * Sample usage:
 * <pre>
 * MonitoredInterface monitoredProxy=proxyFactory.newProxy(new StopwatchProxyHandler(monitoredImplementation),MonitoredInterface.class);</pre>
 * or
 * <pre>
 * MonitoredInterface monitoredProxy=proxyFactory.newProxy(new StopwatchProxyFactory(monitoredImplementation, new CustomProxyStopwatchSource<MonitoredInterface>()),MonitoredInterface.class);</pre>
 */
public final class StopwatchProxyFactory<T> extends DelegatingProxyHandler<T> {
	/**
	 * Stopwatch template.
	 */
	private final StopwatchTemplate<DelegatingMethodInvocation<T>> stopwatchTemplate;

	/**
	 * Constructor
	 * @param  delegate Wrapped object
	 * @param  stopwatchSource Stopwatch source (to configure Stopwatch naming)
	 */
	public StopwatchProxyFactory(T delegate, MonitorSource<DelegatingMethodInvocation<T>, Stopwatch> stopwatchSource) {
		super(delegate);
		this.stopwatchTemplate = new StopwatchTemplate<DelegatingMethodInvocation<T>>(stopwatchSource);
	}
	/**
	 * Constructor
	 * @param  delegate Wrapped object
	 */
	public StopwatchProxyFactory(T delegate) {
		this(delegate, new ProxyStopwatchSource<T>());
	}

	/**
	 * Invocation handler main method.
	 */
	@Override
	protected Object invoke(DelegatingMethodInvocation<T> methodInvocation) throws Throwable {
		final Split split = stopwatchTemplate.start(methodInvocation);
		try {
			return methodInvocation.proceed();
		} finally {
			stopwatchTemplate.stop(split);
		}
	}
}
