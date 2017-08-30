package stopwords;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;

import utility.ContentLoader;

public class StopWordManager {

	public ArrayList<String> stopList;
	String stopDir = "./data/stop-words-english-total.txt";
	String javaKeywordFile = "./data/java-keywords.txt";;
	String CppKeywordFile = "./data/cpp-keywords.txt";

	public StopWordManager() {
		// initialize the Hash set
		this.stopList = new ArrayList<>();
		this.loadStopWords();
	}

	protected void loadStopWords() {
		// loading stop words
		try {
			Scanner scanner = new Scanner(new File(this.stopDir));
			while (scanner.hasNext()) {
				String word = scanner.nextLine().trim();
				this.stopList.add(word);
			}
			scanner.close();

			// now add the programming keywords
			ArrayList<String> keywords = ContentLoader
					.getAllLinesOptList(javaKeywordFile);
			this.stopList.addAll(keywords);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String removeSpecialChars(String sentence) {
		// removing special characters
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		for (String str : parts) {
			refined += str.trim() + " ";
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	public String removeTinyTerms(String sentence) {
		// removing special characters
		String regex = "\\p{Punct}+|\\d+|\\s+";
		String[] parts = sentence.split(regex);
		String refined = new String();
		for (String str : parts) {
			if (str.length() >= 3)
				refined += str.trim() + " ";
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	public String getRefinedSentence(String sentence) {
		// get refined sentence
		String refined = new String();
		String temp = removeSpecialChars(sentence);
		String[] tokens = temp.split("\\s+");
		for (String token : tokens) {
			if (!this.stopList.contains(token.toLowerCase())) {
				refined += token + " ";
			}
		}
		return refined.trim();
	}

	public ArrayList<String> getRefinedList(String[] words) {
		ArrayList<String> refined = new ArrayList<>();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public ArrayList<String> getRefinedList(ArrayList<String> words) {
		ArrayList<String> refined = new ArrayList<>();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public HashSet<String> getRefinedList(HashSet<String> words) {
		HashSet<String> refined = new HashSet<>();
		for (String word : words) {
			if (!this.stopList.contains(word.toLowerCase())) {
				refined.add(word);
			}
		}
		return refined;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StopWordManager manager = new StopWordManager();
		String str = "statement protected java Boolean lang expression Quick Invert operator omits AdvancedQuickAssistProcessor";
		// String modified=manager.removeSpecialChars(sentence);
		System.out.println(manager.getRefinedSentence(str));
	}
}
