package lucenecheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import utility.ContentLoader;
import config.StaticData;

public class RepoSearchManager {

	String queryFile;
	String projectName;
	final int MAX_TOKEN_IN_QUERY = 5;
	HashMap<Integer, String> queryMap;
	public double topkacc = 0;
	ArrayList<Integer> selectedBugs;
	String selectedBugFile;
	String keymapFile;
	static int datasetSize = 0;
	HashMap<Integer, String> keyMap;
	static double sumAcc = 0;

	String queryFolder;
	boolean bugRelated;

	public RepoSearchManager(String queryFile, String projectName) {
		this.queryFile = queryFile;
		this.projectName = projectName;
	}

	public RepoSearchManager(HashMap<Integer, String> queryMap,
			String projectName) {
		this.projectName = projectName;
		this.queryMap = queryMap;
	}

	public RepoSearchManager(String repoName) {
		// active constructor
		this.projectName = repoName;

		this.selectedBugs = new ArrayList<>();
		this.keyMap = new HashMap<>();
		/*
		 * this.queryFile = StaticData.CODERANK_DATA + "/baseline-title/" +
		 * repoName + ".txt";
		 */
		this.queryFile = StaticData.BRICK_EXP + "/proposed-query/" + repoName
				+ "/" + "cc-exp-occur-V2-ext-top5-rq5-alpha-10.txt";

		this.keymapFile = StaticData.BRICK_EXP + "/corpus/" + repoName
				+ ".ckeys";
		this.selectedBugFile = StaticData.BRICK_EXP + "/filter/" + repoName
				+ "/selectedbugs-title-10-java-refined.txt";
		this.keyMap = this.loadKeyMaps();
		this.loadSelectedBugs();
	}

	public RepoSearchManager(String repoName, String queryFolder, boolean bug) {
		this.projectName = repoName;
		this.queryFolder = StaticData.BRICK_EXP + "/" + queryFolder + "/"
				+ repoName;
		this.bugRelated = bug;
		this.keymapFile = StaticData.BRICK_EXP + "/corpus/" + repoName
				+ ".ckeys";
		this.keyMap = this.loadKeyMaps();
		this.loadSelectedBugs();
	}

	protected String extractQuery(String line) {
		String temp = new String();
		String[] words = line.split("\\s+");
		for (int index = 1; index < words.length; index++) {
			temp += words[index] + "\t";
		}
		return temp;
	}

	protected HashMap<Integer, String> loadKeyMaps() {
		ArrayList<String> lines = ContentLoader
				.getAllLinesOptList(this.keymapFile);
		HashMap<Integer, String> keyMap = new HashMap<>();
		for (String line : lines) {
			int key = Integer.parseInt(line.split(":")[0].trim());
			keyMap.put(key, line);
		}
		return keyMap;
	}

	protected void loadSelectedBugs() {
		// loading selected bugs
		String selectedBugFile = StaticData.BRICK_EXP + "/selectedbug/"
				+ projectName + ".txt";
		this.selectedBugs = ContentLoader.getAllLinesInt(selectedBugFile);
	}
	
	protected String normalizeQuery(String query) {
		String[] words = query.split("\\s+");
		int lengthThreshold = 1023;
		String temp = new String();
		for (int i = 0; i < words.length; i++) {
			temp += words[i] + "\t";
			if (i == lengthThreshold)
				break;
		}
		return temp.trim();
	}
	

	public void collectSearchStatsBug() {
		// check the bug report performance
		try {

			int totalcase = 0;
			int foundcase = 0;
			int success = 0;

			if (bugRelated) {
				File[] files = new File(this.queryFolder).listFiles();
				for (File f : files) {
					
					int bugID = Integer.parseInt(f.getName().split("\\.")[0]);
					if(!selectedBugs.contains(bugID))continue;
					
					totalcase++;
					
					String searchQuery = ContentLoader.loadFileContent(f
							.getAbsolutePath());
					//limit the numbers
					searchQuery=normalizeQuery(searchQuery);
					
					LuceneSearcher searcher = new LuceneSearcher(bugID,
							this.projectName, searchQuery);
					ArrayList<String> results = searcher
							.performVSMSearchList(false);
					ResultResolver resolver = new ResultResolver(this.keyMap,
							results, true);
					ArrayList<String> resolvedResults = resolver
							.resolveLuceneResults();
					ArrayList<String> goldset = getGoldSetSVN(bugID);
					boolean found = checkFound(resolvedResults, goldset);
					if (found)
						success++;
				}
				sumAcc += (double) success / totalcase;

				System.out.println("Accuracy:" + (double) success / totalcase);
				System.out.println("Dataset:" + totalcase);
				datasetSize += totalcase;

			}
		} catch (Exception exc) {
			// handle the exception
		}
	}

