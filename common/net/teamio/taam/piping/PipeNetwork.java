package net.teamio.taam.piping;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by oliver on 2017-12-07.
 */
public class PipeNetwork {
	public static final PipeNetwork NET = new PipeNetwork();

	public final DirectedGraph<IPipe> graph;
	public final BlockingQueue<IPipe> additions;
	public final BlockingQueue<IPipe> removals;

	public boolean needsRescan = false;

	public PipeNetwork() {
		graph = new DirectedGraph<IPipe>();
		additions = new LinkedBlockingQueue<IPipe>();
		removals = new LinkedBlockingQueue<IPipe>();
	}

	public void addPipe(IPipe pipe) {
		additions.add(pipe);
		needsRescan = true;
	}

	public void removePipe(IPipe pipe) {
		removals.add(pipe);
		needsRescan = true;
	}

	public void rescan() {
		needsRescan = false;
		{
			IPipe p;
			while ((p = removals.poll()) != null) {
				graph.removeNode(p);
			}
			while ((p = additions.poll()) != null) {
				graph.addNode(p);
			}
		}

		Queue<IPipe> toRescan = new ArrayDeque<IPipe>(graph.orderedNodes);

		Set<IPipe> edges = new HashSet<IPipe>();
		Set<IPipe> existingEdges = new HashSet<IPipe>();

		while (!toRescan.isEmpty()) {
			IPipe pipe = toRescan.remove();
			IBlockAccess world = pipe.getWorld();
			BlockPos pos = pipe.getPos();

			if (world == null || pos == null) {
				// Do not process pipes that are not fully loaded
				needsRescan = true;
				continue;
			}

			// Get internal connections
			IPipe[] internal = pipe.getInternalPipes();
			if (internal != null) {
				for (IPipe intPipe : internal) {
					if (intPipe != null) {
						edges.add(intPipe);
					}
				}
			}

			// Get world connections
			for (EnumFacing side : EnumFacing.VALUES) {
				if (pipe.isSideAvailable(side)) {
					IPipe external = PipeUtil.getConnectedPipe(world, pos, side);
					if (external != null) {
						edges.add(external);
					}
				}
			}

			// Get existing edges & remove those that don't exist anymore
			graph.edgesFrom(pipe, existingEdges);
			existingEdges.removeAll(edges);
			for (IPipe edge : existingEdges) {
				graph.removeEdge(pipe, edge);
			}

			// Add new edges, mark new pipes for rescan
			for (IPipe edge : edges) {
				if (graph.addNode(edge)) {
					toRescan.add(edge);
				}
				graph.addEdge(pipe, edge);
			}

			edges.clear();
			existingEdges.clear();

		}
	}

	/**
	 * Marks the pipe network to rescan on the next update.
	 * Required when e.g. rotating machines without actually adding/removing pipes from the world.
	 */
	public void forceRescan() {
		needsRescan = true;
	}

	public List<IPipe> getPipes() {
		return graph.orderedNodes;
	}
}
