package blizzard.query.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import lucenecheck.LuceneSearcher;
import utility.ContentWriter;
import utility.QueryLoader;
import utility.SelectedBugs;
import config.StaticData;

public class BRICKRankProvider {

	String repoName;
	String repoRankFile;
	String queryFile;
	ArrayList<Integer> selectedBugs;
	String catKey;

	public BRICKRankProvider(String repoName, String catKey) {
		this.repoName = repoName;
		this.catKey = catKey;
		this.repoRankFile = StaticData.BRICK_EXP + "/Rank/" + repoName + "-"
				+ catKey + ".txt";
		this.selectedBugs = new ArrayList<>();
		this.queryFile = StaticData.BRICK_EXP + "/Query/" + repoName + "-"
				+ catKey + ".txt";
		this.loadSelectedBugs();
	}

	protected void loadSelectedBugs() {
		// loading selected bugs
		switch (catKey) {
		case "ST":
			selectedBugs = SelectedBugs.getStackSelectedBugs(repoName);
			break;
		case "PE":
			selectedBugs = SelectedBugs.getPESelectedBugs(repoName);
			break;
		case "NL":
			selectedBugs = SelectedBugs.getNLSelectedBugs(repoName);
			break;
		case "ALL":
			selectedBugs = SelectedBugs.getSelectedBugs(repoName);
			break;
		default:
			break;
		}
	}

	protected String normalizeQuery(String query) {
		String[] words = query.split("\\s+");
		int lengthThreshold = StaticData.MAX_QUERY_LEN;
		String temp = new String();
		for (int i = 0; i < words.length; i++) {
			temp += words[i] + "\t";
			if (i == lengthThreshold)
				break;
		}
		return temp.trim();
	}

	protected void collectBRICKRanks() {
		// collect BRICK ranks
		HashMap<Integer, String> queryMap = QueryLoader
				.loadQuery(this.queryFile);
		ArrayList<String> ranks = new ArrayList<>();
		for (int bugID : queryMap.keySet()) {
			if (selectedBugs.contains(bugID)) {
				try {
					String searchQuery = queryMap.get(bugID);
					searchQuery = normalizeQuery(searchQuery);
					LuceneSearcher searcher = new LuceneSearcher(bugID,
							repoName, searchQuery);
					int firstGoldIndex = searcher.getFirstGoldRankClass();
					ranks.add(bugID + "\t" + firstGoldIndex);
				} catch (Exception exc) {
					// handle the exception
				}
			}
		}
		// now save the ranks
		ContentWriter.writeContent(repoRankFile, ranks);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		String repoName = "log4j";
		String catKey = "NL";
		BRICKRankProvider brickRankProvider = new BRICKRankProvider(repoName,
				catKey);
		brickRankProvider.collectBRICKRanks();
		System.out.println("Rank collected:" + repoName);
		long end = System.currentTimeMillis();
		System.out.println("Time needed:" + (end - start) / 1000 + " s");
	}
}
