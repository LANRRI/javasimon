package org.javasimon;

import java.io.Serializable;

/**
 * Sample contains all relevant values of the Simon that are obtained by the
 * {@link org.javasimon.Simon#sample()} and {@link org.javasimon.Simon#sampleIncrement(Object)} methods.
 * Returned object contains consistent set of Simon values as both operations are synchronized.
 * Sample generally doesn't have any behavior.
 *
 * @author <a href="mailto:virgo47@gmail.com">Richard "Virgo" Richter</a>
 */
public interface Sample extends Serializable {

	/**
	 * Name of the sampled Simon.
	 *
	 * @return Simon's name
	 */
	String getName();

	/**
	 * Note from the sampled Simon.
	 *
	 * @return Simon's note
	 */
	String getNote();

	/**
	 * Timestamp of the first usage from the sampled Simon.
	 *
	 * @return Simon's first usage timestamp
	 */
	long getFirstUsage();

	/**
	 * Timestamp of the last usage from the sampled Simon.
	 *
	 * @return Simon's last usage timestamp
	 */
	long getLastUsage();
}
