package similarity;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class MyTokenizer {

	String itemToTokenize;

	public MyTokenizer(String item) {
		// initialization
		this.itemToTokenize = item;
	}

	public ArrayList<String> tokenize_text_item() {
		// tokenizing textual content
		StringTokenizer tokenizer = new StringTokenizer(this.itemToTokenize);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();

			if (!token.isEmpty()) {
				ArrayList<String> smalltokens = process_text_item(token);
				tokens.addAll(smalltokens);
			}
		}
		return tokens;
	}

	public ArrayList<String> tokenize_code_item() {
		// code for tokenization of code elements
		//String tcode = format_the_code(this.itemToTokenize);
		//String fcode = remove_code_comment(tcode);
		StringTokenizer tokenizer = new StringTokenizer(this.itemToTokenize);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				ArrayList<String> tokenparts = process_source_token(token);
				tokens.addAll(tokenparts);
			}
		}
		return tokens;
	}
	
	public ArrayList<String> refine_insignificant_tokens(
			ArrayList<String> codeTokens) {
		// code for refining the insignificant tokens of length 1
		try {
			for (String token : codeTokens) {
				if (token.trim().length() == 1) {
					codeTokens.remove(token);
				}
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return codeTokens;
	}
	
	
	

	protected static String remove_code_comment(String codeFragment) {
		// code for removing code fragment
		String modifiedCode = new String();
		try {
			String pattern = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
			modifiedCode = codeFragment.replaceAll(pattern, "");
		} catch (Exception exc) {
		} catch (StackOverflowError err) {

		}
		return modifiedCode;
	}

	protected static ArrayList<String> process_source_token(String token) {
		// code for processing source code token
		ArrayList<String> modified = new ArrayList<String>();
		String[] segments = token.split("\\.");
		for (String segment : segments) {
			String[] parts = org.apache.commons.lang.StringUtils
					.splitByCharacterTypeCamelCase(segment);
			if (parts.length == 0) {
				modified.add(segment);
			} else {
				for (String part : parts) {
					modified.add(part);
				}
			}
		}
		return modified;
	}

	protected static ArrayList<String> process_text_item(String bigToken) {
		// code for processing big tokens
		ArrayList<String> modified = new ArrayList<>();
		try {
			String[] parts = org.apache.commons.lang.StringUtils
					.splitByCharacterTypeCamelCase(bigToken);
			for (String part : parts) {
				String[] segments = part.split("\\.");
				if (segments.length == 0)
					modified.add(part);
				else {
					for (String segment : segments) {
						if (!segment.isEmpty() && segment.length() >= 2)
							modified.add(segment);
					}
				}
			}
		} catch (Exception exc) {
		}
		// returning big token
		return modified;
	}

}
