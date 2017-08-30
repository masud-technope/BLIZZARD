package brick.query;

import java.util.ArrayList;
import utility.MiscUtility;
import core.SearchTermProvider;

public class PEKeywordSelector {

	String title;
	String bugDesc;
	int TOPK;

	public PEKeywordSelector(String title, String bugDesc, int TOPK) {
		this.title = title;
		this.bugDesc = bugDesc;
		this.TOPK = TOPK;
	}
	
	public ArrayList<String> getSearchTerms() {
		SearchTermProvider keywordProvider = new SearchTermProvider(this.title,
				this.bugDesc, TOPK, false);
		String termStr = keywordProvider.provideSearchTerms();
		ArrayList<String> searchTerms = MiscUtility.str2List(termStr);
		ArrayList<String> keywords = new ArrayList<>();
		for (String sterm : searchTerms) {
			if (sterm.length() >= 3) {
				sterm = sterm.toLowerCase();
				keywords.add(sterm);
				if (keywords.size() == TOPK) {
					break;
				}
			}
		}
		return keywords;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
