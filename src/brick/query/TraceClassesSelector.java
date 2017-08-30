package brick.query;

import java.util.ArrayList;
import java.util.HashMap;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
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
		DirectedGraph<String, DefaultEdge> classGraph = teExtractor
				.getClassGraph();
		PageRankProviderMgr manager = new PageRankProviderMgr(classGraph);
		return manager.getPageRanks();
	}
}
