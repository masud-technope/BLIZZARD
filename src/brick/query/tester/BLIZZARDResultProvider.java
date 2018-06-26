package brick.query.tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lucenecheck.ClassResultRankMgr;
import lucenecheck.LuceneSearcher;
import utility.ContentLoader;
import utility.GoldsetLoader;

public class BLIZZARDResultProvider {

	String repoName;
	int TOPK;
	String resultKey;
	String[] resultKeys;
	String queryFile;
	public HashMap<Integer, String> queryMap;
	double TopkAcc;
	double mapK;
	double mrrK;
	double mrK;

	public BLIZZARDResultProvider(String repoName, int TOPK, String queryFile) {
		this.repoName = repoName;
		this.TOPK = TOPK;
		this.queryMap = extractQueryMap(queryFile);
	}

	protected String extractQuery(String line) {
		String[] words = line.split("\\s+");
		String temp = new String();
		for (int i = 1; i < words.length; i++) {
			temp += words[i] + "\t";
		}
		return temp.trim();
	}

	protected HashMap<Integer, String> extractQueryMap(String queryFile) {
		// extracting queries
		ArrayList<String> lines = ContentLoader.getAllLinesList(queryFile);
		HashMap<Integer, String> queryMap = new HashMap<>();
		for (String line : lines) {
			String query = extractQuery(line);
			int bugID = Integer.parseInt(line.split("\\s+")[0].trim());
			queryMap.put(bugID, query);
		}
		return queryMap;
	}

	protected double getRR(int firstGoldIndex) {
		if (firstGoldIndex <= 0)
			return 0;
		return 1.0 / firstGoldIndex;
	}

	protected double getRR(ArrayList<Integer> foundIndices) {
		if (foundIndices.isEmpty())
			return 0;
		double min = 10000;
		for (int index : foundIndices) {
			if (index > 0) {
				if (index < min) {
					min = index;
				}
			} else {
				return 0;
			}
		}
		return 1 / min;
	}

	protected double getAP(ArrayList<Integer> foundIndices) {
		// calculating the average precision
		int indexcount = 0;
		double sumPrecision = 0;
		if (foundIndices.isEmpty())
			return 0;
		HashSet<Integer> uniquesIndices=new HashSet<Integer>(foundIndices);
		for (int index : uniquesIndices) {
			indexcount++;
			double precision = (double) indexcount / index;
			sumPrecision += precision;
		}
		return sumPrecision / indexcount;
	}

	protected double getRecall(ArrayList<Integer> foundIndices,
			ArrayList<String> goldset) {
		// calculating recall
		return (double) foundIndices.size() / goldset.size();
	}

	public HashMap<Integer, ArrayList<String>> collectBLIZZARDResults() {
		// collect BLIZZARD results
		System.out.println("Collection of results started. Please wait..");
		HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
		for (int bugID : this.queryMap.keySet()) {
			String singleQuery = this.queryMap.get(bugID);
			LuceneSearcher searcher = new LuceneSearcher(bugID, repoName,
					singleQuery);
			ArrayList<String> ranked = searcher.performVSMSearchList(false);
			resultMap.put(bugID, ranked);
		}
		System.out.println("Localization results collected successfully :-)");
		return resultMap;
	}

	public HashMap<Integer, ArrayList<String>> collectBLIZZARDResultsAll() {
		// collect BLIZZARD results
		System.out.println("Collection of results started. Please wait..");
		HashMap<Integer, ArrayList<String>> resultMap = new HashMap<>();
		for (int bugID : this.queryMap.keySet()) {
			String singleQuery = this.queryMap.get(bugID);
			LuceneSearcher searcher = new LuceneSearcher(bugID, repoName,
					singleQuery);
			ArrayList<String> ranked = searcher.performVSMSearchList(true);
			resultMap.put(bugID, ranked);
		}
		System.out.println("Localization results collected successfully :-)");
		return resultMap;
	}
	
	public void calculateBLIZZARDPerformance(
			HashMap<Integer, ArrayList<String>> resultMap) {
		double sumRR = 0;
		double sumAP = 0;
		double sumAcc = 0;
		System.out.println("Bug Localization Performance:");

		for (int bugID : resultMap.keySet()) {
			ArrayList<String> results = resultMap.get(bugID);
			ArrayList<String> goldset = GoldsetLoader.goldsetLoader(repoName,
					bugID);
			ClassResultRankMgr clsRankMgr = new ClassResultRankMgr(repoName,
					results, goldset);
			ArrayList<Integer> indices = clsRankMgr.getCorrectRanksDotted(TOPK);

			double rr = 0, ap = 0, rec = 0;

			if (!indices.isEmpty()) {
				rr = getRR(indices);
				if (rr > 0) {
					sumRR += rr;
				}
				ap = getAP(indices);
				if (ap > 0) {
					sumAP += ap;
					sumAcc++;
				}
			}
		}

		// now calculate the mean performance
		this.TopkAcc = (double) sumAcc / resultMap.size();
		this.mrrK = sumRR / resultMap.size();
		this.mapK = sumAP / resultMap.size();

		// System.out.println(repoName + " " + this.TopkAcc);

		System.out.println("System: " + repoName);
		System.out.println("Hit@" + TOPK + " Accuracy: " + this.getTopKAcc());
		System.out.println("MRR@" + TOPK + ": " + this.getMRRK());
		System.out.println("MAP@" + TOPK + ": " + this.getMAPK());

		// clear the key map file
		ClassResultRankMgr.keyMap.clear();
	}

	public double getTopKAcc() {
		return this.TopkAcc;
	}

	public double getMAPK() {
		return this.mapK;
	}

	public double getMRK() {
		return this.mrK;
	}

	public double getMRRK() {

		return this.mrrK;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		String queryFile = "./input/query.txt";
		int TOPK = 10;
		// System.out.println(new BLIZZARDResultProvider(repoName, TOPK,
		// queryFile).collectBLIZZARDResults());
	}
}
