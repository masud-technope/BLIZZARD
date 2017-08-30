package brick.query.tester;

import config.StaticData;

public class BLIZZARDRankComparer {

	String repoName;
	String catKey;

	public BLIZZARDRankComparer(String repoName, String catKey) {
		this.repoName = repoName;
		this.catKey = catKey;
	}

	public void compareQE() {
		String blizzardRankFile = StaticData.BRICK_EXP + "/BLIZZARD-QE/"
				+ repoName + "/BR-" + catKey + ".txt";
		String baseRankFile = StaticData.BRICK_EXP + "/Baseline-QE/" + repoName
				+ ".txt";
		ResultComparer comparer = new ResultComparer(repoName, baseRankFile,
				blizzardRankFile, this.catKey);
		comparer.compareResults();
	}
}
