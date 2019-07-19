package blizzard.query;

import java.util.ArrayList;
import java.util.HashMap;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import core.PageRankProviderMgr;

public class TraceMethodsSelector {

	ArrayList<String> traces;
	boolean canonical;

	public TraceMethodsSelector(ArrayList<String> traces, boolean canonical) {
		this.traces = traces;
		this.canonical=canonical;
	}
	
	protected HashMap<String, Double> getSalientMethods() {
		TraceElemExtractor teExtractor = new TraceElemExtractor(traces);
		teExtractor.decodeTraces(canonical);
		DirectedGraph<String, DefaultEdge> methodGraph = teExtractor
				.getMethodGraph();
		PageRankProviderMgr manager = new PageRankProviderMgr(methodGraph);
		return manager.getPageRanks();
	}
}
