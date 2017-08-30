package brick.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.StaticData;
import core.SearchTermProvider;
import text.normalizer.TextNormalizer;
import utility.ItemSorter;
import utility.MiscUtility;

public class BRICKQueryProvider {

	String repoName;
	int bugID;
	String reportGroup;
	String STKey;
	public String reportContent;
	public boolean hasException = false;
	boolean canonical = false;
	ArrayList<String> exceptions;

	public BRICKQueryProvider(String repoName, int bugID, String reportGroup,
			String STKey, ArrayList<String> exceptions) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.reportGroup = reportGroup;
		this.STKey = STKey;
		this.exceptions = exceptions;
	}

	public BRICKQueryProvider(String repoName, int bugID, String reportGroup) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.reportGroup = reportGroup;
	}

	protected ArrayList<String> getSalientItemsFromST(ArrayList<String> traces) {
		switch (this.STKey) {
		case "M":
			TraceMethodsSelector tmSelector = new TraceMethodsSelector(traces,
					this.canonical);
			HashMap<String, Double> itemMapM = tmSelector.getSalientMethods();
			return getTopKItems(itemMapM);
		case "C":
			TraceClassesSelector tcSelector = new TraceClassesSelector(traces,
					this.canonical);
			HashMap<String, Double> itemMapC = tcSelector.getSalientClasses();
			return getTopKItems(itemMapC);
		}
		return null;
	}

	public String provideBRICKQuery() {
		BRDiagnosticsMgr diagnostics = new BRDiagnosticsMgr(repoName, bugID,
				reportGroup);
		String title = diagnostics.getTitle();
		// System.out.println(title);
		ArrayList<String> salientItems = new ArrayList<>();
		switch (reportGroup) {
		case "ST":
			this.STKey = "C";
			ArrayList<String> traces = diagnostics.getTheTracesV2();
			salientItems = getSalientItemsFromST(traces);
			// add the exception
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
					title, title + "\n" + description,
					StaticData.MAX_NL_QUERY_LEN);
			String extended = kwSelector
					.getSearchTermsWithCR(StaticData.MAX_NL_QUERY_LEN);
			salientItems = MiscUtility.str2List(extended);
			break;
		case "PE":
			description = diagnostics.getDescription();
			PEKeywordSelector peSelector = new PEKeywordSelector(title, title
					+ "\n" + description, StaticData.MAX_SUGGESTED_QUERY_LEN);
			salientItems = peSelector.getSearchTerms();
			break;
		default:
			break;
		}

		return MiscUtility.list2Str(salientItems);
	}

	public String provideSTRICTQuery(int TOPKQTERMS) {
		BRDiagnosticsMgr diagnostics = new BRDiagnosticsMgr(repoName, bugID,
				reportGroup);
		String title = diagnostics.getTitle();
		String description = diagnostics.getDescription();
		SearchTermProvider sProvider = new SearchTermProvider(title,
				description, TOPKQTERMS);
		String sQuery = sProvider.provideSearchTerms();
		String[] qterms = sQuery.split("\\s+");
		int count = 0;
		String strictQuery = new String();
		for (String qterm : qterms) {
			strictQuery += qterm + "\t";
			count++;
			if (count == TOPKQTERMS) {
				break;
			}
		}
		return strictQuery;
	}

	public String provideACERQuery(int TOPKQTERMS) {
		BRDiagnosticsMgr diagnostics = new BRDiagnosticsMgr(repoName, bugID,
				reportGroup);
		String title = diagnostics.getTitle();
		String description = diagnostics.getDescription();
		TextKeywordSelector selector = new TextKeywordSelector(repoName, title,
				description, TOPKQTERMS);
		String aQuery = selector.getSearchTermsWithCR(TOPKQTERMS);
		String[] qterms = aQuery.split("\\s+");
		int count = 0;
		String acerQuery = new String();
		for (String qterm : qterms) {
			acerQuery += qterm + "\t";
			count++;
			if (count == TOPKQTERMS) {
				break;
			}
		}
		return acerQuery;
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
				if (selected.size() == StaticData.MAX_ST_THRESHOLD)
					break;
			} else {
				if (selected.size() == StaticData.MAX_SUGGESTED_QUERY_LEN)
					break;
			}
		}
		return selected;
	}

	protected ArrayList<String> getTopKItemsbyDOI(String doiKey,
			ArrayList<String> traces) {
		ArrayList<String> topkItems = new ArrayList<>();
		for (String trace : traces) {
			String[] parts = trace.split("\\.");
			int length = parts.length;

			// avoid this line
			if (length < 2)
				continue;

			String methodName = parts[length - 1];
			String className = parts[length - 2];
			switch (doiKey) {
			case "DOI-M":
				methodName = cleanEntity(methodName);
				topkItems.add(methodName);
				break;
			case "DOI-C":
				className = cleanEntity(className);
				topkItems.add(className);
				break;
			}
			if (topkItems.size() == StaticData.DOI_TOPK_THRESHOLD) {
				break;
			}
		}
		return topkItems;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		int bugID = 264527;
		String reportGroup = "ST";
		BRICKQueryProvider brickProvider = new BRICKQueryProvider(repoName,
				bugID, reportGroup);
		String brickQuery = brickProvider.provideBRICKQuery();
		System.out.println(brickQuery);
	}
}
