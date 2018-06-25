package lucenecheck;

import java.io.File;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.TokenMgrError;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import config.StaticData;
import utility.ContentLoader;

public class LuceneSearcher {

	int bugID;
	String repository;
	String indexFolder;
	String field = "contents";
	String queries = null;
	int repeat = 0;
	boolean raw = true;
	String queryString = null;
	int hitsPerPage = 10;
	String searchQuery;
	public int TOPK_RESULTS = 10;
	int ALL_RESULTS = 100000;
	ArrayList<String> results;
	public ArrayList<String> goldset;
	IndexReader reader = null;
	IndexSearcher searcher = null;
	Analyzer analyzer = null;

	public double precision = 0;
	public double recall = 0;
	public double precatk = 0;
	public double recrank = 0;

	public LuceneSearcher(int bugID, String repository, String searchQuery) {
		// initialization
		this.bugID = bugID;
		this.repository = repository;
		// checking the class-based index
		this.indexFolder = config.StaticData.BRICK_EXP + "/Lucene-Index/"
				+ repository;
		this.searchQuery = searchQuery;
		this.results = new ArrayList<>();
		this.goldset = new ArrayList<>();
	}

	public LuceneSearcher(String indexFolder, String searchQuery) {
		this.indexFolder = indexFolder;
		this.searchQuery = searchQuery;
		this.results = new ArrayList<>();
	}

	public ArrayList<String> performVSMSearchList(boolean all) {
		// performing LUECNE search
		boolean validcase = false;
		try {
			if (reader == null)
				reader = DirectoryReader.open(FSDirectory.open(new File(
						indexFolder).toPath()));
			if (searcher == null)
				searcher = new IndexSearcher(reader);
			if (analyzer == null)
				analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser(field, analyzer);

			if (!searchQuery.isEmpty()) {
				Query myquery = parser.parse(searchQuery);
				TopDocs results = searcher.search(myquery, ALL_RESULTS);
				ScoreDoc[] hits = results.scoreDocs;
				if (!all) {
					int len = hits.length < TOPK_RESULTS ? hits.length
							: TOPK_RESULTS;
					for (int i = 0; i < len; i++) {
						ScoreDoc item = hits[i];
						Document doc = searcher.doc(item.doc);
						String fileURL = doc.get("path");
						fileURL = fileURL.replace('\\', '/');
						this.results.add(fileURL);
						//System.out.print(item.score+", ");
					}
				} else {
					for (int i = 0; i < hits.length; i++) {
						ScoreDoc item = hits[i];
						Document doc = searcher.doc(item.doc);
						String fileURL = doc.get("path");
						fileURL = fileURL.replace('\\', '/');
						this.results.add(fileURL);
						
					}
				}
				// checking the gold set
				try {
					validcase = getGoldSet();
				} catch (Exception exc) {
					// handle the exception
				}
			}
		} catch (Exception exc) {
			//exc.printStackTrace();
		} catch(TokenMgrError err){
			//handle the exception
		}
		return this.results;
	}

	protected boolean getGoldSet() {
		// collecting gold set results
		boolean gsfound = true;
		String goldFile = StaticData.BRICK_EXP + "/Goldset/" + repository
				+ "/" + bugID + ".txt";
		// clear the old values
		if (!this.goldset.isEmpty())
			this.goldset.clear();

		File f = new File(goldFile);
		if (f.exists()) { // if the solution file exists
			String content = ContentLoader.loadFileContent(goldFile);
			String[] items = content.split("\n");
			for (String item : items) {
				if (!item.trim().isEmpty())
					this.goldset.add(item);
			}
		} else {
			gsfound = false;
			// System.out.println("Solution not listed");
		}
		return gsfound;
	}

	protected boolean getGoldSetSVN() {
		// collecting gold set from SVN
		boolean gsfound = true;
		String goldFile = StaticData.BRICK_EXP + "/Goldset/" + repository
				+ "/" + bugID + ".txt";

		File f = new File(goldFile);
		if (f.exists()) { // if the solution file exists
			String content = ContentLoader.loadFileContent(goldFile);
			String[] items = content.split("\n");
			for (String item : items) {
				String trunk = "trunk/";
				int trIndex = item.indexOf(trunk) + 6;
				String truncatedItem = item.substring(trIndex);
				if (!truncatedItem.trim().isEmpty())
					this.goldset.add(truncatedItem);
			}
		} else {
			gsfound = false;
			// System.out.println("Solution not listed");
		}
		return gsfound;
	}

	protected void showGoldSet() {
		// showing the actual solutions
		String goldFile = StaticData.BRICK_EXP + "/goldset/" + repository
				+ "/gold/" + bugID + ".txt";
		File f = new File(goldFile);
		if (f.exists()) { // if the solution file exists
			String content = ContentLoader.loadFileContent(goldFile);
			System.out.println("===========Gold solution===========");
			System.out.println(content);
		} else {
			System.out.println("Solution not listed");
		}
	}

	public ArrayList<Integer> getGoldFileIndicesClass() {
		ArrayList<Integer> foundIndices = new ArrayList<>();
		this.results = performVSMSearchList(false);
		// System.out.println("TopK:" + this.results.size());
		try {
			ClassResultRankMgr rankFinder = new ClassResultRankMgr(repository,
					results, goldset);
			ArrayList<Integer> indices = rankFinder
					.getCorrectRanksDotted(TOPK_RESULTS);
			if (!indices.isEmpty()) {
				foundIndices.addAll(indices);
			}
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
		return foundIndices;
	}
	
	
	
	
	public int getFirstGoldRankClass() {
		this.results = performVSMSearchList(true);
		int foundIndex = -1;
		try {
			ClassResultRankMgr rankFinder = new ClassResultRankMgr(repository,
					results, goldset);
			foundIndex = rankFinder.getFirstGoldRank();
		} catch (Exception exc) {
			// handle the exception
		}
		return foundIndex;
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		int bugID = 241394;
		String repository = "eclipse.jdt.debug";
		//String searchQuery = "Bug   –  preferences  Mark Occurences Pref Page  Link  Build ID       Steps  Reproduce  There should  link   pref page    change the color  Namely  General Editors Text Editors Annotations  pain  find  pref  know Eclipse  preference structure well  More information compliance create preference add configuration field dialog annotation";
		String searchQuery="Can not get input text properly in eclipse console panel";
		LuceneSearcher searcher = new LuceneSearcher(bugID, repository,
				searchQuery);

		System.out.println("First found index:"
				+ searcher.getFirstGoldRankClass());
		long end = System.currentTimeMillis();
		System.out.println("Time:" + (end - start) / 1000 + "s");
	}
}
