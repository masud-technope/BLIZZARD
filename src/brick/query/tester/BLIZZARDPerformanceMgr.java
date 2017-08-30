package brick.query.tester;

import java.util.ArrayList;
import config.StaticData;
import utility.ContentLoader;

public class BLIZZARDPerformanceMgr {

	String repoName;
	int TOPK;
	String catKey;
	boolean all = false;

	public BLIZZARDPerformanceMgr(String repoName, int TOPK, String catKey) {
		this.repoName = repoName;
		this.TOPK = TOPK;
		this.catKey = catKey;
	}

	protected ArrayList<String> loadQueries() {
		String queryFile = StaticData.BRICK_EXP + "/BLIZZARD-Query/" + repoName + "/BR-"
				+ this.catKey + ".txt";
		return ContentLoader.getAllLinesOptList(queryFile);
	}

	public void determinePerformance() {
		ArrayList<String> queries = loadQueries();
		BRICKPerformanceCalc calc = new BRICKPerformanceCalc(repoName, TOPK,
				queries);
		calc.calculateBRICKPerformance();
		System.out.println("System: " + repoName);
		System.out.println("Top-" + TOPK + " Accuracy: " + calc.getTopKAcc());
		System.out.println("MRR@" + TOPK + ": " + calc.getMRRK());
		System.out.println("MAP@" + TOPK + ": " + calc.getMAPK());
		System.out.println("MR@" + TOPK + ": " + calc.getMRK());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start=System.currentTimeMillis();
		String repoName = "ecf";
		String catKey = "ALL";
		int TOPK = 5;
		new BLIZZARDPerformanceMgr(repoName, TOPK, catKey).determinePerformance();
		long end=System.currentTimeMillis();
		System.out.println("Time needed: "+(end-start)/1000+" s");
	}
}
