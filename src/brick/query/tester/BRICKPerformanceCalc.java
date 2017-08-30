package brick.query.tester;

import java.util.ArrayList;
import java.util.HashMap;
import lucenecheck.ClassResultRankMgr;
import lucenecheck.LuceneSearcher;
import utility.ContentLoader;

public class BRICKPerformanceCalc {

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

	public BRICKPerformanceCalc(String repoName, int TOPK,
			ArrayList<String> queries) {
		this.repoName = repoName;
		this.TOPK = TOPK;
		this.queryMap = extractQueriesFromMemory(queries);
	}

	public BRICKPerformanceCalc(String repoName, int TOPK,
			HashMap<Integer, String> queryMap) {
		this.repoName = repoName;
		this.TOPK = TOPK;
		this.queryMap = queryMap;
	}

	protected String extractQuery(String line) {
		String[] words = line.split("\\s+");
		String temp = new String();
		for (int i = 1; i < words.length; i++) {
			temp += words[i] + "\t";
		}
		return temp.trim();
	}

	protected HashMap<Integer, String> extractQueriesFromMemory(
			ArrayList<String> qlines) {
		HashMap<Integer, String> tempMap = new HashMap<>();
		for (String line : qlines) {
			int bugID = Integer.parseInt(line.split("\\s+")[0]);
			String myquery = extractQuery(line);
			tempMap.put(bugID, myquery);
		}
		return tempMap;
	}
	
	protected HashMap<Integer, String> extractQueryMap() {
		// extracting queries
		ArrayList<String> lines = ContentLoader.getAllLinesList(this.queryFile);
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
		for (int index : foundIndices) {
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

	public void calculateBRICKPerformance() {
		double sumRR = 0;
		double sumAP = 0;
		double sumRec = 0;
		double sumAcc = 0;

		for (int bugID : this.queryMap.keySet()) {
			String singleQuery = this.queryMap.get(bugID);
			LuceneSearcher searcher = new LuceneSearcher(bugID, repoName,singleQuery);
			// setting TOP-K values
			searcher.TOPK_RESULTS = TOPK;
			double rr = 0, ap = 0, rec = 0;
			ArrayList<Integer> indices = searcher.getGoldFileIndicesClass();
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
				rec = getRecall(indices, searcher.goldset);
				if (rec > 0) {
					sumRec += rec;
				}
			}
		}

		// now calculate the mean performance
		this.TopkAcc = (double) sumAcc / this.queryMap.size();
		this.mrrK = sumRR / this.queryMap.size();
		this.mapK = sumAP / this.queryMap.size();
		this.mrK = sumRec / this.queryMap.size();

		// System.out.println(repoName + " " + this.TopkAcc);

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
	}
}
