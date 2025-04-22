package org.codeturnery.characters;

import java.io.PrintStream;
import java.util.Optional;

public class ProgressPrinter {

	protected record PrintNumberConfig(int radix, int maxCount) {
	}

	private final char character;
	private final int lineLength;
	private final int skipCount;
	private Optional<PrintNumberConfig> printNumberConfig;
	private final PrintStream out;
	private int count = 0;

	/**
	 * @param lineLength
	 * @param character
	 * @param printNumberConfig if present, at the beginning of each new line the
	 *                          count will be printed
	 * @param skipCount         every {@code n}th call will be skipped. E.g. if set
	 *                          to 2 only on every 2nd call a character will be
	 *                          printed. If set to 10, only 1 out of 10 characters
	 *                          will be printed.
	 */
	protected ProgressPrinter(final PrintStream out, final int lineLength, final char character,
			final Optional<PrintNumberConfig> printNumberConfig, final int skipCount) {
		this.lineLength = lineLength * skipCount;
		this.out = out;
		this.character = character;
		this.printNumberConfig = printNumberConfig;
		this.skipCount = skipCount;
	}

	/**
	 * 
	 * @param out
	 * @param lineLength
	 * @param character
	 * @param skipCount
	 */
	public ProgressPrinter(final PrintStream out, final int lineLength, final char character, final int skipCount) {
		this(out, lineLength, character, Optional.empty(), skipCount);
	}

	/**
	 * 
	 * @param out
	 * @param lineLength
	 * @param character
	 * @param skipCount
	 * @param radix      when printing the count, use the specified radix. E.g. 10
	 *                   for decimal and 16 for hexadecimal numbers.
	 * @param maxCount   The expected max count. If the actual count becomes bigger
	 *                   the line formatting may become sub-optimal.
	 */
	public ProgressPrinter(final PrintStream out, final int lineLength, final char character, final int skipCount,
			final int radix, int maxCount) {
		this(out, lineLength, character, Optional.of(new PrintNumberConfig(radix, maxCount)), skipCount);
	}

	public void next() {
		if (this.count % this.skipCount != 0) {
			this.count++;
			return;
		}
		// Check if we are starting a new line
		if (this.count % this.lineLength == 0) {
			if (this.count != 0) {
				this.out.print("\n");
			}
			this.printNumberConfig.ifPresent(config -> {
				final String countAsString = Integer.toString(this.count, config.radix()).toUpperCase();
				final int columnWidth = Integer.toString(config.maxCount(), config.radix()).length();
				this.out.printf("%" + columnWidth + "s ", countAsString);
			});
		}
		// print the character and increment the count
		this.out.print(this.character);
		this.count++;
	}

	public void reset() {
		this.count = 0;
		this.out.println();
	}
}
