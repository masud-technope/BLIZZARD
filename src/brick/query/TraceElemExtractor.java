package brick.query;

import java.util.ArrayList;
import java.util.HashSet;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import utility.MiscUtility;

public class TraceElemExtractor {

	ArrayList<String> traces;
	HashSet<String> packages;
	HashSet<String> methods;
	HashSet<String> classes;

	DirectedGraph<String, DefaultEdge> methodGraph = null;
	DirectedGraph<String, DefaultEdge> classGraph = null;

	public TraceElemExtractor(ArrayList<String> traces) {
		this.traces = refineTraces(traces);
		this.packages = new HashSet<>();
		this.methods = new HashSet<>();
		this.classes = new HashSet<>();

		// initialize the graph
		this.methodGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.classGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
	}

	protected ArrayList<String> refineTraces(ArrayList<String> traces) {
		// clean the trace if it contains file name
		ArrayList<String> refined = new ArrayList<>();
		for (String trace : traces) {
			if (trace.indexOf('(') > 0) {
				int leftParenIndex = trace.indexOf('(');
				String line = trace.substring(0, leftParenIndex);
				refined.add(line);
			} else {
				refined.add(trace);
			}
		}
		return refined;
	}

	protected String cleanEntity(String itemName) {
		String[] parts = itemName.split("\\p{Punct}+|\\s+");
		return MiscUtility.list2Str(parts);
	}

	protected void decodeTraces(boolean canonical) {
		// analyze the trace information

		String prevClass = new String();
		String prevMethod = new String();

		for (String line : this.traces) {
			String[] parts = line.split("\\.");
			int length = parts.length;

			String methodName = new String();
			String className = new String();
			String packageName = new String();

			if (canonical) {
				for (int i = 0; i < length; i++) {
					methodName += "." + parts[i];
				}

				for (int i = 0; i < length - 1; i++) {
					className += "." + parts[i];
				}

				for (int i = 0; i < length - 2; i++) {
					packageName += "." + parts[i];
				}

				// removing unexpected dots
				if (!methodName.trim().isEmpty())
					methodName = methodName.substring(1);
				if (!className.trim().isEmpty())
					className = className.substring(1);
				if (!packageName.trim().isEmpty())
					packageName = packageName.substring(1);

			} else {
				if (length >= 2) {
					methodName = parts[length - 1];
					methodName = cleanEntity(methodName);
					className = parts[length - 2];
					className = cleanEntity(className);
					// package is always canonical
					for (int i = 0; i < length - 2; i++) {
						packageName += "." + parts[i];
					}
					packageName = cleanEntity(packageName.trim());
				}
			}

			this.methods.add(methodName);
			this.classes.add(className);
			this.packages.add(packageName);

			// record the dependencies
			if (!methodGraph.containsVertex(methodName)) {
				methodGraph.addVertex(methodName);
				if (!prevMethod.isEmpty()) {
					if (!methodGraph.containsEdge(methodName, prevMethod)) {
						this.methodGraph.addEdge(methodName, prevMethod);
					}
				}
				prevMethod = methodName;
			}

			
			if (!classGraph.containsVertex(className)) {
				classGraph.addVertex(className);
				if (!prevClass.isEmpty()) {
					if (!classGraph.containsEdge(className, prevClass)) {
						this.classGraph.addEdge(className, prevClass);
					}
				}
				prevClass = className;
			}

			//added extra module
			if (!classGraph.containsVertex(methodName)) {
				classGraph.addVertex(methodName);
				if (!classGraph.containsEdge(methodName, className)) {
					this.classGraph.addEdge(methodName, className);
				}
				if (!classGraph.containsEdge(className, methodName)) {
					this.classGraph.addEdge(className,methodName);
				}
				
				if (!prevMethod.isEmpty()) {
					if (!classGraph.containsEdge(methodName, prevMethod)) {
						this.classGraph.addEdge(methodName, prevMethod);
					}
					
					/*if (!classGraph.containsEdge(methodName, prevClass)) {
						this.classGraph.addEdge(methodName, prevClass);
					}*/
					
				}
				prevMethod = methodName;
			}

		}
	}

	protected DirectedGraph<String, DefaultEdge> getMethodGraph() {
		return this.methodGraph;
	}

	protected DirectedGraph<String, DefaultEdge> getClassGraph() {
		return this.classGraph;
	}
}
