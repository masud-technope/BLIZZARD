package blizzard.query.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportedPerformanceProvider {

	String reportKey;

	public ReportedPerformanceProvider(String reportKey) {
		this.reportKey = reportKey;
	}

	public void determineRetrievalPerformance(int TOPK) {
		String[] repos = { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug",
				"eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70" };

		double sumHitK = 0;
		double sumMAPK = 0;
		double sumMRR = 0;

		for (String repoName : repos) {
			String queryFile = "./BLIZZARD/Query/" + repoName + "/proposed-"
					+ reportKey + ".txt";
			BLIZZARDResultProvider brProvider = new BLIZZARDResultProvider(
					repoName, TOPK, queryFile);
			HashMap<Integer, ArrayList<String>> tempResults = brProvider
					.collectBLIZZARDResults();
			brProvider.calculateBLIZZARDPerformance(tempResults);
			sumHitK += brProvider.TopkAcc;
			sumMAPK += brProvider.mapK;
			sumMRR += brProvider.mrrK;
			System.out.println(tempResults.size());
			System.out.println();
		}

		System.out.println("==========================================");
		System.out.println("Reported Bug Localization Performance");
		System.out.println("==========================================");

		System.out.println("Hit@" + TOPK + " : " + sumHitK / repos.length);
		System.out.println("MAP@" + TOPK + " : " + sumMAPK / repos.length);
		System.out.println("MRR@" + TOPK + " : " + sumMRR / repos.length);
	}

	public void determineQE() {
		String[] repos = { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug",
				"eclipse.jdt.ui", "eclipse.pde.ui", "tomcat70" };

		double sumImproved = 0;
		double sumWorsened = 0;
		double sumPreserved = 0;
		double datasetSize = 0;

		for (String repoName : repos) {
			String proposedQEFile = "./BLIZZARD/QE/" + repoName + "/proposed-"
					+ reportKey + ".txt";
			String baselineQEFile = "./Baseline/QE/" + repoName + ".txt";
			ResultComparer rcomparer = new ResultComparer(repoName,
					baselineQEFile, proposedQEFile, false);
			rcomparer.compareResults();

			sumImproved += rcomparer.getImproved();
			sumWorsened += rcomparer.getWorsened();
			sumPreserved += rcomparer.getPreserved();
			datasetSize += rcomparer.getSelectedBug();

			System.out.println();
		}

		System.out.println("==========================================");
		System.out.println("Reported Query Effectiveness");
		System.out.println("==========================================");

		System.out.println("Report Key:"+reportKey);
		System.out.println("Improved: " + sumImproved + "," + sumImproved
				/ datasetSize);
		System.out.println("Worsened: " + sumWorsened + "," + sumWorsened
				/ datasetSize);
		System.out.println("Preserved:" + sumPreserved + "," + sumPreserved
				/ datasetSize);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
