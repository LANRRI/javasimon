package org.javasimon.jdbc;

import org.javasimon.Split;
import org.javasimon.proxy.DelegatingMethodInvocation;

import java.sql.*;

/**
 * JDBC Proxy handler for {@link Connection}
 */
public class ConnectionProxyHandler extends JdbcProxyHandler<Connection> {
    /**
     * Main constructor
     *
     * @param delegate Wrapped connection
     */
    public ConnectionProxyHandler(Connection delegate, String connectionFactoryName, JdbcProxyFactory proxyFactory, Split lifeSplit) {
        super(delegate, Connection.class, connectionFactoryName, proxyFactory, lifeSplit);
    }

    @Override
    protected Object invoke(DelegatingMethodInvocation<Connection> delegatingMethodInvocation) throws Throwable {
        final String methodName=delegatingMethodInvocation.getMethodName();
        Object result;
        if (methodName.equals("isWrapperFor")) {
            result=isWrapperFor(delegatingMethodInvocation);
        } else if (methodName.equals("unwrap")) {
            result=unwrap(delegatingMethodInvocation);
        } else if (methodName.equals("close")) {
            result=close(delegatingMethodInvocation);
        } else if (methodName.equals("createStatement")) {
            result=createStatement(delegatingMethodInvocation);
        } else if (methodName.equals("prepareStatement")) {
            result=prepareStatement(delegatingMethodInvocation);
        } else if (methodName.equals("prepareCall")) {
            result=prepareCall(delegatingMethodInvocation);
        } else {
            result=delegatingMethodInvocation.proceed();
        }
        return result;
    }
    private Statement createStatement(DelegatingMethodInvocation<Connection> methodInvocation) throws Throwable {
        Statement result=(Statement) methodInvocation.proceed();
        result= proxyFactory.wrapStatement(name, result);
        return result;
    }
    private PreparedStatement prepareStatement(DelegatingMethodInvocation<Connection> methodInvocation) throws Throwable {
        PreparedStatement result=(PreparedStatement) methodInvocation.proceed();
        result= proxyFactory.wrapPreparedStatement(name, result, methodInvocation.getArgAt(0, String.class));
        return result;
    }
    private CallableStatement prepareCall(DelegatingMethodInvocation<Connection> methodInvocation) throws Throwable {
        CallableStatement result=(CallableStatement) methodInvocation.proceed();
        result= proxyFactory.wrapCallableStatement(name, result, methodInvocation.getArgAt(0, String.class));
        return result;
    }
}
