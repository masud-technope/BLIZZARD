package utility;

import java.util.ArrayList;
import config.StaticData;

public class SelectedBugs {

	public static ArrayList<Integer> getSelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/BugReport-Clusters/" + repoName
				+ "/BR-ALL" + ".txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	public static ArrayList<Integer> getStackSelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/BugReport-Clusters/" + repoName
				+ "/BR-ST.txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	public static ArrayList<Integer> getPESelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/BugReport-Clusters/" + repoName
				+ "/BR-PE.txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	public static ArrayList<Integer> getNLSelectedBugs(String repoName) {
		String bugFile = StaticData.BRICK_EXP + "/BugReport-Clusters/" + repoName
				+ "/BR-NL.txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}

	public static ArrayList<Integer> getSampledBugs(String repoName,
			int sampleNo) {
		String bugFile = StaticData.BRICK_EXP + "/BugReport-Clusters/" + repoName
				+ "/sampled-" + sampleNo + ".txt";
		return ContentLoader.getAllLinesInt(bugFile);
	}
}
