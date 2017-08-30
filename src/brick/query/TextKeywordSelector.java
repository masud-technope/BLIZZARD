package brick.query;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import coderank.query.expansion.CodeRankQueryExpansionProvider;
import config.StaticData;
import lucenecheck.LuceneSearcher;
import samurai.splitter.SamuraiSplitter;
import text.normalizer.TextNormalizer;
import token.manager.TokenTracebackManager;
import utility.ContentLoader;
import utility.ItemSorter;
import utility.MiscUtility;
import core.SearchTermProvider;

public class TextKeywordSelector {

	String title;
	String bugDesc;
	int TOPK;
	String repoName;


	public TextKeywordSelector(String repoName, String title, String bugDesc,
			int TOPK) {
		this.repoName = repoName;
		this.title = title;
		this.bugDesc = bugDesc;
		this.TOPK = TOPK;
	}
	
	@Deprecated
	protected ArrayList<String> performSanitization(ArrayList<String> terms) {
		// perform some sanitization on the query
		SamuraiSplitter ssplitter = new SamuraiSplitter(terms);
		HashMap<String, String> expanded = ssplitter.getSplittedTokenMap();
		HashSet<String> uniques = new HashSet<>();
		for (String key : expanded.keySet()) {
			String expandedSingle = expanded.get(key);
			if (expandedSingle.trim().length() > key.length()) {
				terms.addAll(MiscUtility.str2List(expandedSingle.toLowerCase()));
			} else {
				uniques.add(key.toLowerCase());
			}
		}
		return new ArrayList<String>(uniques);
	}

	@Deprecated
	protected ArrayList<String> removeDuplicates(ArrayList<String> keywords) {
		// remove duplicate keywords
		ArrayList<String> flagged = new ArrayList<>();
		for (int i = 0; i < keywords.size(); i++) {
			String target = keywords.get(i);
			String first = keywords.get(i).toLowerCase();
			for (int j = 0; j < keywords.size(); j++) {
				if (i == j)
					continue;
				String second = keywords.get(j).toLowerCase();
				if (first.startsWith(second) || first.endsWith(second)) {
					flagged.add(target);
					break;
				}
			}
		}
		keywords.removeAll(flagged);
		return keywords;
	}

	@Deprecated
	protected ArrayList<String> refineTokens(ArrayList<String> keywords) {
		ArrayList<String> refined = new ArrayList<>();
		TokenTracebackManager manager = new TokenTracebackManager();
		for (String keyword : keywords) {
			String rkeyword = manager.tracebackToken(keyword.toLowerCase());
			// if (!refined.contains(rkeyword)) {
			refined.add(rkeyword);
			// }
		}
		return refined;
	}

	@Deprecated
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

	public String getSearchTermsWithCR(int expansionSize) {
		
		String indexFolder = StaticData.BRICK_EXP + "/Lucene-Index/"
				+ repoName;
		String corpusFolder = StaticData.BRICK_EXP + "/Corpus/"
				+ repoName;
		String normDesc=new TextNormalizer(this.bugDesc).normalizeText();
		
		//title  as the search query produced the best result
		CodeRankQueryExpansionProvider expander=new 
				CodeRankQueryExpansionProvider(repoName, 0, this.title, indexFolder, corpusFolder);

		String  sourceTerms=expander.getCRExtension(expansionSize);
		
		return  normDesc+"\t"+sourceTerms;
		
	}

	
	protected ArrayList<String> getMiniCorpus(ArrayList<String> results) {
		ArrayList<String> corpusLines = new ArrayList<>();
		for (String fileURL : results) {
			String fileName = new File(fileURL).getName();
			String srcURL = StaticData.BRICK_EXP + "/corpus/norm-class/"
					+ repoName + "/" + fileName;
			String srcContent = ContentLoader.loadFileContent(srcURL);
			// ArrayLis<String> lines=ContentLoader.getAllLinesOptList(srcURL);
			corpusLines.add(srcContent);
		}
		return corpusLines;
	}

	protected ArrayList<String> getTopK(String itemStr, int TopK) {
		ArrayList<String> candidates = new ArrayList<>();
		ArrayList<String> items = MiscUtility.str2List(itemStr);
		for (String sterm : items) {
			if (sterm.length() >= 3) {
				sterm = sterm.toLowerCase();
				candidates.add(sterm);
				if (candidates.size() == TOPK) {
					break;
				}
			}
		}
		return candidates;
	}

	@Deprecated
	public ArrayList<String> getSearchTermsWithRF() {
		SearchTermProvider keywordProvider = new SearchTermProvider(this.title,
				this.bugDesc, TOPK, false);
		String termStr = keywordProvider.provideSearchTerms();
		ArrayList<String> candidates = getTopK(termStr, 10);
		LuceneSearcher searcher = new LuceneSearcher(0, repoName,
				MiscUtility.list2Str(candidates));
		ArrayList<String> results = searcher.performVSMSearchList(false);
		ArrayList<String> corpusLines = getMiniCorpus(results);

		/*
		 * SearchTermProvider sProvider = new SearchTermProvider(termStr,
		 * MiscUtility.list2Str(corpusLines), TOPK, true); String extended =
		 * sProvider.provideSearchTerms();
		 */

		HashMap<String, Integer> masterMap = new HashMap<>();
		for (String cline : corpusLines) {
			HashMap<String, Integer> wordmap = MiscUtility.wordcount(cline);
			for (String key : wordmap.keySet()) {
				if (masterMap.containsKey(key)) {
					int count = wordmap.get(key) + masterMap.get(key);
					masterMap.put(key, count);
				} else {
					masterMap.put(key, wordmap.get(key));
				}
			}
		}

		ArrayList<String> extension = new ArrayList<String>();
		List<Map.Entry<String, Integer>> sorted = ItemSorter
				.sortHashMapInt(masterMap);
		for (Map.Entry<String, Integer> entry : sorted) {
			String sterm = entry.getKey();
			if (sterm.length() >= 3) {
				sterm = sterm.toLowerCase();
				extension.add(sterm);
				if (extension.size() == TOPK) {
					break;
				}
			}
		}

		// add the initial keywords
		// candidates.addAll(extension);
		return extension;
		// candidates;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
