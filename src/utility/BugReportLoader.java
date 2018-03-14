package utility;

import config.StaticData;

public class BugReportLoader {
	public static String loadBugReport(String repoName, int bugID) {
		String brFile = StaticData.BRICK_EXP + "/BR-Raw/" + repoName
				+ "/" + bugID + ".txt";
		return ContentLoader.loadFileContent(brFile);
	}
}
