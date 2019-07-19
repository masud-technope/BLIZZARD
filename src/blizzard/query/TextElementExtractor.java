package blizzard.query;

import java.util.ArrayList;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class TextElementExtractor {

	String reportText;
	DirectedGraph<String, DefaultEdge> textGraph = null;
	int windowSize = 2;
	ArrayList<String> sentences;

	public TextElementExtractor(String reportText) {
		this.reportText = reportText;
		this.textGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.sentences = getSentenceSet(this.reportText);
	}

	public ArrayList<String> getSentenceSet(String content) {
		// collecting individual sentences from a list
		ArrayList<String> sentlist = new ArrayList<>();
		content = content.replace('\n', ' ');
		String separator = "(?<=[.?!:;])\\s+(?=[a-zA-Z0-9])";
		// String separator="\\. |\\? |! ";
		String[] sentences = content.split(separator);
		for (String sentence : sentences) {
			// System.out.println(sentence);
			sentlist.add(sentence);
		}
		return sentlist;
	}

	public DirectedGraph<String, DefaultEdge> developTextGraph() {
		// developing the word network
		for (String sentence : this.sentences) {
			String[] tokens = sentence.split("\\s+");
			for (int index = 0; index < tokens.length; index++) {
				String previousToken = new String();
				String nextToken = new String();
				String currentToken = tokens[index];
				if (index > 0)
					previousToken = tokens[index - 1];

				if (index < tokens.length - 1)
					nextToken = tokens[index + 1];

				// now add the graph nodes
				if (!textGraph.containsVertex(currentToken)) {
					textGraph.addVertex(currentToken);
				}
				if (!textGraph.containsVertex(previousToken)
						&& !previousToken.isEmpty()) {
					textGraph.addVertex(previousToken);
				}
				if (!textGraph.containsVertex(nextToken)
						&& !nextToken.isEmpty()) {
					textGraph.addVertex(nextToken);
				}

				// System.out.println(currentToken);

				// adding edges to the graph
				if (!previousToken.isEmpty())
					if (!textGraph.containsEdge(currentToken, previousToken)) {
						textGraph.addEdge(currentToken, previousToken);
					}

				if (!nextToken.isEmpty())
					if (!textGraph.containsEdge(currentToken, nextToken)) {
						textGraph.addEdge(currentToken, nextToken);
					}
			}
		}
		// returning the created graph
		return textGraph;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
