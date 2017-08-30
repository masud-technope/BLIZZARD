package brick.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.StaticData;
import utility.ContentLoader;
import utility.ContentWriter;
import utility.MiscUtility;
import utility.SelectedBugs;

public class ExceptionExtractor {

	String repoName;
	String catKey;
	ArrayList<Integer> selectedBugs;
	String excepFile;

	public ExceptionExtractor(String repoName, String catKey) {
		this.repoName = repoName;
		this.catKey = catKey;
		this.selectedBugs = SelectedBugs.getStackSelectedBugs(repoName);
		this.excepFile = StaticData.BRICK_EXP + "/brick/exceptions/" + repoName
				+ ".txt";
	}

	protected HashSet<String> getExceptionMessages(String reportDesc) {
		HashSet<String> exceptions = new HashSet<>();
		String excepRegex = "(.)+Exception";
		Pattern p = Pattern.compile(excepRegex);
		Matcher m = p.matcher(reportDesc);
		while (m.find()) {
			String exception = reportDesc.substring(m.start(), m.end());
			String[] parts = exception.split("\\p{Punct}+|\\d+|\\s+");
			// System.out.println(exception);
			for (String part : parts) {
				if (part.endsWith("Exception") || part.endsWith("Error")) {
					if (part.equals("Exception") || part.equals("Error")) {
						// avoid the generic exception
					} else {
						exceptions.add(part);
					}
				}
			}
		}
		return exceptions;
	}

	protected void extractExceptions() {
		ArrayList<String> results = new ArrayList<>();
		for (int bugID : this.selectedBugs) {
			String bugReportFile = StaticData.BRICK_EXP + "/changereqs/"
					+ this.repoName + "/reqs/" + bugID + ".txt";
			String bugReport = ContentLoader.loadFileContent(bugReportFile);
			HashSet<String> exceptions = getExceptionMessages(bugReport);
			System.out.println(bugID + "\t" + MiscUtility.list2Str(exceptions));
			if (!exceptions.isEmpty())
				results.add(bugID + "\t" + MiscUtility.list2Str(exceptions));
		}
		// saving the exceptions
		ContentWriter.writeContent(excepFile, results);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "tomcat70";
		String collName = "eclipse";
		String catKey = "ST";
		new ExceptionExtractor(repoName, catKey).extractExceptions();
	}
}
