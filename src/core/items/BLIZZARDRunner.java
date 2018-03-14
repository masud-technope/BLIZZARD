package core.items;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentWriter;
import brick.query.BLIZZARDQueryManager;
import brick.query.tester.BLIZZARDResultProvider;

public class BLIZZARDRunner {

	public static void saveItems(String outputFile,
			HashMap<Integer, String> suggestedQueries) {
		ArrayList<String> results = new ArrayList<>();
		for (int bugID : suggestedQueries.keySet()) {
			String line = bugID + "\t" + suggestedQueries.get(bugID);
			results.add(line);
		}
		ContentWriter.writeContent(outputFile, results);
	}

	public static void saveItemList(String outputFile,
			HashMap<Integer, ArrayList<String>> resultMap) {
		ArrayList<String> results = new ArrayList<>();
		for (int bugID : resultMap.keySet()) {
			ArrayList<String> ranked = resultMap.get(bugID);
			int index = 0;
			for (String file : ranked) {
				String line = bugID + "\t" + file + "\t" + index;
				results.add(line);
				index++;
			}
		}
		ContentWriter.writeContent(outputFile, results);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		long start = System.currentTimeMillis();
		if (args.length == 0) {
			System.out
					.println("Please check README, and enter your arguments.");
			return;
		}

		HashMap<String, String> keymap = new HashMap<>();
		for (int i = 0; i < args.length; i += 2) {
			String key = args[i];
			String value = args[i + 1];
			keymap.put(key, value);
		}

		String task = null;
		if (keymap.containsKey("-task")) {
			task = keymap.get("-task");

			String repoName = null;
			String queryFile = null;
			String resultFile = null;
			String bugIDFile = null;
			int topk = 10;

			switch (task) {
			case "reformulateQuery":
				if (keymap.containsKey("-repo")) {
					repoName = keymap.get("-repo");
				} else {
					System.out
							.println("Please enter a project name (e.g., ecf)");
					return;
				}
				if (keymap.containsKey("-bugIDFile")) {
					bugIDFile = keymap.get("-bugIDFile");
				} else {
					System.out.println("Please enter your bug IDs in a file.");
				}

				if (keymap.containsKey("-queryFile")) {
					queryFile = keymap.get("-queryFile");
				} else {
					System.out.println("Please enter your query file.");
				}

				if (!repoName.isEmpty() && !bugIDFile.isEmpty()
						&& !queryFile.isEmpty()) {
					HashMap<Integer, String> suggestedQueries = new BLIZZARDQueryManager(
							repoName, bugIDFile).getSuggestedQueries();
					saveItems(queryFile, suggestedQueries);
				}
				break;
			case "getResult":
				if (keymap.containsKey("-repo")) {
					repoName = keymap.get("-repo");
				} else {
					System.out
							.println("Please enter a project name (e.g., ecf)");
					return;
				}
				if (keymap.containsKey("-queryFile")) {
					queryFile = keymap.get("-queryFile");
				} else {
					System.out.println("Please enter the query file.");
				}
				if (keymap.containsKey("-topk")) {
					topk = Integer.parseInt(keymap.get("-topk"));
				} else {
					System.out
							.println("Please enter a Top-K value. Default Top-K = 10");
				}
				if (keymap.containsKey("-resultFile")) {
					resultFile = keymap.get("-resultFile");
				} else {
					System.out.println("Please enter your result file.");
				}

				if (!repoName.isEmpty() && !queryFile.isEmpty()
						&& !resultFile.isEmpty()) {
					BLIZZARDResultProvider bprovider = new BLIZZARDResultProvider(
							repoName, topk, queryFile);
					HashMap<Integer, ArrayList<String>> results = bprovider
							.collectBLIZZARDResults();
					bprovider.calculateBLIZZARDPerformance(results);
					saveItemList(resultFile, results);
				}
				break;

			default:
				break;
			}

		}
		long end = System.currentTimeMillis();
		System.out.println("Time elapsed:" + (end - start) / 1000 + " seconds");
	}
}
