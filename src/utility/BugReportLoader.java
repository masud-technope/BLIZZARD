package utility;

import config.StaticData;

public class BugReportLoader {
	public static String loadBugReport(String repoName, int bugID) {
		String brFile = StaticData.BRICK_EXP + "/changereqs/" + repoName
				+ "/reqs/" + bugID + ".txt";
		return ContentLoader.loadFileContent(brFile);
	}
}
