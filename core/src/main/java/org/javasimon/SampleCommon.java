package org.javasimon;

import org.javasimon.utils.SimonUtils;

/** Sample implementation covering common fiels from {@link AbstractSimon}. */
public class SampleCommon implements Sample {

	private final String name;
	private final String note;
	private final long firstUsage;
	private final long lastUsage;

	public SampleCommon(String name, String note, long firstUsage, long lastUsage) {
		this.name = name;
		this.note = note;
		this.firstUsage = firstUsage;
		this.lastUsage = lastUsage;
	}

	/** Creates new sample with updated note. */
	SampleCommon withNote(String note) {
		return new SampleCommon(name, note, firstUsage, lastUsage);
	}

	/** Creates new sample with updated lastUsage. */
	SampleCommon withLastUsage(long lastUsage) {
		return new SampleCommon(name, note, firstUsage == 0 ? lastUsage : firstUsage, lastUsage);
	}

	/**
	 * Name of the sampled Simon.
	 *
	 * @return Simon's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Note from the sampled Simon.
	 *
	 * @return Simon's note
	 */
	public final String getNote() {
		return note;
	}

	/**
	 * Timestamp of the first usage from the sampled Simon.
	 *
	 * @return Simon's first usage timestamp
	 */
	public long getFirstUsage() {
		return firstUsage;
	}

	/**
	 * Timestamp of the last usage from the sampled Simon.
	 *
	 * @return Simon's last usage timestamp
	 */
	public long getLastUsage() {
		return lastUsage;
	}

	// common part of the toString method
	void toStringCommon(StringBuilder sb) {
		sb.append(", firstUsage=").append(SimonUtils.presentTimestamp(firstUsage));
		sb.append(", lastUsage=").append(SimonUtils.presentTimestamp(lastUsage));
		sb.append(", note=").append(note);
		sb.append('}');
	}

	String simonToStringCommon() {
		return " [" + name +
			(note != null && note.length() != 0 ? " \"" + note + "\"]" : "]");
	}
}
