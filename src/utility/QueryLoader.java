package utility;

import java.util.ArrayList;
import java.util.HashMap;

import config.StaticData;

public class QueryLoader {

	protected static String extractQuery(String line) {
		String temp = new String();
		String[] parts = line.split("\\s+");
		for (int i = 1; i < parts.length; i++) {
			temp += parts[i] + "\t";
		}
		return temp.trim();
	}

	public static HashMap<Integer, String> loadQuery(String queryFile) {
		ArrayList<String> qlines = ContentLoader.getAllLinesOptList(queryFile);
		HashMap<Integer, String> queryMap = new HashMap<>();
		for (String line : qlines) {
			int bugID = Integer.parseInt(line.split("\\s+")[0]);
			String query = extractQuery(line);
			queryMap.put(bugID, query);
		}
		return queryMap;
	}

	public static HashMap<Integer, String> loadBRTitles(String repoName,
			ArrayList<Integer> selectedBugs) {
		// loading the bug report titles
		HashMap<Integer, String> titleMap = new HashMap<>();
		for (int bugID : selectedBugs) {
			String requestFile = StaticData.BRICK_EXP + "/changereqs/"
					+ repoName + "/reqs/" + bugID + ".txt";
			String title = ContentLoader.getAllLinesOptList(requestFile).get(0);
			titleMap.put(bugID, title);
		}
		return titleMap;
	}

}
