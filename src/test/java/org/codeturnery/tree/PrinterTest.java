package org.codeturnery.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codeturnery.typesystem.Iterables;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"null", "javadoc"})
public class PrinterTest {
	private static final String OUTPUT_A = "• M0-1/3, 2×DIRECTORY | Stats for children: (ARCHIVE:⟦1,1⟧) (DIRECTORY:⟦1,1⟧); 4 children in the 2 non-leaf nodes were merged as follows:\n"
			+ "  • M1-1/2, 2×ARCHIVE\n"
			+ "  • M1-2/2, 2×DIRECTORY | Stats for children: (IMAGE:⟦1,1⟧) (TEXT:⟦1,1⟧); 4 children in the 2 non-leaf nodes were merged as follows:\n"
			+ "    • M2-1/1, 2×TEXT, 2×IMAGE\n"
			+ "• M0-2/3, 3×DIRECTORY | Stats for children: (ARCHIVE:⟦1,1⟧) (DIRECTORY:⟦1,1⟧); 6 children in the 3 non-leaf nodes were merged as follows:\n"
			+ "  • M1-1/2, 3×ARCHIVE\n"
			+ "  • M1-2/2, 3×DIRECTORY | Stats for children: (TEXT:⟦1,1⟧); 3 children in the 3 non-leaf nodes were merged as follows:\n"
			+ "    • M2-1/1, 3×TEXT\n"
			+ "• M0-3/3, 4×DIRECTORY | Stats for children: (ARCHIVE:⟦1,1⟧) (DIRECTORY:⟦1,1⟧); 8 children in the 4 non-leaf nodes were merged as follows:\n"
			+ "  • M1-1/2, 4×ARCHIVE\n"
			+ "  • M1-2/2, 4×DIRECTORY | Stats for children: (IMAGE:⟦2,5⟧,x̄=14÷4=3.5) (TEXT:⟦1,1⟧); 18 children in the 4 non-leaf nodes were merged as follows:\n"
			+ "    • M2-1/1, 4×TEXT, 14×IMAGE\n";
	private static final String OUTPUT_A2 = "dir E\n"
			+ "dir EA\n"
			+ "dir F\n"
			+ "dir FA\n"
			+ "dir G\n"
			+ "dir GA\n"
			+ "dir H\n"
			+ "dir HA\n";

	@Test
	public void testPrint() {

		final var config = new Config();
		final var grouper = new Grouper<>(config.getPredicates());
		final var hasher = new GroupPredicateHasher<>(grouper, config.getHashApproaches());
		final var merger = new Merger<>(hasher);

		final var rootNode = getNoneOneMultipleTestTree();

		final List<NodeMerge<TestNode>> topMerges = merger
				.separateAndCreateMerges(rootNode.getChildren().orElseThrow()).sorted().toList();

		final var statisticsCalculator = new StatisticsCalculator<>(grouper);
		final var outputBuilder = new OutputBuilder(config.getPredicateNaming(), statisticsCalculator, grouper);
		for (int i = 0; i < topMerges.size(); i++) {
			outputBuilder.addMerge(topMerges.get(i), i + 1, topMerges.size());
		}

		assertEquals(OUTPUT_A, outputBuilder.build());
		
		// get only those hashes for which at least three non-leaves were found
		final Map<Integer, List<ChildableNode<TestNode>>> hashToNodeMapping = hasher.getHashToNodeMapping(Iterables.setOf(
				entry -> entry.getValue().size() >= 4,
				entry -> entry.getValue().stream().anyMatch(node -> node.getChildrenStream().isPresent())
		));

		final var builder = new StringBuffer();
		final var nodes = new ArrayList<ChildableNode<TestNode>>();
		for (final List<ChildableNode<TestNode>> nodeList : hashToNodeMapping.values()) {
			for (final ChildableNode<TestNode> node : nodeList) {
				nodes.add(node);
			}
		}
		
		nodes.stream().map(ChildableNode::toString).sorted().forEach(s -> builder.append(s + '\n'));
		
		assertEquals(OUTPUT_A2, builder.toString());
	}

	/**
	 * TODO: currently unused, add test case using this data
	 */
	protected TestNode getTestTreeDiverse() {
		return new TestNode("root", List.of(
			new TestNode("dir A", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir A", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir B", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir BB", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir C", List.of(
				new TestNode("zip A.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir D", List.of(
				new TestNode("zip A.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir E", List.of(
				new TestNode("zip A.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png")
				))
			)),
			new TestNode("dir E", List.of(
				new TestNode("zip A.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png")
				))
			)),
			new TestNode("dir E", List.of(
				new TestNode("zip A.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.foo")
				))
			)),
			new TestNode("dir E", List.of(
				new TestNode("zip A.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.foo")
				))
			))
		));
	}
	
	/**
	 * Tree to test if {@link HashApproach#DIFFERENCIATE_NONE_ONE_MULTIPLE} works correctly.
	 * @return
	 */
	protected TestNode getNoneOneMultipleTestTree() {
		return new TestNode("root", List.of(
			new TestNode("dir A", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir AA", List.of(
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir B1", List.of(
					new TestNode("zip.zip"),
					new TestNode("dir BA", List.of(
						new TestNode("text.txt")
					))
				)),
			new TestNode("dir B2", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir BA", List.of(
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir C", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir CA", List.of(
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir D", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir DA", List.of(
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir E", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir EA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir F", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir FA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir G", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir GA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			)),
			new TestNode("dir H", List.of(
				new TestNode("zip.zip"),
				new TestNode("dir HA", List.of(
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("image.png"),
					new TestNode("text.txt")
				))
			))
		));
	}
}
