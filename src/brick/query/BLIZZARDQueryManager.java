package brick.query;

import java.util.ArrayList;
import java.util.HashMap;
import utility.BugReportLoader;
import utility.ContentLoader;

public class BLIZZARDQueryManager {

	String repoName;
	String bugIDFile;
	HashMap<Integer, String> reportMap;
	HashMap<Integer, String> suggestedQueryMap;

	public BLIZZARDQueryManager(String repoName, String bugIDFile) {
		this.repoName = repoName;
		this.bugIDFile = bugIDFile;
		this.suggestedQueryMap = new HashMap<>();
		this.reportMap = loadReportMap();
	}

	protected HashMap<Integer, String> loadReportMap() {
		ArrayList<Integer> bugs = ContentLoader.getAllLinesInt(this.bugIDFile);
		HashMap<Integer, String> reportMap = new HashMap<>();
		for (int bugID : bugs) {
			String reportContent = BugReportLoader.loadBugReport(repoName,
					bugID);
			reportMap.put(bugID, reportContent);
		}
		return reportMap;
	}

	public HashMap<Integer, String> getSuggestedQueries() {
		System.out.println("Query reformulation may take a few minutes. Please wait...");
		for (int bugID : this.reportMap.keySet()) {
			String reportContent = this.reportMap.get(bugID);
			BLIZZARDQueryProvider provider = new BLIZZARDQueryProvider(
					this.repoName, bugID, reportContent);
			String suggestedQuery = provider.provideBRICKQuery();
			System.out.println("Done: "+bugID);
			this.suggestedQueryMap.put(bugID, suggestedQuery);
		}
		System.out.println("Query Reformulation completed successfully :-)");
		return this.suggestedQueryMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		String bugIDFile = "./input/bugs.txt";
		System.out.println(new BLIZZARDQueryManager(repoName, bugIDFile)
				.getSuggestedQueries());
	}
}
