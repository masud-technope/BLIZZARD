package brick.query;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;
import utility.SelectedBugs;
import config.StaticData;

public class STStatAnalyzer {

	String repoName;
	String stFolder;
	HashMap<Integer, Integer> stLengthMap;

	public STStatAnalyzer(String repoName) {
		this.repoName = repoName;
		this.stFolder = StaticData.BRICK_EXP + "/laura-moreno/stacktraces/"
				+ repoName;
		this.stLengthMap = new HashMap<>();
	}

	protected void analyzeStackTraces() {
		ArrayList<Integer> selectedBugs = SelectedBugs
				.getStackSelectedBugs(repoName);
		int stSumCount = 0;
		for (int bugID : selectedBugs) {
			String traceFile = this.stFolder + "/" + bugID + ".txt";
			ArrayList<String> lines = ContentLoader
					.getAllLinesOptList(traceFile);
			int stCount = lines.size();
			this.stLengthMap.put(bugID, stCount);
			stSumCount += stCount;
		}
		//System.out.println(repoName + "\t" + (stSumCount /
		//selectedBugs.size()));
	}

	public ArrayList<Integer> getST5() {
		this.analyzeStackTraces();
		ArrayList<Integer> selected = new ArrayList<>();
		for (int bugID : this.stLengthMap.keySet()) {
			int length = this.stLengthMap.get(bugID);
			if (length < 5) {
				selected.add(bugID);
			}
		}
		return selected;
	}
	
	public ArrayList<Integer> getSTK(int K)
	{
		this.analyzeStackTraces();
		ArrayList<Integer> selected = new ArrayList<>();
		for (int bugID : this.stLengthMap.keySet()) {
			int length = this.stLengthMap.get(bugID);
			if (length> K-5 && length <= K) {
				selected.add(bugID);
			}
		}
		return selected;
	}
	
	

	public ArrayList<Integer> getST10() {
		this.analyzeStackTraces();
		ArrayList<Integer> selected = new ArrayList<>();
		for (int bugID : this.stLengthMap.keySet()) {
			int length = this.stLengthMap.get(bugID);
			if (length >= 5 && length <= 10) {
				selected.add(bugID);
			}
		}
		return selected;
	}
	
	public ArrayList<Integer> getST20() {
		this.analyzeStackTraces();
		ArrayList<Integer> selected = new ArrayList<>();
		for (int bugID : this.stLengthMap.keySet()) {
			int length = this.stLengthMap.get(bugID);
			if (length >10 && length <=20) {
				selected.add(bugID);
			}
		}
		return selected;
	}
	

	public ArrayList<Integer> getSTMax() {
		this.analyzeStackTraces();
		ArrayList<Integer> selected = new ArrayList<>();
		for (int bugID : this.stLengthMap.keySet()) {
			int length = this.stLengthMap.get(bugID);
			if (length > 20 ) {
				selected.add(bugID);
			}
		}
		return selected;
	}

	public static void main(String[] args) {
		String[] repos = { "ecf", "eclipse.jdt.core", "eclipse.jdt.debug",
				"eclipse.jdt.ui", "eclipse.pde.ui", "log4j", "sling",
				"tomcat70" };
		int brcount=0;
		for (String repoName : repos) {
			STStatAnalyzer analyzer=new STStatAnalyzer(repoName);
			analyzer.analyzeStackTraces();
			//brcount+=analyzer.getST20().size();
			brcount+=analyzer.getSTMax().size();
		}
		System.out.println("Large traces:"+ brcount);
	}
}
