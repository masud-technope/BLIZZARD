package blizzard.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;

import utility.MiscUtility;
import core.PageRankProviderMgr;

public class TraceClassesSelector {

	ArrayList<String> traces;
	boolean canonical = false;

	public TraceClassesSelector(ArrayList<String> traces, boolean canonical) {
		this.traces = traces;
		this.canonical = canonical;
	}

	protected HashMap<String, Double> getSalientClasses() {
		TraceElemExtractor teExtractor = new TraceElemExtractor(traces);
		teExtractor.decodeTraces(canonical);
		//adding extra step
		teExtractor.expandTraceNodes();
		DirectedGraph<String, DefaultEdge> classGraph = teExtractor
				.getClassGraph();
		//showGraphEdges(classGraph);;
		
		PageRankProviderMgr manager = new PageRankProviderMgr(classGraph);
		return manager.getPageRanks();
	}

	protected void showGraphEdges(DirectedGraph<String, DefaultEdge> classGraph) {
		// showing the graph
		HashSet<DefaultEdge> edges = new HashSet<DefaultEdge>(
				classGraph.edgeSet());
		HashSet<String> nodes = new HashSet<>();
		for (DefaultEdge edge : edges) {
			System.out.println(classGraph.getEdgeSource(edge) + "/"
					+ classGraph.getEdgeTarget(edge));
			nodes.add(classGraph.getEdgeSource(edge));
			nodes.add(classGraph.getEdgeTarget(edge));
		}
		MiscUtility.showList(nodes);
	}
}
