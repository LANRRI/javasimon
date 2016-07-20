package org.javasimon;

import org.javasimon.utils.SimonUtils;

/**
 * Object holds all relevant data from {@link Stopwatch} Simon. Whenever it is important to get
 * more values in a synchronous manner, {@link org.javasimon.Stopwatch#sample()} (or {@link
 * Stopwatch#sampleIncrement(Object)} should be used to obtain this object.
 *
 * @author <a href="mailto:virgo47@gmail.com">Richard "Virgo" Richter</a>
 */
public class StopwatchSample implements Sample {

	private final SampleCommon common;
	private final long total;
	private final long counter;
	private final long min;
	private final long minTimestamp;
	private final long max;
	private final long maxTimestamp;
	private final int active;
	private final int maxActive;
	private final long maxActiveTimestamp;
	private final long lastSplit;
	private final double mean;
	private final double mean2;

	StopwatchSample(String name) {
		common = new SampleCommon(name, null, 0, 0);
		total = 0;
		counter = 0;
		min = Long.MAX_VALUE;
		minTimestamp = 0;
		max = 0;
		maxTimestamp = 0;
		active = 0;
		maxActive = 0;
		maxActiveTimestamp = 0;
		lastSplit = 0;
		mean = 0;
		mean2 = 0;
	}

	private StopwatchSample(SampleCommon common, long total, long counter, long min, long minTimestamp,
		long max, long maxTimestamp, int active, int maxActive, long maxActiveTimestamp,
		long lastSplit, double mean, double mean2)
	{
		this.common = common;
		this.total = total;
		this.counter = counter;
		this.min = min;
		this.minTimestamp = minTimestamp;
		this.max = max;
		this.maxTimestamp = maxTimestamp;
		this.active = active;
		this.maxActive = maxActive;
		this.maxActiveTimestamp = maxActiveTimestamp;
		this.lastSplit = lastSplit;
		this.mean = mean;
		this.mean2 = mean2;
	}

	private StopwatchSample addSplit(long split) {
		double delta = split - mean;
		double newMean = ((double) total) / counter;

		return new StopwatchSample(common, total + split, counter + 1,
			split < min ? split : min, split < min ? minTimestamp : common.getLastUsage(),
			split > max ? split : max, split > max ? maxTimestamp : common.getLastUsage(),
			active, maxActive, maxActiveTimestamp, split, newMean, delta * (split - newMean));
	}

	StopwatchSample activeStart(long now) {
		int newActive = active + 1;

		return new StopwatchSample(common.withLastUsage(now), total, counter, min, minTimestamp,
			max, maxTimestamp, newActive, newActive > maxActive ? newActive : maxActive,
			newActive > maxActive ? now : maxActiveTimestamp, lastSplit, mean, mean2);
	}

	StopwatchSample withNote(String note) {
		return new StopwatchSample(common.withNote(note), total, counter, min, minTimestamp,
			max, maxTimestamp, active, maxActive, maxActiveTimestamp, lastSplit, mean, mean2);
	}

	@Override public String getName() {
		return common.getName();
	}

	@Override public String getNote() {
		return common.getNote();
	}

	@Override public long getFirstUsage() {
		return common.getFirstUsage();
	}

	@Override public long getLastUsage() {
		return common.getLastUsage();
	}

	/**
	 * Returns the total sum of all split times in nanoseconds.
	 *
	 * @return total time of the stopwatch in nanoseconds
	 */
	public final long getTotal() {
		return total;
	}

	/**
	 * Returns usage count of the stopwatch. Counter is increased by {@code addTime} and
	 * {@code stop} - that means that it's updated every time the next time split is added.
	 *
	 * @return count of time splits
	 */
	public final long getCounter() {
		return counter;
	}

	/**
	 * Returns minimal time split value in nanoseconds.
	 *
	 * @return minimal time split in nanoseconds
	 */
	public final long getMin() {
		return min;
	}

	/**
	 * Returns maximal time split value in nanoseconds.
	 *
	 * @return maximal time split in nanoseconds
	 */
	public final long getMax() {
		return max;
	}

	/**
	 * Returns ms timestamp when the min value was measured.
	 *
	 * @return ms timestamp of the min value measurement
	 */
	public final long getMinTimestamp() {
		return minTimestamp;
	}

	/**
	 * Returns ms timestamp when the max value was measured.
	 *
	 * @return ms timestamp of the max value measurement
	 */
	public final long getMaxTimestamp() {
		return maxTimestamp;
	}

	/**
	 * Returns current number of measured splits (concurrently running).
	 *
	 * @return current number of active splits
	 */
	public final long getActive() {
		return active;
	}

	/**
	 * Returns peek value of active concurrent splits.
	 *
	 * @return maximum reached value of active splits
	 */
	public final long getMaxActive() {
		return maxActive;
	}

	/**
	 * Returns ms timestamp when the last peek of the active split count occurred.
	 *
	 * @return ms timestamp of the last peek of the active split count
	 */
	public final long getMaxActiveTimestamp() {
		return maxActiveTimestamp;
	}

	/**
	 * Returns the value of the last measured split in ns.
	 *
	 * @return last measured split in ns
	 */
	public final long getLastSplit() {
		return lastSplit;
	}

	/**
	 * Returns mean value (average) of all measured values.
	 *
	 * @return mean value
	 */
	public final double getMean() {
		return mean;
	}

	public double getVarianceN() {
		if (counter == 0) {
			return Double.NaN;
		}
		if (counter == 1) {
			return 0d;
		}
		return mean2 / counter;
	}

	public double getVariance() {
		if (counter == 0) {
			return Double.NaN;
		}
		if (counter == 1) {
			return 0d;
		}
		return mean2 / (counter - 1);
	}

	public double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}

	/**
	 * Returns readable representation of object.
	 *
	 * @return string with readable representation of object
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("StopwatchSample{");
		if (getName() != null) {
			sb.append("name=").append(getName()).append(", ");
		}
		sb.append("total=").append(SimonUtils.presentNanoTime(total));
		sb.append(", counter=").append(counter);
		sb.append(", max=").append(SimonUtils.presentNanoTime(max));
		sb.append(", min=").append(SimonUtils.presentNanoTime(min));
		sb.append(", maxTimestamp=").append(SimonUtils.presentTimestamp(maxTimestamp));
		sb.append(", minTimestamp=").append(SimonUtils.presentTimestamp(minTimestamp));
		sb.append(", active=").append(active);
		sb.append(", maxActive=").append(maxActive);
		sb.append(", maxActiveTimestamp=").append(SimonUtils.presentTimestamp(maxActiveTimestamp));
		sb.append(", lastSplit=").append(SimonUtils.presentNanoTime(lastSplit));
		sb.append(", mean=").append(SimonUtils.presentNanoTime((long) getMean()));
		sb.append(", standardDeviation=").append(SimonUtils.presentNanoTime((long) getStandardDeviation()));
		sb.append(", variance=").append(getVariance());
		sb.append(", varianceN=").append(getVarianceN());
		common.toStringCommon(sb);
		return sb.toString();
	}

	/** Equivalent to {@link org.javasimon.StopwatchImpl#toString()} without state. */
	public String simonToString() {
		return "Simon Stopwatch: total " + SimonUtils.presentNanoTime(total) +
			", counter " + counter +
			", max " + SimonUtils.presentNanoTime(max) +
			", min " + SimonUtils.presentNanoTime(min) +
			", mean " + SimonUtils.presentNanoTime((long) mean) +
			common.simonToStringCommon();
	}
}
