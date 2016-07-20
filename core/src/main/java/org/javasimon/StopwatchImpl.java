package org.javasimon;

import java.util.Collection;

import org.javasimon.utils.SimonUtils;

/**
 * Class implements {@link org.javasimon.Stopwatch} interface - see there for how to use Stopwatch.
 *
 * @author <a href="mailto:virgo47@gmail.com">Richard "Virgo" Richter</a>
 * @see org.javasimon.Stopwatch
 */
final class StopwatchImpl extends AbstractSimon implements Stopwatch {

	private StopwatchSample sample;

	/**
	 * Constructs Stopwatch Simon with a specified name and for the specified manager.
	 *
	 * @param name Simon's name
	 * @param manager owning manager
	 */
	StopwatchImpl(String name, Manager manager) {
		super(manager);
		new StopwatchSample(name);
	}

	@Override
	public Stopwatch addSplit(Split split) {
		if (!enabled) {
			return this;
		}

		long splitNs = split.runningFor();
		long nowNanos = nanoTimeFromSplit(split, splitNs);

		StopwatchSample sample = null;
		synchronized (this) {
			// using parameter version saves one currentTimeMillis call
			updateUsagesNanos(nowNanos);
			addSplit(splitNs);
			if (!manager.callback().callbacks().isEmpty()) {
				sample = sample();
			}
			updateIncrementalSimons(splitNs, nowNanos);
		}
		manager.callback().onStopwatchAdd(this, split, sample);
		return this;
	}

	private long nanoTimeFromSplit(Split split, long splitNs) {
		if (split.getStopwatch() != null) {
			return split.getStart() + splitNs;
		} else {
			return manager.nanoTime();
		}
	}

	private void updateIncrementalSimons(long splitNs, long nowNanos) {
		Collection<Simon> simons = incrementalSimons();
		if (simons != null) {
			for (Simon simon : simons) {
				StopwatchImpl stopwatch = (StopwatchImpl) simon;
				stopwatch.addSplit(splitNs);
				stopwatch.updateUsagesNanos(nowNanos);
			}
		}
	}

	@Override
	public Split start() {
		if (!enabled) {
			return Split.disabled(this, manager);
		}

		synchronized (this) {
			sample = sample.activeStart(manager.milliTime());
		}
		Split split = new Split(this, manager, manager.nanoTime());
		manager.callback().onStopwatchStart(split);
		return split;
	}

	/**
	 * Protected method doing the stop work based on provided start nano-time.
	 *
	 * @param split Split object that has been stopped
	 * @param nowNanos current nano time
	 * @param subSimon name of the sub-stopwatch (hierarchy delimiter is added automatically), may be {@code null}
	 */
	void stop(final Split split, final long nowNanos, final String subSimon) {
		StopwatchSample sample = null;
		synchronized (this) {
			active--;
			updateUsagesNanos(nowNanos);
			if (subSimon == null) {
				long splitNs = nowNanos - split.getStart();
				addSplit(splitNs);
				if (!manager.callback().callbacks().isEmpty()) {
					sample = sample();
				}
				updateIncrementalSimons(splitNs, nowNanos);
			}
		}
		if (subSimon != null) {
			Stopwatch effectiveStopwatch = manager.getStopwatch(getName() + Manager.HIERARCHY_DELIMITER + subSimon);
			split.setAttribute(Split.ATTR_EFFECTIVE_STOPWATCH, effectiveStopwatch);
			effectiveStopwatch.addSplit(split);
			return;
		}
		manager.callback().onStopwatchStop(split, sample);
	}
	// Uses last usage, hence it must be placed after usages update

	private long addSplit(long split) {
		last = split;
		total += split;
		counter++;
		if (split > max) {
			max = split;
			maxTimestamp = getLastUsage();
		}
		if (split < min) {
			min = split;
			minTimestamp = getLastUsage();
		}
		// statistics processing
		double delta = split - mean;
		mean = ((double) total) / counter;
		mean2 += delta * (split - mean);

		return split;
	}

	@Override
	public synchronized double getMean() {
		return sample.getMean();
	}

	@Override
	public synchronized double getVarianceN() {
		return sample.getVarianceN();
	}

	@Override
	public synchronized double getVariance() {
		return sample.getVariance();
	}

	@Override
	public synchronized double getStandardDeviation() {
		return sample.getStandardDeviation();
	}

	@Override
	public synchronized long getTotal() {
		return sample.getTotal();
	}

	@Override
	public synchronized long getLastSplit() {
		return sample.getLastSplit();
	}

	@Override
	public synchronized long getCounter() {
		return sample.getCounter();
	}

	@Override
	public synchronized long getMax() {
		return sample.getMax();
	}

	@Override
	public synchronized long getMin() {
		return sample.getMin();
	}

	@Override
	public synchronized long getMaxTimestamp() {
		return sample.getMaxTimestamp();
	}

	@Override
	public synchronized long getMinTimestamp() {
		return sample.getMinTimestamp();
	}

	@Override
	public synchronized long getActive() {
		return sample.getActive();
	}

	@Override
	public synchronized long getMaxActive() {
		return sample.getMaxActive();
	}

	@Override
	public synchronized long getMaxActiveTimestamp() {
		return sample.getMaxActiveTimestamp();
	}

	@Override public void setNote(String note) {
		sample = sample.withNote(note);
	}

	@Override
	public synchronized StopwatchSample sample() {
		return sample;
	}

	@Override
	public synchronized StopwatchSample sampleIncrement(Object key) {
		return (StopwatchSample) sampleIncrementHelper(key, new StopwatchImpl(null, manager));
	}

	@Override
	public StopwatchSample sampleIncrementNoReset(Object key) {
		return (StopwatchSample) sampleIncrementNoResetHelper(key);
	}

	/**
	 * Returns Simon basic information, total time, counter, max value and min value as a human readable string.
	 *
	 * @return basic information, total time, counter, max and min values
	 * @see AbstractSimon#toString()
	 */
	@Override
	public synchronized String toString() {
		StopwatchSample currentSample = this.sample;
		return "Simon Stopwatch: total " + SimonUtils.presentNanoTime(currentSample.getTotal()) +
			", counter " + currentSample.getCounter() +
			", max " + SimonUtils.presentNanoTime(currentSample.getMax()) +
			", min " + SimonUtils.presentNanoTime(currentSample.getMin()) +
			", mean " + SimonUtils.presentNanoTime(currentSample.getMean()) +
			super.toString();
	}
}
