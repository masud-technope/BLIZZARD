package similarity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class CosineSimilarityMeasure {

	// titles
	String title1 = new String();
	String title2 = new String();
	double cosine_measure = 0;

	// Hash sets
	Set<String> set1;
	Set<String> set2;

	// Array Lists
	ArrayList<String> list1;
	ArrayList<String> list2;

	// Hash Maps
	HashMap<String, Integer> map1;
	HashMap<String, Integer> map2;

	public CosineSimilarityMeasure(String title1, String title2) {
		// assigning two parties
		this.title1 = title1;
		this.title2 = title2;
		// instantiating sets
		set1 = new HashSet<String>();
		set2 = new HashSet<String>();
		// instantiating the Hash maps
		map1 = new HashMap<String, Integer>();
		map2 = new HashMap<String, Integer>();
	}

	public CosineSimilarityMeasure(ArrayList<String> list1,
			ArrayList<String> list2) {
		// getting cosine similarity measure
		this.list1 = list1;
		this.list2 = list2;
		// instantiating sets
		set1 = new HashSet<String>();
		set2 = new HashSet<String>();
		// instantiating the Hash maps
		map1 = new HashMap<String, Integer>();
		map2 = new HashMap<String, Integer>();
	}

	@Deprecated
	protected ArrayList<String> getTokenized_text_content(String content) {
		// code for creating tokens
		ArrayList<String> tokens = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(content);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		// returning tokens
		return tokens;
	}

	protected ArrayList<String> getTokenized_text_content_granularized(
			String content) {
		MyTokenizer myTokenizer = new MyTokenizer(content);
		ArrayList<String> tokens = myTokenizer.tokenize_code_item();
		// discarding the insignificant tokens such as punctuation marks
		return myTokenizer.refine_insignificant_tokens(tokens);
	}

	
	public double getCosineSimilarityScore()
	{
		try{
		for (String str : this.list1) {

			if (!str.isEmpty()) {
				set1.add(str);
				if (!map1.containsKey(str))
					map1.put(str, new Integer(1));
				else {
					int count = Integer.parseInt(map1.get(str).toString());
					count++;
					map1.put(str, new Integer(count));
				}
			}
		}

		for (String str : this.list2) {
			
			if (!str.isEmpty()) {
				set2.add(str);
				if (!map2.containsKey(str))
					map2.put(str, new Integer(1));
				else {
					int count = Integer.parseInt(map2.get(str).toString());
					count++;
					map2.put(str, new Integer(count));
				}
			}
		}

		// converting to Hash Map
		HashSet<String> hset1 = new HashSet<String>(set1);
		HashSet<String> hset2 = new HashSet<String>(set2);

		double sqr1 = 0;
		for (int i = 0; i < hset1.size(); i++) {
			int val = Integer
					.parseInt(map1.get(hset1.toArray()[i]) != null ? map1
							.get(hset1.toArray()[i]).toString() : "0");
			sqr1 += val * val;
		}

		double sqr2 = 0;
		for (int i = 0; i < hset2.size(); i++) {
			int val = Integer
					.parseInt(map2.get(hset2.toArray()[i]) != null ? map2
							.get(hset2.toArray()[i]).toString() : "0");
			sqr2 += val * val;
		}

		// now calculate the similarity
		double top_part = 0;
		for (int i = 0; i < hset1.size(); i++) {
			String key = (String) set1.toArray()[i];
			double val1 = Double.parseDouble(map1.get(key).toString());
			double val2 = Double.parseDouble(map2.get(key) != null ? map2
					.get(key).toString() : "0");
			top_part += val1 * val2;
		}

		double cosine_ratio = 0;
		try {
			cosine_ratio = top_part / (Math.sqrt(sqr1) * Math.sqrt(sqr2));
		} catch (Exception exc) {
			cosine_ratio = 0;
		}
		// System.out.println(cosine_ratio);
		this.cosine_measure = cosine_ratio;

	} catch (Exception exc) {
		// exc.printStackTrace();
	}
	// returning cosine ration
	return cosine_measure;

	}
	
	
	public double get_cosine_similarity_score(boolean granularized) {
		// code for getting the cosine similarity score
		try {
			if (title1.isEmpty() || title1 == null)
				return 0;
			if (title2.isEmpty() || title2 == null)
				return 0;

			ArrayList<String> parts1 = granularized == true ? this
					.getTokenized_text_content_granularized(title1) : this
					.getTokenized_text_content(title1);
			ArrayList<String> parts2 = granularized == true ? this
					.getTokenized_text_content_granularized(title2) : this
					.getTokenized_text_content(title2);

			// Stemmer my_stemmer=new Stemmer();

			for (String str : parts1) {

				// str=my_stemmer.stripAffixes(str);
				if (!str.isEmpty()) {
					set1.add(str);
					if (!map1.containsKey(str))
						map1.put(str, new Integer(1));
					else {
						int count = Integer.parseInt(map1.get(str).toString());
						count++;
						map1.put(str, new Integer(count));
					}
				}
			}

			for (String str : parts2) {
				// str=my_stemmer.stripAffixes(str);
				if (!str.isEmpty()) {
					set2.add(str);
					if (!map2.containsKey(str))
						map2.put(str, new Integer(1));
					else {
						int count = Integer.parseInt(map2.get(str).toString());
						count++;
						map2.put(str, new Integer(count));
					}
				}
			}

			// show extracted tokens
			// show_extracted_tokens(set1);
			// System.out.println();
			// show_extracted_tokens(set2);

			// converting to Hash Map
			HashSet<String> hset1 = new HashSet<String>(set1);
			HashSet<String> hset2 = new HashSet<String>(set2);

			double sqr1 = 0;
			for (int i = 0; i < hset1.size(); i++) {
				int val = Integer
						.parseInt(map1.get(hset1.toArray()[i]) != null ? map1
								.get(hset1.toArray()[i]).toString() : "0");
				sqr1 += val * val;
			}

			double sqr2 = 0;
			for (int i = 0; i < hset2.size(); i++) {
				int val = Integer
						.parseInt(map2.get(hset2.toArray()[i]) != null ? map2
								.get(hset2.toArray()[i]).toString() : "0");
				sqr2 += val * val;
			}

			// now calculate the similarity
			double top_part = 0;
			for (int i = 0; i < hset1.size(); i++) {
				String key = (String) set1.toArray()[i];
				double val1 = Double.parseDouble(map1.get(key).toString());
				double val2 = Double.parseDouble(map2.get(key) != null ? map2
						.get(key).toString() : "0");
				top_part += val1 * val2;
			}

			double cosine_ratio = 0;
			try {
				cosine_ratio = top_part / (Math.sqrt(sqr1) * Math.sqrt(sqr2));
			} catch (Exception exc) {
				cosine_ratio = 0;
			}
			// System.out.println(cosine_ratio);
			this.cosine_measure = cosine_ratio;

		} catch (Exception exc) {
			// exc.printStackTrace();
		}
		// returning cosine ration
		return cosine_measure;
	}

	protected void show_extracted_tokens(Set s) {
		// code for showing extracted tokens
		for (int i = 0; i < s.size(); i++) {
			System.out.print(s.toArray()[i] + "\t");
		}
	}

	protected static String load_text_content(String fileName) {
		// code for loading content
		String content = new String();
		try {
			Scanner scanner = new Scanner(new File("./testdata/" + fileName));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line;
			}
			scanner.close();
		} catch (Exception exc) {
		}
		return content;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String title1 = load_text_content("stack.txt");
		String title2 = load_text_content("stack2.txt");
		CosineSimilarityMeasure measure = new CosineSimilarityMeasure(title1,
				title2);
		double similarity = measure.get_cosine_similarity_score(true);
		System.out.println("Similarity score:" + similarity);
	}

}
