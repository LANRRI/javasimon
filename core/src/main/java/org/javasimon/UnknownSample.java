package org.javasimon;

/**
 * Sample produced by {@link UnknownSimon}s.
 *
 * @author gquintana
 * @since 3.2
 */
public class UnknownSample extends SampleCommon implements Sample {

	UnknownSample(String name, String note, long now) {
		super(name, note, now, now);
	}

	@Override UnknownSample withNote(String note) {
		return new UnknownSample(getName(), note, getLastUsage());
	}

	/**
	 * Returns readable representation of the sample object.
	 *
	 * @return string with readable representation of the sample
	 */
	@Override
	public String toString() {
		return "UnknownSample" + "{name=" + getName() + ", note=" + getNote() + '}';
	}
}
