package utility;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;

public class AdjacencyLoader {

	public static HashMap<String, ArrayList<String>> loadAdjacencyList(
			String repoName) {
		long start = System.currentTimeMillis();
		String adjFile = StaticData.BRICK_EXP + "/adjacentlist/ss-adjacent/"
				+ repoName + ".txt";
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(adjFile);
		HashMap<String, ArrayList<String>> adjMap = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.split(":");
			if (parts.length == 2) {
				String key = parts[0];
				ArrayList<String> adjNodes = MiscUtility.str2List(parts[1]
						.trim());
				adjMap.put(key, adjNodes);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("ADJ loaded:" + (end - start) / 1000 + "s");
		return adjMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(loadAdjacencyList("eclipse.jdt.core").size());
	}
}
