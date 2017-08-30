package brick.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utility.ContentLoader;
import config.StaticData;

public class BRDiagnosticsMgr {

	String repoName;
	int bugID;
	String reportGroup;
	String reportContent;
	TraceElemExtractor teExtractor = null;

	public BRDiagnosticsMgr(String repoName, int bugID, String reportGroup,
			String reportContent) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.reportGroup = reportGroup;
		this.reportContent = reportContent;
	}

	public BRDiagnosticsMgr(String repoName, int bugID, String reportGroup) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.reportGroup = reportGroup;
		this.reportContent = getBugReport(repoName, bugID);
	}

	protected String getTitle() {
		return reportContent.split("\n")[0];
	}

	protected String getDescription() {
		String[] lines = this.reportContent.split("\n");
		String temp = new String();
		for (int i = 1; i < lines.length; i++) {
			temp += lines[i] + "\n";
		}
		return temp.trim();
	}

	protected ArrayList<String> getTheTraces(String reportDesc) {
		// check for stack traces
		ArrayList<String> traces = new ArrayList<>();
		String stackRegex = "(.*)?(.+)\\.(.+)\\((.+)\\.java:\\d+|unknown source|native method\\)";
		Pattern p = Pattern.compile(stackRegex);
		Matcher m = p.matcher(reportDesc);
		while (m.find()) {
			String trace = reportDesc.substring(m.start(), m.end());
			// avoid duplication
			if (!traces.contains(trace)) {
				traces.add(trace);
			}
		}
		return traces;
	}
	
	
	protected ArrayList<String> getTheTracesV2() {
		String traceFile = StaticData.BRICK_EXP + "/BR-ST-StackTraces/"
				+ repoName + "/" + bugID + ".txt";
		ArrayList<String> traces = ContentLoader.getAllLinesList(traceFile);
		return traces;
	}

	protected HashSet<String> getExceptionMessages(String reportDesc) {
		HashSet<String> exceptions = new HashSet<>();
		String excepRegex = "(.)+Exception|(.)+Error";
		Pattern p = Pattern.compile(excepRegex);
		Matcher m = p.matcher(reportDesc);
		while (m.find()) {
			String exception = reportDesc.substring(m.start(), m.end());
			String[] parts = exception.split("\\p{Punct}+|\\d+|\\s+");
			// System.out.println(exception);
			for (String part : parts) {
				if (part.endsWith("Exception") || part.endsWith("Error")) {
					exceptions.add(part);
				}
			}
		}
		return exceptions;
	}
	

	public void performDiagnosis() {
		String title = getTitle();
		String description = getDescription();
		// ArrayList<String> traces = getTheTraces(description);
		ArrayList<String> traces = getTheTracesV2();
		System.out.println(title);
		// MiscUtility.showList(traces);
		teExtractor = new TraceElemExtractor(traces);
		teExtractor.decodeTraces(false);
		System.out.println(getExceptionMessages(description));
	}

	protected static String getBugReport(String repoName, int bugID) {
		String brFile = StaticData.BRICK_EXP + "/BugReport-Raw/" + repoName
				+ "/" + bugID + ".txt";
		return ContentLoader.loadFileContent(brFile);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		int bugID = 326221;
		String rg = "ST";
		//String STKey = "C";
		String reportContent = getBugReport(repoName, bugID);
		new BRDiagnosticsMgr(repoName, bugID, rg, reportContent)
				.performDiagnosis();
	}
}
