package de.flapdoodle.solid.parser;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;

import de.flapdoodle.solid.parser.ImmutableTree.Builder;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public abstract class Tree {

	protected abstract ImmutableMap<String, ImmutableList<String>> relation();

	@Lazy
	protected ImmutableMap<String,String> childParentMap() {
		ImmutableMap.Builder<String,String> builder=ImmutableMap.builder();
		relation().forEach((parent, children) -> {
			children.forEach(c -> builder.put(c, parent));
		});
		return builder.build();
	}

	@Lazy
	protected ImmutableList<Node> asNodeTree() {
		ImmutableMap<String, String> childParentMap = childParentMap();
		SetView<String> roots = Sets.difference(relation().keySet(), childParentMap.keySet());
		return roots.stream()
			.map(s -> nodeOf(s, relation()))
			.collect(ImmutableList.toImmutableList());
	}

	private static Node nodeOf(String name, ImmutableMap<String, ImmutableList<String>> relation) {
		return Node.builder()
				.name(name)
				.addAllChildren(Maybe.ofNullable(relation.get(name)).orElse(ImmutableList::of)
						.stream()
						.map(c -> nodeOf(c,relation))
						.collect(ImmutableList.toImmutableList()))
				.build();
	}

	@Lazy
	protected ImmutableSet<String> knownNames() {
		return relation().keySet()
			.stream()
			.flatMap(s -> Stream.concat(Stream.of(s), relation().get(s).stream()))
			.collect(ImmutableSet.toImmutableSet());
	}

	@Auxiliary
	public ImmutableList<Node> mapAsTree(Collection<? extends String> src) {
		ImmutableList<Node> nodeTree = asNodeTree();
		ImmutableSet<String> knownNames = knownNames();

		return filter(nodeTree, knownNames, ImmutableSet.copyOf(src), 0);
	}

	@Auxiliary
	public ImmutableSet<String> allParentsOf(Collection<? extends String> src) {
		Set<String> all=Sets.newLinkedHashSet();
		src.forEach(c -> all.addAll(allParentsOf(relation(), c)));
		return ImmutableSet.copyOf(all);
	}

	private static ImmutableSet<String> allParentsOf(ImmutableMap<String,ImmutableList<String>> relations, String name) {
		ImmutableSet.Builder<String> builder=ImmutableSet.builder();

		Maybe<String> current=Maybe.of(name);
		while (current.isPresent()) {
			Maybe<String> parent = parentOf(relations, current.get());
			if (parent.isPresent()) {
				builder.add(parent.get());
			}
			current=parent;
		}
		return builder.build();
	}

	private static Maybe<String> parentOf(ImmutableMap<String,ImmutableList<String>> relations, String name) {
		for (Entry<String, ImmutableList<String>> e : relations.entrySet()) {
			if (e.getValue().contains(name)) {
				return Maybe.of(e.getKey());
			}
		}
		return Maybe.not();
	}


	private static ImmutableList<Node> filter(ImmutableList<Node> nodeTree, ImmutableSet<String> knownNames, ImmutableSet<String> src, int level) {
		ImmutableList.Builder<Node> builder = ImmutableList.builder();

		for (String current : src) {
			if (!knownNames.contains(current)) {
				if (level==0) {
					builder.add(Node.builder().name(current).build());
				}
			} else {
				Optional<Node> matchingNode = nodeTree.stream()
					.filter(n -> n.name().equals(current))
					.findAny();
				matchingNode.ifPresent(node -> {
					builder.add(Node.builder()
							.name(current)
							.addAllChildren(filter(node.children(), knownNames, src, level+1))
							.build());
				});
			}
		}
		return builder.build();
	}

	public static Tree treeOf(PropertyTree tree) {
		Builder builder = ImmutableTree.builder();

		MutableGraph<String> graph = GraphBuilder.directed()
			.allowsSelfLoops(false)
			.build();

		Set<String> properties = tree.properties();
		properties.forEach(n -> {
			Maybe<PropertyTree> found = tree.find(n);

			found.ifPresent(sub -> {
				Maybe<String> label = sub.find(String.class, "name");
				ImmutableList<String> children = sub.findList(String.class, "children");
				label.ifPresent(parent -> {
					graph.addNode(parent);
					children.forEach(child -> {
						graph.addNode(child);
						graph.putEdge(child, parent);
						Preconditions.checkArgument(Graphs.hasCycle(graph)==false,"cycle detected: %s - %s",child, parent);
					});

					builder.putRelation(parent, children);
				});
			});
		});


		return builder.build();
	}

	@Immutable
	public interface Node {
		String name();

		ImmutableList<Node> children();

		public static ImmutableNode.Builder builder() {
			return ImmutableNode.builder();
		}
	}
}
