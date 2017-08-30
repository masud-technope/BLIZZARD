package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MiscUtility {

	public static String list2Str(ArrayList<String> list) {
		String temp = new String();
		for (String item : list) {
			temp += item + " ";
		}
		return temp.trim();
	}

	public static String list2Str(String[] list) {
		String temp = new String();
		for (String item : list) {
			temp += item + " ";
		}
		return temp.trim();
	}

	public static String list2Str(HashSet<String> list) {
		String temp = new String();
		for (String item : list) {
			temp += item + " ";
		}
		return temp.trim();
	}

	public static ArrayList<String> str2List(String str) {
		return new ArrayList<>(Arrays.asList(str.split("\\s+")));
	}

	public static HashSet<String> str2Set(String str) {
		return new HashSet<>(Arrays.asList(str.split("\\s+")));
	}

	public static double[] list2Array(ArrayList<Integer> list) {
		double[] array = new double[list.size()];
		for (int index = 0; index < list.size(); index++) {
			array[index] = list.get(index);
		}
		return array;
	}

	public static HashMap<String, Integer> wordcount(String content) {
		// performing simple word count
		String[] words = content.split("\\s+");
		HashMap<String, Integer> countmap = new HashMap<>();
		for (String word : words) {
			if (countmap.containsKey(word)) {
				int count = countmap.get(word) + 1;
				countmap.put(word, count);
			} else {
				countmap.put(word, 1);
			}
		}
		return countmap;
	}

	public static double getItemAverage(ArrayList<Integer> items) {
		// get the item average
		double sum = 0;
		for (int item : items) {
			if (item > 0) {
				sum += 1.0 / item;
			}
		}
		return 1.0 / (sum / items.size());
	}

	public static double getItemAverageV2(ArrayList<Integer> items) {
		// get the item average
		double sum = 0;
		int bvcount = 0;
		for (int item : items) {
			if (item >= 0) {
				sum += item;
				bvcount++;
			}
		}
		return sum / items.size();
	}

	public static ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(ftokens));
		return refined;
	}

	public static void showList(ArrayList<String> items) {
		for (String item : items) {
			System.out.println(item);
		}
	}

	public static void showList(HashSet<String> items) {
		for (String item : items) {
			System.out.println(item);
		}
	}

	public static void showList(Set<String> items) {
		for (String item : items) {
			System.out.println(item);
		}
	}

	public static void showList(String[] items) {
		for (String item : items) {
			System.out.println(item);
		}
	}
}
