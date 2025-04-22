package org.codeturnery.zip;

import java.time.Instant;
import java.util.Objects;

@SuppressWarnings("javadoc") // TODO: add documentation
public class TimestampRange {

	private Instant minimumZipModifiedTime;
	private Instant minimumNtfsCreationTime;
	private Instant minimumNtfsModifiedTime;
	private Instant minimumNtfsAccessTime;
	private Instant maximumZipModifiedTime;
	private Instant maximumNtfsCreationTime;
	private Instant maximumNtfsModifiedTime;
	private Instant maximumNtfsAccessTime;

	public TimestampRange(final Timestamp initialTimestamp) {
		final Instant zipModifiedTime = Objects.requireNonNull(initialTimestamp.getZipModifiedTime());
		final Instant ntfsCreationTime = Objects.requireNonNull(initialTimestamp.getNtfsCreationTime());
		final Instant ntfsModifiedTime = Objects.requireNonNull(initialTimestamp.getNtfsModifiedTime());
		final Instant ntfsAccessTime = Objects.requireNonNull(initialTimestamp.getNtfsAccessTime());
		this.minimumZipModifiedTime = zipModifiedTime;
		this.minimumNtfsCreationTime = ntfsCreationTime;
		this.minimumNtfsModifiedTime = ntfsModifiedTime;
		this.minimumNtfsAccessTime = ntfsAccessTime;
		this.maximumZipModifiedTime = zipModifiedTime;
		this.maximumNtfsCreationTime = ntfsCreationTime;
		this.maximumNtfsModifiedTime = ntfsModifiedTime;
		this.maximumNtfsAccessTime = ntfsAccessTime;
	}

	public void update(final Timestamp timestamp) {
		final Instant zipModifiedTime = Objects.requireNonNull(timestamp.getZipModifiedTime());
		final Instant ntfsCreationTime = Objects.requireNonNull(timestamp.getNtfsCreationTime());
		final Instant ntfsModifiedTime = Objects.requireNonNull(timestamp.getNtfsModifiedTime());
		final Instant ntfsAccessTime = Objects.requireNonNull(timestamp.getNtfsAccessTime());
		this.minimumZipModifiedTime = getMin(this.minimumZipModifiedTime, zipModifiedTime);
		this.minimumNtfsCreationTime = getMin(this.minimumNtfsCreationTime, ntfsCreationTime);
		this.minimumNtfsModifiedTime = getMin(this.minimumNtfsModifiedTime, ntfsModifiedTime);
		this.minimumNtfsAccessTime = getMin(this.minimumNtfsAccessTime, ntfsAccessTime);
		this.maximumZipModifiedTime = getMax(this.maximumZipModifiedTime, zipModifiedTime);
		this.maximumNtfsCreationTime = getMax(this.maximumNtfsCreationTime, ntfsCreationTime);
		this.maximumNtfsModifiedTime = getMax(this.maximumNtfsModifiedTime, ntfsModifiedTime);
		this.maximumNtfsAccessTime = getMax(this.maximumNtfsAccessTime, ntfsAccessTime);
	}

	public Timestamp getMinimum() {
		return new Timestamp() {
			@Override public Instant getZipModifiedTime() { return TimestampRange.this.minimumZipModifiedTime; }
			@Override public Instant getNtfsModifiedTime() { return TimestampRange.this.minimumNtfsModifiedTime; }
			@Override public Instant getNtfsCreationTime() { return TimestampRange.this.minimumNtfsCreationTime; }
			@Override public Instant getNtfsAccessTime() { return TimestampRange.this.minimumNtfsAccessTime; }
		};
	}
	
	public Timestamp getMaximum() {
		return new Timestamp() {
			@Override public Instant getZipModifiedTime() { return TimestampRange.this.maximumZipModifiedTime; }
			@Override public Instant getNtfsModifiedTime() { return TimestampRange.this.maximumNtfsModifiedTime; }
			@Override public Instant getNtfsCreationTime() { return TimestampRange.this.maximumNtfsCreationTime; }
			@Override public Instant getNtfsAccessTime() { return TimestampRange.this.maximumNtfsAccessTime; }
		};
	}

	private static Instant getMax(final Instant a, final Instant b) {
		if (a.compareTo(b) > 0) {
			return a;
		}
		return b;
	}

	private static Instant getMin(final Instant a, final Instant b) {
		if (a.compareTo(b) < 0) {
			return a;
		}
		return b;
	}
}
