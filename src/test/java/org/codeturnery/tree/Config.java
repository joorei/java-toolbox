package org.codeturnery.tree;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("javadoc")
public class Config {

	private final Predicate<TestNode> archive = new TestNodeMatcher(".zip");
	private final Predicate<TestNode> image = new TestNodeMatcher(".jpeg", ".jpg", ".png", ".PNG", ".JPEG", ".JPG");
	private final Predicate<TestNode> text = new TestNodeMatcher(".txt");
	private final Predicate<TestNode> dir = n -> n.getChildren().isPresent();
	private final Predicate<TestNode> other = n -> true;
	

	public Map<Predicate<TestNode>, HashApproach> getHashApproaches() {
		final var map = new HashMap<Predicate<TestNode>, HashApproach>();
		map.put(this.archive, HashApproach.EXACT_COUNT);
		map.put(this.image, HashApproach.DIFFERENCIATE_NONE_ONE_MULTIPLE);
		map.put(this.text, HashApproach.EXACT_COUNT);
		map.put(this.dir, HashApproach.EXACT_COUNT);
		map.put(this.other, HashApproach.EXACT_COUNT);
		return map;
	}

	public Map<Predicate<TestNode>, String> getPredicateNaming() {
		final var matchers = new LinkedHashMap<Predicate<TestNode>, String>();
		matchers.put(this.archive, "ARCHIVE");
		matchers.put(this.image, "IMAGE");
		matchers.put(this.text, "TEXT");
		matchers.put(this.dir, "DIRECTORY");
		matchers.put(this.other, "OTHER");

		return matchers;
	}

	public Set<Predicate<TestNode>> getPredicates() {
		return getPredicateNaming().keySet();
	}
}
