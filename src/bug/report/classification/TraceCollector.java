package bug.report.classification;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import config.StaticData;
import utility.ContentLoader;
import utility.MiscUtility;

public class TraceCollector {

	String traceFile;

	public TraceCollector(String traceFile) {
		this.traceFile = traceFile;
	}

	protected ArrayList<String> extractStackTraces(String bugReport) {
		// extract the stack traces from the bug report
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

	protected ArrayList<String> collectTraceEntries() {
		String bugReport = ContentLoader.loadFileContent(this.traceFile);
		ArrayList<String> traces = extractStackTraces(bugReport);
		// MiscUtility.showList(traces);
		return traces;
	}

	protected String cleanTheProse(String prose) {
		// clean the prose texts
		prose = prose.replaceAll("at ", "\t");
		// remove the file name
		String fileNameRegex = "\\((.+)\\.java:\\d+\\)|\\(Unknown Source\\)|\\(Native Method\\)";
		prose = prose.replaceAll(fileNameRegex, "\t");
		return prose;
	}

	protected String collectTraceProse() {
		// collect trace prose from bug report
		String bugReport = ContentLoader.loadFileContent(this.traceFile);
		ArrayList<String> traces = extractStackTraces(bugReport);
		for (String trace : traces) {
			bugReport = bugReport.replaceFirst(trace, "\t");
		}
		return cleanTheProse(bugReport);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "sling";
		int bugID = 4955;
		String traceFile = StaticData.BRICK_EXP + "/changereqs/" + repoName
				+ "/reqs/" + bugID + ".txt";
		String prose = new TraceCollector(traceFile).collectTraceProse();
		System.out.println(prose);
	}
}
