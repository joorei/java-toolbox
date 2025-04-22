package org.codeturnery.zip;

import java.time.Instant;

/**
 * Provides access to temporal informations available for files and directories
 * in ZIP files.
 */
public interface Timestamp {
	/**
	 * Provides the time of the last modification of the file denoted by a ZIP
	 * entry. The value is read from the header of the corresponding ZIP entry.
	 * <p>
	 * Second accuracy.
	 *
	 * @return The last time the file or folder was modified before it was archived.
	 */
	public Instant getZipModifiedTime();

	/**
	 * Provides the time of the creation of the file denoted by a ZIP entry. The
	 * value is read from the NTFS extra field of the corresponding ZIP entry
	 * header.
	 * <p>
	 * 100 nanosecond accuracy.
	 *
	 * @return The time the folder was created outside of the ZIP file.
	 */
	public Instant getNtfsCreationTime();

	/**
	 * Provides the time of the last modification of the file denoted by a ZIP
	 * entry. The value is read from the NTFS extra field of the corresponding ZIP
	 * entry header.
	 * <p>
	 * 100 nanosecond accuracy.
	 *
	 * @return The last time the file or folder was modified before it was archived.
	 */
	public Instant getNtfsModifiedTime();

	/**
	 * Provides the time of the last access of the file denoted by a ZIP entry. The
	 * value is read from the NTFS extra field of the corresponding ZIP entry
	 * header.
	 * <p>
	 * 100 nanosecond accuracy.
	 *
	 * @return The last time the file or folder was accessed before it was archived.
	 */
	public Instant getNtfsAccessTime();
}
