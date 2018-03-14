package bug.report.classification;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.SelectedBugs;

public class StackTraceExtractor {

	String repoName;
	ArrayList<Integer> selectedBugs;
	String traceFolder;

	public StackTraceExtractor(String repoName) {
		this.repoName = repoName;
		this.selectedBugs = SelectedBugs.getStackSelectedBugs(repoName);
		this.traceFolder = StaticData.BRICK_EXP + "/laura-moreno/stacktraces/"
				+ repoName;
	}

	protected ArrayList<String> extractStackTraces(String bugReport) {
		// extract the stack traces from the bug report
		ArrayList<String> traces = new ArrayList<>();
		String stackRegex = "(.*)?(.+)\\.(.+)\\((.+)\\.java:\\d+|unknown source|native method\\)";
		Pattern p = Pattern.compile(stackRegex);
		Matcher m = p.matcher(bugReport);
		while (m.find()) {
			traces.add(bugReport.substring(m.start(), m.end()));
		}
		return traces;
	}

	protected ArrayList<String> refineTraces(ArrayList<String> traces) {
		ArrayList<String> refined = new ArrayList<>();
		for (String trace : traces) {
			String[] parts = trace.split("\\s+");
			if (parts.length == 2) {
				String line = parts[1].trim();
				int leftParenIndex = line.indexOf('(');
				line = line.substring(0, leftParenIndex);
				refined.add(line);
			}
		}
		return refined;
	}

	protected void collectStackTraces() {
		for (int bugID : this.selectedBugs) {
			String reqFile = StaticData.BRICK_EXP + "/changereqs/" + repoName
					+ "/reqs/" + bugID + ".txt";
			String reqText = ContentLoader.loadFileContent(reqFile);
			ArrayList<String> traces = extractStackTraces(reqText);
			traces = refineTraces(traces);
			String outFile = this.traceFolder + "/" + bugID + ".txt";
			// saving the stack traces
			if (traces.isEmpty()) {
				System.err.println(bugID);
			} else {
				ContentWriter.writeContent(outFile, traces);
			}
		}
	}

	// developed on Feb 19,2018
	protected void collectStackTracesNew() {
		for (int bugID : this.selectedBugs) {
			String reqFile = StaticData.BRICK_EXP + "/changereqs/" + repoName
					+ "/reqs/" + bugID + ".txt";
			TraceCollector tcoll = new TraceCollector(reqFile);
			ArrayList<String> traces = tcoll.collectTraceEntries();
			// traces = refineTraces(traces);
			String outFile = this.traceFolder + "/" + bugID + ".txt";
			// saving the stack traces
			if (traces.isEmpty()) {
				System.err.println(bugID);
			} else {
				ContentWriter.writeContent(outFile, traces);
			}
		}
	}

	protected void collectStackTraceProse() {
		for (int bugID : this.selectedBugs) {
			String reqFile = StaticData.BRICK_EXP + "/changereqs/" + repoName
					+ "/reqs/" + bugID + ".txt";
			String outputFile = StaticData.BRICK_EXP
					+ "/laura-moreno/stacktraces-prose/" + repoName + "/"
					+ bugID + ".txt";
			TraceCollector tcoll = new TraceCollector(reqFile);
			String prose = tcoll.collectTraceProse();
			// traces = refineTraces(traces);
			// saving the stack traces
			if (prose.trim().isEmpty()) {
				System.err.println(bugID);
			} else {
				ContentWriter.writeContent(outputFile, prose);
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "log4j";
		new StackTraceExtractor(repoName).collectStackTracesNew();
	}
}
