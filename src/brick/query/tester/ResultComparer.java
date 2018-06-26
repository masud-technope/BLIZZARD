package brick.query.tester;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;
import utility.ContentWriter;
import config.StaticData;

public class ResultComparer {

	String repoName;
	String baselineResultFile;
	String strictResultFile;
	ArrayList<Integer> selectedBugs;
	HashMap<Integer, Integer> baseRankMap;
	HashMap<Integer, Integer> strictRankMap;

	int improved = 0;
	int worsened = 0;
	int preserved = 0;

	public double improvedRatio = 0;
	public double worsenedRatio = 0;
	public double preservedRatio = 0;

	public ResultComparer(String repoName, String baseResultFile,
			String strictResultFile, boolean includedInSuite) {
		this.repoName = repoName;
		this.baselineResultFile = baseResultFile;
		this.strictResultFile = strictResultFile;

		this.baseRankMap = new HashMap<>();
		this.strictRankMap = new HashMap<>();

		this.selectedBugs = getSelectedBugs(this.strictResultFile);

		this.strictRankMap = loadQE(this.strictResultFile);
		this.baseRankMap = loadQE(baseResultFile);
	}

	protected ArrayList<Integer> getSelectedBugs(String proposedQEFile) {
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(proposedQEFile);
		ArrayList<Integer> temp = new ArrayList<>();
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			if (parts.length == 2) {
				temp.add(Integer.parseInt(parts[0].trim()));
			}
		}
		return temp;
	}

	protected HashMap<Integer, Integer> loadQE(String toolRankFile) {
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(toolRankFile);
		HashMap<Integer, Integer> tempMap = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			if (parts.length == 2) {
				int bugID = Integer.parseInt(parts[0].trim());
				int qe = Integer.parseInt(parts[1].trim());
				if (this.selectedBugs.contains(bugID)) {
					tempMap.put(bugID, qe);
				}
			}
		}
		return tempMap;
	}

	public void compareResults() {
		// comparing our result with baseline

		int strictRankSum = 0;
		int baseRankSum = 0;
		int svcount = 0;
		int bvcount = 0;

		for (int bugID : this.selectedBugs) {

			int baserank = this.baseRankMap.get(bugID);
			int strictRank = -1;
			if (strictRankMap.containsKey(bugID)) {
				strictRank = this.strictRankMap.get(bugID);
			}
			if (strictRank > 0) {
				strictRankSum += strictRank;
				svcount++;

				if (baserank > 0) {

					baseRankSum += baserank;
					bvcount++;

					if (strictRank < baserank) {
						improved++;
					} else if (strictRank == baserank) {
						preserved++;
					} else if (strictRank > baserank) {
						worsened++;
					}
				} else {
					if (strictRank > 0) {
						improved++;
					}
				}
			} else {
				if (baserank == strictRank) {
					preserved++;
				} else {
					worsened++;
					baseRankSum += baserank;
				}
			}
		}

		this.improvedRatio = (double) improved / selectedBugs.size();
		this.worsenedRatio = (double) worsened / selectedBugs.size();
		this.preservedRatio = (double) preserved / selectedBugs.size();

		System.out.println("System:" + repoName);
		System.out.println("Improved: " + improved + "/" + selectedBugs.size()
				+ " =  " + this.improvedRatio);
		System.out.println("Worsened: " + worsened + "/" + selectedBugs.size()
				+ " =  " + this.worsenedRatio);
		System.out.println("Preserved: " + preserved + "/"
				+ selectedBugs.size() + " =  " + this.preserved);

	}

	public double getImproved() {
		return this.improved;
	}

	public double getWorsened() {
		return this.worsened;
	}

	public double getPreserved() {
		return this.preserved;
	}

	public double getSelectedBug() {
		return this.selectedBugs.size();
	}
}
