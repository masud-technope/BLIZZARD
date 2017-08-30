package text.normalizer;

import java.util.ArrayList;
import java.util.Arrays;

import stopwords.StopWordManager;
import utility.MiscUtility;

public class TextNormalizer {

	String content;
	final int MAX_KEYWORD_COUNT = 1024;

	public TextNormalizer(String content) {
		this.content = content;
	}

	public TextNormalizer() {
		// default constructor
	}

	public String normalizeSimple1024() {
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		ArrayList<String> baseKeys = new ArrayList<>();
		for (String word : wordList) {
			baseKeys.add(word);
			if (baseKeys.size() == MAX_KEYWORD_COUNT)
				break;
		}
		return MiscUtility.list2Str(baseKeys);
	}

	public String normalizeSimple() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		return MiscUtility.list2Str(wordList);
	}

	public String normalizeSimpleWithStemming() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		wordList = applyStemming(wordList);
		return MiscUtility.list2Str(wordList);
	}

	public String normalizeSimpleCodeDiscardSmall(String content) {
		String[] words = content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// decomposing the camel cases
		ArrayList<String> codeItems = extractCodeItem(wordList);
		codeItems = decomposeCamelCase(codeItems);
		wordList.addAll(codeItems);
		// discarding non-important tokens
		wordList = discardSmallTokens(wordList);
		String modified = MiscUtility.list2Str(wordList);
		// discard stop words
		StopWordManager stopManager = new StopWordManager();
		this.content = stopManager.getRefinedSentence(modified);
		return this.content;
	}

	protected ArrayList<String> applyStemming(ArrayList<String> words) {
		ArrayList<String> stemList = new ArrayList<>();
		Stemmer stemmer = new Stemmer();
		for (String word : words) {
			if (!word.trim().isEmpty()) {
				stemList.add(stemmer.stripAffixes(word));
			}
		}
		return stemList;
	}

	public String normalizeTextLaura() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// decomposing the camel cases
		ArrayList<String> codeItems = extractCodeItem(wordList);
		codeItems = decomposeCamelCase(codeItems);
		wordList.addAll(codeItems);
		// stopping
		StopWordManager stopManager = new StopWordManager();
		wordList = stopManager.getRefinedList(wordList);
		// stemming
		wordList = applyStemming(wordList);
		String modified = MiscUtility.list2Str(wordList);
		return modified;
	}

	public String normalizeSimpleCode() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// extracting code only items
		wordList = extractCodeItem(wordList);
		String modifiedContent = MiscUtility.list2Str(wordList);
		StopWordManager stopManager = new StopWordManager();
		this.content = stopManager.getRefinedSentence(modifiedContent);
		return this.content;
	}

	public String normalizeSimpleNonCode() {
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		ArrayList<String> codeOnly = extractCodeItem(wordList);
		// only non-code elements
		wordList.removeAll(codeOnly);
		return MiscUtility.list2Str(wordList);
	}

	protected ArrayList<String> discardSmallTokens(ArrayList<String> items) {
		// discarding small tokens
		ArrayList<String> temp = new ArrayList<>();
		for (String item : items) {
			if (item.length() > 2) {
				temp.add(item);
			}
		}
		return temp;
	}

	public String normalizeText() {
		// normalize the content
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// discard the small tokens
		wordList = discardSmallTokens(wordList);
		String modifiedContent = MiscUtility.list2Str(wordList);
		StopWordManager stopManager = new StopWordManager();
		this.content = stopManager.getRefinedSentence(modifiedContent);
		return this.content;
	}
	
	public String normalizeBaseline() {
		// normalize the content
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// discard the small tokens
		//wordList = discardSmallTokens(wordList);
		String modifiedContent = MiscUtility.list2Str(wordList);
		StopWordManager stopManager = new StopWordManager();
		this.content = stopManager.getRefinedSentence(modifiedContent);
		return this.content;
	}
	
	

	protected ArrayList<String> extractCodeItem(ArrayList<String> words) {
		// extracting camel-case letters
		ArrayList<String> codeTokens = new ArrayList<>();
		for (String token : words) {
			if (decomposeCamelCase(token).size() > 1) {
				codeTokens.add(token);
			}
		}
		return codeTokens;
	}

	protected ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(ftokens));
		return refined;
	}

	protected ArrayList<String> decomposeCamelCase(ArrayList<String> tokens) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		for (String token : tokens) {
			String camRegex = "([a-z])([A-Z]+)";
			String replacement = "$1\t$2";
			String filtered = token.replaceAll(camRegex, replacement);
			String[] ftokens = filtered.split("\\s+");
			refined.addAll(Arrays.asList(ftokens));
		}
		return refined;
	}
}
