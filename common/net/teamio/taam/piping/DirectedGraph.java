package net.teamio.taam.piping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by oliver on 2017-12-07.
 * Based on net.minecraftforge.fml.common.toposort.TopologicalSort.DirectedGraph
 */
public class DirectedGraph<T> implements Iterable<T> {
	private final Map<T, SortedSet<T>> graph = new HashMap<T, SortedSet<T>>();
	public List<T> orderedNodes = new ArrayList<T>();

	public boolean addNode(T node) {
		// Ignore nodes already added
		if (graph.containsKey(node)) {
			return false;
		}

		orderedNodes.add(node);
		graph.put(node, new TreeSet<T>(new Comparator<T>() {
			@Override
			public int compare(T o1, T o2) {
				return orderedNodes.indexOf(o1) - orderedNodes.indexOf(o2);
			}
		}));
		return true;
	}

	public void removeNode(T node) {
		graph.remove(node);
		orderedNodes.remove(node);
	}

	public void addEdge(T from, T to) {
		if (!(graph.containsKey(from) && graph.containsKey(to))) {
			throw new NoSuchElementException("Missing nodes from graph");
		}

		graph.get(from).add(to);
	}

	public void removeEdge(T from, T to) {
		if (!graph.containsKey(from)) {
			return;
		}

		graph.get(from).remove(to);
	}

	public boolean edgeExists(T from, T to) {
		if (!(graph.containsKey(from) && graph.containsKey(to))) {
			return false;
		}

		return graph.get(from).contains(to);
	}

	public Set<T> edgesFrom(T from) {
		if (!graph.containsKey(from)) {
			throw new NoSuchElementException("Missing node from graph");
		}

		return Collections.unmodifiableSortedSet(graph.get(from));
	}

	public void edgesFrom(T from, Collection<T> collection) {
		if (!graph.containsKey(from)) {
			throw new NoSuchElementException("Missing node from graph");
		}

		collection.addAll(graph.get(from));
	}

	@Override
	public Iterator<T> iterator() {
		return orderedNodes.iterator();
	}

	public int size() {
		return graph.size();
	}

	public boolean isEmpty() {
		return graph.isEmpty();
	}

	@Override
	public String toString() {
		return graph.toString();
	}
}