package org.codeturnery.tree;

import java.util.List;
import java.util.Optional;

import org.codeturnery.typesystem.Optionals;

public class TestNode extends AbstractChildableNode<TestNode> {
	private final String name;
	private final Optional<List<TestNode>> children;

	public TestNode(final String name) {
		this.name = name;
		this.children = Optionals.empty();
	}

	public TestNode(final String name, List<TestNode> children) {
		this.name = name;
		this.children = Optionals.of(children);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public Optional<List<TestNode>> getChildren() {
		return this.children;
	}
}
