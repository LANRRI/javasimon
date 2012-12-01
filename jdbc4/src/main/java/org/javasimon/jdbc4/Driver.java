package org.javasimon.jdbc4;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simon JDBC4 Proxy Driver.
 * <p>
 * An application should not use this class directly. The application (if standalone)
 * should use {@link java.sql.DriverManager} only. For example:
 * </p>
 * <pre>
 * Connection conn = DriverManager.getConnection("jdbc:simon:oracle:thin:...", "scott", "tiger");</pre>
 *
 * Simon driver has following format of JDBC connection string:
 * <pre>{@literal
 * jdbc:simon:<real driver conn string>;<param1>=<value1>;...}</pre>
 * Simon driver recognizes two parameters:
 * <ul>
 * <li>
 * {@code SIMON_REAL_DRV} - if you don't want or can't register real driver for any
 * reason, you can use this parameter and Simon proxy driver will do the registration
 * for you. You don't need to specify real driver parameter for some well known databases.
 * Simon proxy driver recognize database by first key word after JDBC and register.
 * </li>
 * <li>
 * {@code SIMON_PREFIX} - setting this parameter you can choose different prefix
 * for all monitors for one instance of driver. For example, setting
 * {@code SIMON_PREFIX=com.foo} will ensure that all proxy related Simons are located
 * under the subtree specified by the prefix, e.g. {@code com.foo.conn}, <code>com.foo.stmt</code>,
 * <code>com.foo.select</code>, etc. If no prefix is set, default {@code org.javasimon.jdbc} prefix
 * is used.
 * </li>
 * </ul>                                `
 *
 * By default, there is no need to load any driver explicitly, because drivers are loaded automatically
 * (since JDK 1.5) if they are in class path and jar have appropriate
 * meta information (see {@link java.sql.DriverManager}).
 *
 * If this is not a case for any reason, you need to register Simon proxy driver at least.
 * For real driver Simon proxy driver contains following procedure for find and register it:
 * <ol>
 * <li>Simon proxy driver tries if there is registered driver for driver key word.
 * <li>If not, driver tries if there is real driver parameter in info properties and then registers it.
 * <li>If not, driver tries to find driver by key word within internal list of well known drivers and
 * then registers it. For now, list contains default drivers for Oracle, PostgreSQL, Enterprise DB, H2,
 * MySQL.
 * <li>If not, driver tries to find real driver param within connection string and then registers it.
 * <li>If not, getting new connection fails.
 * </ol>
 * The safest way to get Simon proxy driver work is to load the drivers, the real one (i.e. oracle)
 * and a Simon proxy driver explicitly. This can be done using Class.forName. To load the driver and open a
 * database connection, use following code:
 * <pre>
 * Class.forName("oracle.jdbc.driver.OracleDriver");  // loads real driver
 * Class.forName("org.javasimon.jdbc4.Driver");  // loads Simon proxy driver
 * Connection conn = DriverManager.getConnection(
 *      "jdbc:simon:oracle:thin:...", "scott", "tiger");</pre>
 *
 * @author Radovan Sninsky
 * @author <a href="mailto:virgo47@gmail.com">Richard "Virgo" Richter</a>
 * @see java.sql.DriverManager#getConnection(String)
 * @since 2.4
 */
public class Driver implements java.sql.Driver {

	/**
	 * Default hierarchy prefix for Simon JDBC driver. All Simons created by Simon JDBC
	 * driver without explicitly specified prefix are started with default prefix.
	 */
	public static final String DEFAULT_PREFIX = "org.javasimon.jdbc";

    /**
     * Prefix used for URLs
     */
    public static final String URL_PREFIX = "jdbc:simon:";

    static {
		try {
			DriverManager.registerDriver(new Driver());
		} catch (Exception e) {
			// don't know what to do yet, maybe throw RuntimeException ???
			e.printStackTrace();
		}
	}

	private final Properties drivers = new Properties();

	/**
	 * Class constructor. It loads well known driver list from resource file drivers.properties.
	 */
	public Driver() {
		try {
			InputStream stream = null;
			try {
				// TODO: limited to known drivers, better find driver later based on JDBC URL without "simon" word
				stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/javasimon/jdbc4/drivers.properties");
				drivers.load(stream);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (IOException e) {
			// log somewhere
		}
	}

	/**
	 * Opens new Simon proxy driver connection associated with real connection to the specified database.
	 *
	 * @param simonUrl JDBC connection string (i.e. jdbc:simon:h2:file:test)
	 * @param info properties for connection
	 * @return open connection to database or null if provided url is not accepted by this driver
	 * @throws java.sql.SQLException if there is no real driver registered/recognized or opening real connection fails
	 * @see org.javasimon.jdbc4.Driver
	 */
	@Override
	public Connection connect(String simonUrl, Properties info) throws SQLException {
		if (!acceptsURL(simonUrl)) {
			return null;
		}

        DriverUrl url = new DriverUrl(getUrlPrefix(), simonUrl, info);
		java.sql.Driver realDriver = getRealDriver(url);
        java.sql.Connection realConnection=realDriver.connect(url.getRealUrl(), url.getRealProperties());
		return wrapConnection(realConnection, url);
	}
    protected Connection wrapConnection(Connection realConnection, DriverUrl url) {
        return new SimonConnection(realConnection, url.getPrefix());
    }
	/**
	 * Tries to determine driver class, instantiate it and register if already not registered.
	 * For more detail look at {@link org.javasimon.jdbc4.Driver} class javadoc.
	 *
	 * @param url instance of url object that represents url
	 * @return instance of real driver
	 * @throws java.sql.SQLException if real driver can't be determined or is not registerd
	 */
	private java.sql.Driver getRealDriver(DriverUrl url) throws SQLException {
		java.sql.Driver drv = null;
		try {
			drv = DriverManager.getDriver(url.getRealUrl());
		} catch (SQLException e) {
			// nothing, not an error
		}
        String realDriver=url.getRealDriver();
		if (drv == null && realDriver!=null) {
			drv = registerDriver(realDriver);
		}

		if (drv == null && url.getDriverId() != null) {
			drv = registerDriver(drivers.getProperty(url.getDriverId()));
		}

		if (drv == null) {
			if (url.getRealDriver() != null) {
				drv = registerDriver(url.getRealDriver());
			}
		}

		if (drv == null) {
			throw new SQLException("Real driver is not registered and can't determine real driver class name for registration.");
		}
		return drv;
	}

	/**
	 * Registers real driver through {@link java.sql.DriverManager}.
	 *
	 * @param name real driver class name
	 * @return instance of registered real driver
	 * @throws java.sql.SQLException if registration fails
	 */
	protected static final java.sql.Driver registerDriver(String name) throws SQLException {
		try {
			java.sql.Driver d = (java.sql.Driver) Class.forName(name).newInstance();
			DriverManager.registerDriver(d);
			return d;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url != null && url.toLowerCase().startsWith(getUrlPrefix());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
		return new DriverPropertyInfo[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMajorVersion() {
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinorVersion() {
		return 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean jdbcCompliant() {
		return true;
	}

    public String getUrlPrefix() {
        return URL_PREFIX;
    }
}
