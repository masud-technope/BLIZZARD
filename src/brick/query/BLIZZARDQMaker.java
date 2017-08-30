package brick.query;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentWriter;
import utility.FileMapLoader;
import utility.SelectedBugs;
import config.StaticData;

public class BLIZZARDQMaker {

	String repoName;
	String queryFile;
	String rubric;
	ArrayList<Integer> selectedBugs;
	String reportGroup;
	HashMap<Integer, ArrayList<String>> exceptionMap;

	public BLIZZARDQMaker(String repoName, String queryFile, String reportGroup) {
		this.repoName = repoName;
		this.reportGroup = reportGroup;
		this.queryFile = queryFile;
		this.loadRGSpecificBugs(reportGroup);
	}

	protected void loadRGSpecificBugs(String reportGroup) {
		switch (reportGroup) {
		case "ST":
			this.selectedBugs = SelectedBugs.getStackSelectedBugs(repoName);
			this.exceptionMap = FileMapLoader.loadBRExceptions(repoName);
			break;
		case "NL":
			this.selectedBugs = SelectedBugs.getNLSelectedBugs(repoName);
			break;
		case "PE":
			this.selectedBugs = SelectedBugs.getPESelectedBugs(repoName);
			break;
		}
	}

	public ArrayList<String> makeBRQueries() {
		ArrayList<String> queries = new ArrayList<>();
		for (int bugID : this.selectedBugs) {
			BRICKQueryProvider bqProvider = null;
			ArrayList<String> exceptions = new ArrayList<>();
			String brickQuery = new String();
			switch (reportGroup) {
			case "ST":
				if (exceptionMap.containsKey(bugID)) {
					exceptions = exceptionMap.get(bugID);
				}
				bqProvider = new BRICKQueryProvider(repoName, bugID,
						reportGroup, "C", exceptions);
				brickQuery = bqProvider.provideBRICKQuery();
				break;
			case "NL":
				bqProvider = new BRICKQueryProvider(repoName, bugID,
						reportGroup);
				brickQuery = bqProvider.provideBRICKQuery();
				break;
			case "PE":
				bqProvider = new BRICKQueryProvider(repoName, bugID,
						reportGroup);
				brickQuery = bqProvider.provideBRICKQuery();
				break;
			}
			queries.add(bugID + "\t" + brickQuery);

		}
		// now save the output
		ContentWriter.writeContent(queryFile, queries);

		System.out.println("BLIZZARD returned " + queries.size()
				+ " queries successfully!");

		// now return queries
		return queries;
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		HashMap<String, String> keymap = new HashMap<>();
		for (int i = 0; i < args.length; i += 2) {
			String key = args[i];
			String value = args[i + 1];
			keymap.put(key, value);
		}
		String repoName = "ecf";
		String reportGroup = "ST";
		if (keymap.containsKey("-repo")) {
			repoName = keymap.get("-repo");
		}
		if (keymap.containsKey("-rgkey")) {
			reportGroup = keymap.get("-rgkey");
		}

		String queryFile = StaticData.BRICK_EXP + "/Query/" + repoName;
		queryFile += "-" + reportGroup + ".txt";

		BLIZZARDQMaker brqMaker = new BLIZZARDQMaker(repoName, queryFile,
				reportGroup);
		brqMaker.makeBRQueries();

		long end = System.currentTimeMillis();
		System.out.println("Time elapsed:" + (end - start) / 1000 + " s");
	}
}
