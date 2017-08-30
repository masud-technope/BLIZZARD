package brick.query.tester;

import java.util.ArrayList;
import java.util.HashMap;
import text.normalizer.TextNormalizer;
import utility.BugReportLoader;
import lucenecheck.LuceneSearcher;
import brick.query.BRICKQueryProvider;

public class BRICKQueryTester {

	String repoName;
	int bugID;
	String reportGroup;
	String STKey;
	boolean ss;

	public BRICKQueryTester(String repoName, int bugID, String reportGroup,
			String STKey, boolean ss) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.reportGroup = reportGroup;
		this.STKey = STKey;
		this.ss=ss;
	}

	protected String getBaselineQuery(String reportContent) {
		return new TextNormalizer(reportContent).normalizeSimple1024();
	}

	protected void testBRICKQuery() {
		BRICKQueryProvider bqProvider = new BRICKQueryProvider(this.repoName,
				this.bugID, this.reportGroup);
		String bquery = bqProvider.provideBRICKQuery();
		String baseline = getBaselineQuery(BugReportLoader.loadBugReport(repoName, bugID));

		LuceneSearcher brickSearcher = new LuceneSearcher(bugID, repoName,
				bquery);
		System.out.println("BRICK:"+bquery);
		System.out.println("BRICK rank:"
				+ brickSearcher.getFirstGoldRankClass());
		
		
		LuceneSearcher seacher0 = new LuceneSearcher(bugID, repoName, baseline);
		System.out.println("Baseline:"+baseline);
		System.out.println("Base rank:" + seacher0.getFirstGoldRankClass());		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		int bugID = 170796;
		String rg = "ST";
		String STKey="C";
		boolean ss=true;
		BRICKQueryTester bqTester=new BRICKQueryTester(repoName, bugID, rg, STKey,ss);
		bqTester.testBRICKQuery();
	}
}