	@Deprecated
	public void collectSearchStats() {
		// checking the query performance
		try {
			int totalcase = 0;
			int foundcase = 0;

			Scanner scanner = new Scanner(new File(this.queryFile));
			int success = 0;
			int sumIndex = 0;
			while (scanner.hasNext()) {
				try {
					String line = scanner.nextLine();
					// line=chooseTopK(line);
					String[] parts = line.trim().split("\\s+");

					if (parts.length == 1)
						continue;

					int bugID = Integer.parseInt(parts[0].trim());

					if (!this.selectedBugs.contains(bugID))
						continue;

					String query = extractQuery(line);

					LuceneSearcher searcher = new LuceneSearcher(bugID,
							this.projectName, query);
					ArrayList<String> results = searcher
							.performVSMSearchList(false);
					ResultResolver resolver = new ResultResolver(this.keyMap,
							results, true);
					ArrayList<String> resolvedResults = resolver
							.resolveLuceneResults();
					ArrayList<String> goldset = getGoldSetSVN(bugID);

					boolean found = checkFound(resolvedResults, goldset);
					if (found)
						success++;

				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			scanner.close();
			datasetSize += this.selectedBugs.size();

			System.out.println("Repo name:" + projectName);
			System.out.println("Average first index:" + (double) sumIndex
					/ this.selectedBugs.size());

			System.out.println("Total cases:	" + this.selectedBugs.size());
			System.out.println("Results found for:	" + success);
			System.out.println("Top-10 accuracy:	" + (double) success
					/ this.selectedBugs.size());

			sumAcc += (double) success / this.selectedBugs.size();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean checkFound(ArrayList<String> results,
			ArrayList<String> goldset) {
		// checking if gold set is available in the results
		for (String goldURL : goldset) {
			for (String resultURL : results) {
				if (resultURL.endsWith(goldURL)) {
					return true;
				}
			}
		}
		return false;
	}

	protected int getFirstResultIndex(ArrayList<String> results,
			ArrayList<String> goldset) {
		for (String goldURL : goldset) {
			int index = 0;
			for (String resultURL : results) {
				index++;
				if (resultURL.endsWith(goldURL)) {
					return index;
				}
			}
		}
		return -1;
	}

	protected ArrayList<String> getGoldSetSVN(int bugID) {
		// collecting gold set from SVN
		String goldFile = StaticData.BRICK_EXP + "/goldset/" + projectName
				+ "/gold/" + bugID + ".txt";
		ArrayList<String> goldset = new ArrayList<>();
		File f = new File(goldFile);
		if (f.exists()) { // if the solution file exists
			String content = ContentLoader.loadFileContent(goldFile);

			String[] items = content.split("\n");
			for (String item : items) {
				// convert to forward slash
				if (item.contains("/")) {
					String slashedItem = item.replace('/', '\\');
					goldset.add(slashedItem);
				} else
					goldset.add(item);
			}
		}
		return goldset;
	}

	public static void main(String[] args) {
		/*
		 * String[] repos = { "log4j", "eclipse.jdt.debug", "ecf",
		 * "eclipse.pde.ui", "sling", "tomcat70", "eclipse.jdt.ui",
		 * "eclipse.jdt.core" };
		 */

		/*
		 * String projectName = "eclipse.jdt.debug"; RepoSearchManager manager =
		 * null; manager = new RepoSearchManager(projectName, true);
		 * manager.collectSearchStatsBug();
		 */

		String[] repos = { "ecf", "eclipse.jdt.debug", "eclipse.jdt.core",
				"eclipse.jdt.ui","eclipse.pde.ui" };
		String queryFolder = "brick-query";

		for (String projectName : repos) {
			System.out.println(projectName);
			RepoSearchManager manager = new RepoSearchManager(projectName,
					queryFolder, true);
			manager.collectSearchStatsBug();
		}

		System.out.println("Mean accuracy:" + (sumAcc / repos.length));
		System.out.println("Total dataset:" + datasetSize);
	}
}
