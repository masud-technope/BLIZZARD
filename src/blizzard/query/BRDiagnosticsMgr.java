package blizzard.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bug.report.classification.BugReportClassifier;
import utility.ContentLoader;
import config.StaticData;

public class BRDiagnosticsMgr {

	String repoName;
	int bugID;
	String reportGroup = "NL";
	String reportContent;
	ArrayList<String> traces;

	public BRDiagnosticsMgr(String repoName, int bugID, String reportContent) {
		this.repoName = repoName;
		this.bugID = bugID;
		this.traces = new ArrayList<>();
		this.reportContent = reportContent;
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

	public ArrayList<String> getTheTracesLocal() {
		String traceFile = StaticData.BRICK_EXP + "/laura-moreno/stacktraces/"
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

	protected String getReportClass() {
		BugReportClassifier classifier = new BugReportClassifier(
				this.reportContent);
		String rClass = classifier.determineReportClass();
		if (rClass.equals("ST")) {
			this.traces = classifier.getTraces();
		}
		return rClass;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		int bugID = 326221;
		String rg = "ST";
		// String STKey = "C";
	}
}
