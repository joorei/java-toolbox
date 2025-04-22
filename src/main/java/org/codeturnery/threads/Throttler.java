package org.codeturnery.threads;

import java.time.Duration;
import java.util.PrimitiveIterator.OfDouble;
import java.util.function.DoubleSupplier;
import java.util.stream.DoubleStream;

import org.codeturnery.typesystem.NonNegative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Puts the thread it is called from to sleep for some time.
 * <p>
 * The class attempts to provide some flexibility to adjust the duration of
 * upcoming sleep calls, without becoming too complicated.
 * <p>
 * Basically when initializing an instance, you start with a default duration to
 * be used. You also set an absolute minimal and maximal duration, that will be
 * adhered to for any upcoming adjustments of the default duration.
 * <p>
 * You can then use the different {@link #sleep} methods or call
 * {@link #setAdjustment} in-between to adjust the duration of the next sleep
 * call or calls.
 * <p>
 * A usage example is to reduce the stress put on a server when sending many
 * requests. After each request, the time the request took can be used as base
 * duration for the next sleep circle. Depending on additional information, e.g.
 * a 429 ("Too Many Requests") response status code, an increased adjustment
 * factor can be set to be applied to the base duration.
 * <p>
 * TODO: reduce method calls in this class by having methods accept
 * {@link Duration} instances, but convert them into long millisecond values as
 * soon as possible.
 */
public class Throttler {
	private static final Logger LOGGER = LoggerFactory.getLogger(Throttler.class);
	private static final @NonNegative double DEFAULT_ADJUSTMENT_FACTOR = 1.0;
	private final @NonNegative Duration minDuration;
	private final @NonNegative Duration maxDuration;
	private final @NonNegative Duration defaultDuration;
	private @NonNegative DoubleSupplier adjustment;

	/**
	 * @param defaultDuration The unadjusted duration to sleep when {@link #sleep}
	 *                        is called.
	 * @param minDuration
	 * @param maxDuration
	 * @throws IllegalArgumentException if duration is non-positive.
	 */
	public Throttler(final @NonNegative Duration defaultDuration, final @NonNegative Duration minDuration,
			final @NonNegative Duration maxDuration) {
		this(defaultDuration, minDuration, maxDuration, () -> DEFAULT_ADJUSTMENT_FACTOR);
	}

	/**
	 * @param defaultDuration The unadjusted duration to sleep when {@link #sleep}
	 *                        is called.
	 * @param minDuration
	 * @param maxDuration
	 * @param adjustment      The factor with which the sleep duration is to be
	 *                        adjusted with.
	 * @throws IllegalArgumentException if duration is non-positive.
	 */
	public Throttler(final @NonNegative Duration defaultDuration, final @NonNegative Duration minDuration,
			final @NonNegative Duration maxDuration, final @NonNegative DoubleSupplier adjustment) {
		if (defaultDuration.isNegative() || defaultDuration.isZero()) {
			throw new IllegalArgumentException("Duration must be a positive value.");
		}
		if (minDuration.isNegative()) {
			throw new IllegalArgumentException("minDuration must be a non-negative value.");
		}
		if (maxDuration.isNegative() || maxDuration.isZero()) {
			throw new IllegalArgumentException("maxDuration must be a positive value.");
		}
		this.defaultDuration = defaultDuration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.adjustment = adjustment;
	}

	/**
	 * Adjusts the sleep durations for the next cycles with a fixed factor.
	 * <p>
	 * Overwrites any previously set adjustments.
	 * <p>
	 * After the defined circles, the factor will fall back to the default
	 * {@value #DEFAULT_ADJUSTMENT_FACTOR}.
	 *
	 * @param factor  The factor to adjust the sleep duration by.
	 * @param circles The number of cycles to apply the adjustment to.
	 * @throws IllegalArgumentException if the factor is non-positive or if circles
	 *                                  is non-positive.
	 */
	public void setAdjustment(@NonNegative double factor, @NonNegative int circles) {
		if (factor < 0) {
			throw new IllegalArgumentException("Factor must be non-negative.");
		}
		if (circles < 0) {
			throw new IllegalArgumentException("Number of cycles must be non-negative.");
		}
		setAdjustment(DoubleStream.generate(() -> factor).limit(circles));
	}

	/**
	 * Adjust the sleep duration for the next cycle only.
	 * <p>
	 * Overwrites any previously set adjustments.
	 * <p>
	 * After this one circle, the factor will fall back to the default
	 * {@value #DEFAULT_ADJUSTMENT_FACTOR}.
	 *
	 * @param factor The factor to adjust the sleep duration by.
	 */
	public void setAdjustment(final @NonNegative double factor) {
		setAdjustment(factor, 1);
	}

	/**
	 * Apply the given factors for the next {@link #sleep} calls.
	 * <p>
	 * Overwrites any previously set adjustments.
	 * <p>
	 * After the stream runs empty, the factor will fall back to the default
	 * {@value #DEFAULT_ADJUSTMENT_FACTOR}.
	 * 
	 * @param adjustments Will be used on each sleep to get the next factor with
	 *                    which the duration is to be adjusted.
	 * @throws IllegalArgumentException if the factors array is null, empty, or
	 *                                  contains non-positive values.
	 */
	public void setAdjustment(final @NonNegative DoubleStream adjustments) {
		setAdjustment(adjustments.iterator());
	}

	/**
	 * Apply the given factors for the next {@link #sleep} call or calls.
	 * <p>
	 * Overwrites any previously set adjustments.
	 * <p>
	 * After the iterator runs empty, the factor will fall back to the default
	 * {@value #DEFAULT_ADJUSTMENT_FACTOR}.
	 * 
	 * @param adjustments Will be used on each sleep to get the next factor with
	 *                    which the duration is to be adjusted.
	 * @throws IllegalArgumentException if the factors array is null, empty, or
	 *                                  contains non-positive values.
	 */
	public void setAdjustment(final @NonNegative OfDouble adjustments) {
		this.adjustment = () -> {
			if (adjustments.hasNext()) {
				return adjustments.nextDouble();
			}
			return DEFAULT_ADJUSTMENT_FACTOR;
		};
	}

	/**
	 * Puts the thread to sleep for a duration adjusted by the current factor.
	 * 
	 * @return
	 * 
	 * @throws InterruptedException     if the thread is interrupted while sleeping.
	 * @throws IllegalArgumentException if the adjustments array is invalid or
	 *                                  exhausted.
	 */
	public Duration sleep() throws InterruptedException {
		final long adjustedDuration = (long) (this.defaultDuration.toMillis() * getAdjustmentFactor());
		return sleepBoundedDuration(Duration.ofMillis(adjustedDuration));
	}

	/**
	 * Will apply the adjustment factor and bounds of this instance, but uses the
	 * given duration as base instead of the default one set on instantiation.
	 * 
	 * @param duration
	 * @throws InterruptedException
	 */
	public Duration sleep(final @NonNegative Duration duration) throws InterruptedException {
		final long adjustedDuration = (long) (duration.toMillis() * getAdjustmentFactor());
		return sleepBoundedDuration(Duration.ofMillis(adjustedDuration));
	}

	/**
	 * Will sleep the exact time set as minimal duration during instantiation
	 * without any adjustment via factor or bounds.
	 * 
	 * @return
	 * 
	 * @throws InterruptedException
	 */
	public Duration sleepExactMinDuration() throws InterruptedException {
		return sleepBoundedDuration(this.minDuration);
	}

	/**
	 * Will sleep the exact time set as maximum duration during instantiation
	 * without any adjustment via factor or bounds.
	 * 
	 * @return
	 * 
	 * @throws InterruptedException
	 */
	public Duration sleepExactMaxDuration() throws InterruptedException {
		return sleepBoundedDuration(this.maxDuration);
	}

	/**
	 * @return The current minimal duration of a sleep call.
	 */
	public @NonNegative Duration getMinDuration() {
		return this.minDuration;
	}

	/**
	 * @return The current maximal duration of a sleep call.
	 */
	public @NonNegative Duration getMaxDuration() {
		return this.maxDuration;
	}

	/**
	 * Will sleep the exact given time without any adjustment via factor.
	 * <p>
	 * Will limit the time within the bounds defined by this instance.
	 * 
	 * @param duration
	 * @return
	 * 
	 * @throws InterruptedException
	 */
	protected Duration sleepBoundedDuration(final @NonNegative Duration duration) throws InterruptedException {
		final long durationMs = putInBounds(duration.toMillis());
		LOGGER.debug("Sleeping " + durationMs + " ms.");
		Thread.sleep(durationMs);
		return Duration.ofMillis(durationMs);
	}

	protected @NonNegative long putInBounds(final @NonNegative long duration) {
		return Math.min(Math.max(duration, this.minDuration.toMillis()), this.maxDuration.toMillis());
	}

	protected @NonNegative double getAdjustmentFactor() {
		final double adjustmentValue = this.adjustment.getAsDouble();
		if (adjustmentValue < 0) {
			throw new IllegalArgumentException("Factor must be non-negative. Was " + adjustmentValue + ".");
		}
		return adjustmentValue;
	}
}
