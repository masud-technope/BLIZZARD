package blizzard.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import config.StaticData;
import text.normalizer.TextNormalizer;
import utility.BugReportLoader;
import utility.ItemSorter;
import utility.MiscUtility;

public class BLIZZARDQueryProvider {

	String repoName;
	int bugID;
	String reportGroup;
	public String reportContent;
	public boolean hasException = false;

	public BLIZZARDQueryProvider(String repoName, int bugID,
			String reportContent) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.reportContent = reportContent;
	}

	protected ArrayList<String> getSalientItemsFromST(ArrayList<String> traces) {
		TraceClassesSelector tcSelector = new TraceClassesSelector(traces,
				false);
		HashMap<String, Double> itemMapC = tcSelector.getSalientClasses();
		return getTopKItems(itemMapC);
	}

	public String provideBRICKQuery() {
		BRDiagnosticsMgr diagnostics = new BRDiagnosticsMgr(repoName, bugID,
				this.reportContent);
		String title = diagnostics.getTitle();
		this.reportGroup = diagnostics.getReportClass();

		// System.out.println(title);
		ArrayList<String> salientItems = new ArrayList<>();
		switch (reportGroup) {
		case "ST":
			ArrayList<String> traces = diagnostics.traces;
			HashSet<String> exceptions = diagnostics
					.getExceptionMessages(this.reportContent);
			salientItems = getSalientItemsFromST(traces);
			if (!exceptions.isEmpty()) {
				hasException = true;
				salientItems.addAll(exceptions);
			}

			if (!title.isEmpty()) {
				String normTitle = new TextNormalizer(title).normalizeSimple();
				salientItems.add(normTitle);
			}

			break;
		case "NL":
			String description = diagnostics.getDescription();
			TextKeywordSelector kwSelector = new TextKeywordSelector(repoName,
					title, reportContent,
					StaticData.MAX_NL_SUGGESTED_QUERY_LEN);
			// salientItems = kwSelector.getSearchTermsWithRF();
			String extended = kwSelector
					.getSearchTermsWithCR(StaticData.MAX_NL_SUGGESTED_QUERY_LEN);
			salientItems = MiscUtility.str2List(extended);
			/*
			 * salientItems = getTopKItems(kwSelector
			 * .getSearchTermsWithExtension());
			 */
			break;
		case "PE":
			description = diagnostics.getDescription();
			PEKeywordSelector peSelector = new PEKeywordSelector(title, title
					+ "\n" + description, StaticData.MAX_PE_SUGGESTED_QUERY_LEN);
			salientItems = peSelector.getSearchTerms();
			break;
		default:
			break;
		}

		return MiscUtility.list2Str(salientItems);
	}

	protected String cleanEntity(String itemName) {
		String[] parts = itemName.split("\\p{Punct}+|\\s+|\\d+");
		return MiscUtility.list2Str(parts);
	}

	protected ArrayList<String> getTopKItems(HashMap<String, Double> tokendb) {
		List<Map.Entry<String, Double>> sorted = ItemSorter
				.sortHashMapDouble(tokendb);
		ArrayList<String> selected = new ArrayList<>();
		for (int i = 0; i < sorted.size(); i++) {
			Map.Entry<String, Double> entry = sorted.get(i);
			selected.add(entry.getKey());
			if (reportGroup.equals("ST")) {
				if (selected.size() == StaticData.MAX_ST_SUGGESTED_QUERY_LEN)
					break;
			}
		}
		return selected;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "eclipse.jdt.core";
		int bugID = 15036;
		String reportContent = BugReportLoader.loadBugReport(repoName, bugID);
		String refQuery = new BLIZZARDQueryProvider(repoName, bugID,
				reportContent).provideBRICKQuery();
		System.out.println("Reformulated: " + refQuery);
	}
}
