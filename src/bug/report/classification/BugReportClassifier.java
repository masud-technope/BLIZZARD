package bug.report.classification;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utility.BugReportLoader;

public class BugReportClassifier {

	String reportContent;
	ArrayList<String> traces;

	public BugReportClassifier(String reportContent) {
		this.reportContent = reportContent;
		this.traces = new ArrayList<>();
	}

	public String determineReportClass() {
		ArrayList<String> traces = extractTraces(this.reportContent);
		if (!traces.isEmpty()) {
			this.traces = traces;
			return "ST";
		} else {
			ArrayList<String> invocations = extractMethodInvocations(this.reportContent);
			if (!invocations.isEmpty()) {
				return "PE";
			} else {
				return "NL";
			}
		}
	}

	public ArrayList<String> getTraces() {
		return this.traces;
	}

	protected ArrayList<String> extractMethodInvocations(String bugReport) {
		ArrayList<String> invocations = new ArrayList<>();
		String regex = "((\\w+)?\\.[\\s\\n\\r]*[\\w]+)[\\s\\n\\r]*(?=\\(.*\\))|([A-Z][a-z0-9]+){2,}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(bugReport);
		while (m.find()) {
			invocations.add(bugReport.substring(m.start(), m.end()));
		}
		return invocations;
	}

	protected ArrayList<String> extractTraces(String bugReport) {
		ArrayList<String> traces = new ArrayList<>();
		String stackRegex = "(.*)?(.+)\\.(.+)(\\((.+)\\.java:\\d+\\)|\\(Unknown Source\\)|\\(Native Method\\))";
		Pattern p = Pattern.compile(stackRegex);
		Matcher m = p.matcher(bugReport);
		while (m.find()) {
			String entry = bugReport.substring(m.start(), m.end());
			entry = cleanTheEntry(entry);
			traces.add(entry);
		}
		return traces;
	}

	protected String cleanTheEntry(String entry) {
		if (entry.indexOf("at ") >= 0) {
			int atIndex = entry.indexOf("at");
			entry = entry.substring(atIndex + 2).trim();
		}
		if (entry.contains("(")) {
			int leftBraceIndex = entry.indexOf("(");
			entry = entry.substring(0, leftBraceIndex);
		}
		return entry;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "ecf";
		int bugID = 264983;
		String content = BugReportLoader.loadBugReport(repoName, bugID);
		BugReportClassifier classifier = new BugReportClassifier(content);
		System.out.println(classifier.determineReportClass());
	}
}
