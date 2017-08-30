package lucenecheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class ResultResolver {

	HashMap<Integer, String> keymap;
	ArrayList<ResultFile> results;
	ArrayList<String> luceneResults;
	boolean lucene = false;

	public ResultResolver(HashMap<Integer, String> keymap,
			ArrayList<ResultFile> results) {
		this.keymap = keymap;
		this.results = results;
	}

	public ResultResolver(HashMap<Integer, String> keymap,
			ArrayList<String> results, boolean lucene) {
		this.keymap = keymap;
		this.luceneResults = results;
		this.lucene = lucene;
	}

	public ArrayList<String> resolveResults() {
		// resolving the results
		ArrayList<String> tempResults = new ArrayList<>();
		for (ResultFile result : results) {
			String fileName = new File(result.filePath).getName();
			int fileID = Integer.parseInt(fileName.split("\\.")[0]);
			if (this.keymap.containsKey(fileID)) {
				tempResults.add(this.keymap.get(fileID));
			}
		}
		return tempResults;
	}

	public ArrayList<String> resolveLuceneResults() {
		ArrayList<String> tempResults = new ArrayList<>();
		for (String result : luceneResults) {
			String fileName = new File(result).getName();
			int fileID = Integer.parseInt(fileName.split("\\.")[0]);
			if (this.keymap.containsKey(fileID)) {
				tempResults.add(this.keymap.get(fileID));
			}
		}
		return tempResults;
	}
}
