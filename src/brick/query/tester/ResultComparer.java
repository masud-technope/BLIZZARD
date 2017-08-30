package brick.query.tester;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;
import utility.ContentLoader;
import utility.MiscUtility;
import utility.SelectedBugs;

public class ResultComparer {

	String repoName;
	String baselineResultFile;
	String brickResultFile;
	String catKey;
	ArrayList<Integer> selectedBugs;
	HashMap<Integer, Integer> baseRankMap;
	HashMap<Integer, Integer> brickRankMap;
	static ArrayList<String> improvedList = new ArrayList<>();

	ArrayList<Integer> improvedRanks;
	ArrayList<Integer> worsenedRanks;
	ArrayList<Integer> persistedRanks;

	public ArrayList<Integer> improvedBugs;
	public ArrayList<Integer> worsenedBugs;

	public static double sumImproved = 0;
	public static double sumWorsened = 0;
	public static double sumPreserved = 0;

	int improved = 0;
	int worsened = 0;
	int preserved = 0;

	static int sampleIter = 0;

	public double improvedRatio = 0;
	public double worsenedRatio = 0;
	public double preservedRatio = 0;

	static int sumMRD = 0;
	public static int sumMRDImp = 0;
	public static int sumMRDWorse = 0;

	public static int datasetSize;
	int MRD = 0;
	static int goodBaseRank = 0;
	static int validBaseRank = 0;
	static int goodSTRICTRank = 0;
	static int validSTRICTRank = 0;

	public ResultComparer(String repoName, String baseResultFile,
			String brickResultFile, String catKey) {
		this.repoName = repoName;
		this.baselineResultFile = baseResultFile;
		this.brickResultFile = brickResultFile;
		this.baseRankMap = loadRankMap(this.baselineResultFile);
		this.brickRankMap = loadRankMap(this.brickResultFile);
		this.improvedRanks = new ArrayList<>();
		this.worsenedRanks = new ArrayList<>();
		this.persistedRanks = new ArrayList<>();

		this.improvedBugs = new ArrayList<>();
		this.worsenedBugs = new ArrayList<>();

		this.catKey = catKey;
		this.selectedBugs = new ArrayList<>();
		this.loadSelectedBugs(this.catKey);
	}

	protected HashMap<Integer, Integer> loadRankMap(String rankFile) {
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(rankFile);
		HashMap<Integer, Integer> rankMap = new HashMap<>();
		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			String[] parts = line.split("\\s+");
			int bugID = Integer.parseInt(parts[0].trim());
			int rank = Integer.parseInt(parts[1].trim());
			rankMap.put(bugID, rank);
		}
		return rankMap;
	}

	protected void loadSelectedBugs(String catKey) {
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

	public double compareResults() {
		// comparing our result with baseline
		int strictRankSum = 0;
		int baseRankSum = 0;
		int svcount = 0;
		int bvcount = 0;

		double strictRR = 0;
		double baseRR = 0;

		for (int bugID : this.selectedBugs) {
			// int bugID = Integer.parseInt(bug.trim());
			int baserank = this.baseRankMap.get(bugID);
			int brickRank = -1;
			if (brickRankMap.containsKey(bugID)) {
				brickRank = this.brickRankMap.get(bugID);
			}
			if (brickRank > 0) {
				strictRankSum += brickRank;
				svcount++;

				strictRR += (1.0 / brickRank);

				if (baserank > 0) {
					baseRankSum += baserank;
					bvcount++;

					baseRR += (1.0 / baserank);

					if (brickRank < baserank) {
						improved++;
						// this.improvedList.add(bugID + "");
						improvedBugs.add(bugID);
						// strictRankSum += strictRank;
						improvedRanks.add(brickRank);
					} else if (brickRank == baserank) {
						preserved++;
						persistedRanks.add(brickRank);
					} else if (brickRank > baserank) {
						worsened++;
						worsenedRanks.add(brickRank);
						worsenedBugs.add(bugID);
					}
				} else {
					if (brickRank > 0) {
						improved++;
						// strictRankSum += strictRank;
						improvedRanks.add(brickRank);
						// this.improvedList.add(bugID + "");
						improvedBugs.add(bugID);
					}
					// baseRankSum += NOT_FOUND;
				}
			} else {
				if (baserank == brickRank) {
					preserved++;
					persistedRanks.add(brickRank);
				} else {
					worsened++;
					baseRankSum += baserank;
					worsenedBugs.add(bugID);
				}
			}
		}

		this.improvedRatio = (double) improved / selectedBugs.size();
		//sumImproved += this.improvedRatio;
		// System.out.println("Improved:" + improvedRatio);
		this.worsenedRatio = (double) worsened / selectedBugs.size();
		//sumWorsened += this.worsenedRatio;
		// System.out.println("Worsened:" + worsenedRatio);
		this.preservedRatio = (double) preserved / selectedBugs.size();
		//sumPreserved += this.preservedRatio;
		// System.out.println("Preserved:" + preservedRatio);
		// System.err.println(repoName+"\t"+worsenedRatio);

		double mrr1 = strictRR / selectedBugs.size();
		double mrr2 = baseRR / selectedBugs.size();
		int meanRank1 = (int) (1.0 / mrr1);
		int meanRank2 = (int) (1.0 / mrr2);

		//sumMRD += (meanRank1 - meanRank2);
		//sumMRDImp += getMRDImprovedV2();
		//sumMRDWorse += getMRDWorsenedV2();
		//datasetSize += this.selectedBugs.size();
		
		System.out.println("Improved: "+ this.improvedRatio+ " MRD: "+getMRDImprovedV2());
		System.out.println("Worsened: "+ this.worsenedRatio+ " MRD: "+getMRDWorsenedV2());
		System.out.println("Preserved: "+ this.preservedRatio);

		return this.improvedRatio;
	}

	protected double getMRDImprovedV2() {
		int baseSum = 0;
		int bvcount = 0;
		for (int key : improvedBugs) {
			if (this.baseRankMap.containsKey(key)) {
				int baserank = this.baseRankMap.get(key);
				if (baserank > 0) {
					baseSum += baserank;
					bvcount++;
				}
			}
		}
		double baseRAvg = (double) baseSum / bvcount;
		double strictRAvg = MiscUtility.getItemAverageV2(this.improvedRanks);
		return strictRAvg - baseRAvg;
	}

	protected double getMRDWorsenedV2() {
		int baseSum = 0;
		int bvcount = 0;
		for (int key : worsenedBugs) {
			if (this.baseRankMap.containsKey(key)) {
				int baserank = this.baseRankMap.get(key);
				if (baserank > 0) {
					baseSum += baserank;
					bvcount++;
				}
			}
		}
		double baseRAvg = (double) baseSum / bvcount;
		double strictRAvg = MiscUtility.getItemAverageV2(this.worsenedRanks);
		return strictRAvg - baseRAvg;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "log4j";
		String catKey = "NL";
		String blizzardRankFile = StaticData.BRICK_EXP + "/BLIZZARD-QE/"
				+ repoName + "/BR-" + catKey + ".txt";
		String baseRankFile = StaticData.BRICK_EXP + "/Baseline-QE/" + repoName
				+ ".txt";
		ResultComparer comparer = new ResultComparer(repoName, baseRankFile,
				blizzardRankFile, catKey);
		comparer.compareResults();
	}
}
