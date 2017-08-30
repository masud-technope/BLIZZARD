package core.items;

import java.util.HashMap;

import brick.query.BLIZZARDQMaker;
import brick.query.tester.BLIZZARDPerformanceMgr;
import brick.query.tester.BLIZZARDRankComparer;
import brick.query.tester.BLIZZARDRankProvider;
import config.StaticData;

public class BLIZZARDRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
			String reportGroup = null;
			String queryFile = null;
			int topk = 10;

			switch (task) {
			case "createQuery":
				repoName = keymap.get("-repo");
				if (repoName == null) {
					System.out.println("Please enter a project name");
					return;
				}
				reportGroup = keymap.get("-rgkey");
				if (reportGroup == null) {
					System.out
							.println("Please enter a report category (e.g., ST, PE, NL, ALL)");
					return;
				}
				queryFile = StaticData.BRICK_EXP + "/Query/" + repoName;
				queryFile += "-" + reportGroup + ".txt";
				new BLIZZARDQMaker(repoName, queryFile, reportGroup)
						.makeBRQueries();
				break;
			case "getRetPerf":
				repoName = keymap.get("-repo");
				if (repoName == null) {
					System.out.println("Please enter a project name");
					return;
				}
				reportGroup = keymap.get("-rgkey");
				if (reportGroup == null) {
					System.out.println("Please enter a report category (e.g., ST, PE, NL, ALL)");
					return;
				}
				queryFile = StaticData.BRICK_EXP + "/Query/" + repoName;
				queryFile += "-" + reportGroup + ".txt";
				topk = Integer.parseInt(keymap.get("-topk"));
				if (topk == 0) {
					System.out.println("Please enter a Top-K value");
				}
				new BLIZZARDPerformanceMgr(repoName, topk, reportGroup)
						.determinePerformance();
				break;
			case "getQEPerf":
				repoName = keymap.get("-repo");
				if (repoName == null) {
					System.out.println("Please enter a project name");
					return;
				}
				reportGroup = keymap.get("-rgkey");
				if (reportGroup == null) {
					System.out
							.println("Please enter a report category (e.g., ST, PE, NL, ALL)");
					return;
				}
				// collecting brick ranks
				new BLIZZARDRankProvider(repoName, reportGroup)
						.collectBRICKRanks();
				// comparing brick ranks
				new BLIZZARDRankComparer(repoName, reportGroup).compareQE();

				break;
			default:
				break;
			}

		}
	}
}
