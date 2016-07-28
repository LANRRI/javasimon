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
		super(name, manager);
		sample = new StopwatchSample(name);
	}

	@Override
	public Stopwatch addSplit(Split split) {
		if (!enabled) {
			return this;
		}

		long splitNs = split.runningFor();
		long nowMillis = manager.millisForNano(split.getStart() + splitNs); //nanoTimeFromSplit(split, splitNs);

		synchronized (this) {
			// using parameter version saves one currentTimeMillis call
			addSplit(splitNs, nowMillis);
			updateIncrementalSimons(splitNs, nowMillis);
		}
		manager.callback().onStopwatchAdd(this, split, sample);
		return this;
	}

	private void addSplit(long splitNs, long now) {
		sample = sample.addSplit(splitNs, getActive() - 1, now);
	}

	private long nanoTimeFromSplit(Split split, long splitNs) {
		if (split.getStopwatch() != null) {
			return split.getStart() + splitNs;
		} else {
			return manager.nanoTime();
		}
	}

	private void updateIncrementalSimons(long splitNs, long now) {
		Collection<Simon> simons = incrementalSimons();
		if (simons != null) {
			for (Simon simon : simons) {
				StopwatchImpl stopwatch = (StopwatchImpl) simon;
				stopwatch.addSplit(splitNs, now);
			}
		}
	}

	@Override
	public Split start() {
		if (!enabled) {
			return Split.disabled(this, manager);
		}

		long nowNanos = manager.nanoTime();
		synchronized (this) {
			sample = sample.updateActive(getActive() + 1, manager.millisForNano(nowNanos));
		}
		Split split = new Split(this, manager, nowNanos);
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
		synchronized (this) {
			long now = manager.millisForNano(nowNanos);
			if (subSimon == null) {
				long splitNs = nowNanos - split.getStart();
				sample = sample.addSplit(splitNs, getActive() - 1, now);
				updateIncrementalSimons(splitNs, now);
			} else {
				sample = sample.updateActive(getActive() - 1, now);
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
	public synchronized int getActive() {
		return sample.getActive();
	}

	@Override
	public synchronized int getMaxActive() {
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
