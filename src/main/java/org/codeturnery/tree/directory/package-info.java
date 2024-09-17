/**
 * Provides tools to analyze a directory to gain information regarding its
 * structure.
 * <p>
 * The content of the directory and sub-directories will be compared to find
 * similarities based on configurable criteria. Similar files and directories
 * with the same parent directory will be summarized, allowing to reduce the
 * amount of information relevant to get an overview over the structure.
 * <p>
 * This works best for directories with a homogeneous structure.
 */
@org.eclipse.jdt.annotation.NonNullByDefault
package org.codeturnery.tree.directory;