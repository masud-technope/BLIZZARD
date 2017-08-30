package utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import core.items.CorpusToken;



public class MyItemSorter {

	public static List<Map.Entry<String, CorpusToken>> sortQTokensByTFIDF(
			HashMap<String, CorpusToken> qTokenMap) {
		// code for sorting the hash map
		List<Map.Entry<String, CorpusToken>> list = new LinkedList<>(
				qTokenMap.entrySet());
		list.sort(new Comparator<Map.Entry<String, CorpusToken>>() {

			@Override
			public int compare(Entry<String, CorpusToken> e1,
					Entry<String, CorpusToken> e2) {
				// TODO Auto-generated method stub
				CorpusToken t1 = e1.getValue();
				Double v1 = new Double(t1.tfIDFScore);
				CorpusToken t2 = e2.getValue();
				Double v2 = new Double(t2.tfIDFScore);
				return v2.compareTo(v1);
			}
		});
		return list;

	}

	public static List<Map.Entry<String, CorpusToken>> sortQTokensByTF(
			HashMap<String, CorpusToken> qTokenMap) {
		// code for sorting the hash map
		List<Map.Entry<String, CorpusToken>> list = new LinkedList<>(
				qTokenMap.entrySet());
		list.sort(new Comparator<Map.Entry<String, CorpusToken>>() {

			@Override
			public int compare(Entry<String, CorpusToken> e1,
					Entry<String, CorpusToken> e2) {
				// TODO Auto-generated method stub
				CorpusToken t1 = e1.getValue();
				Double v1 = new Double(t1.tf);
				CorpusToken t2 = e2.getValue();
				Double v2 = new Double(t2.tf);
				return v2.compareTo(v1);
			}
		});
		return list;
	}

	public static List<Map.Entry<String, CorpusToken>> sortQTokensByTR(
			HashMap<String, CorpusToken> qTokenMap) {
		// code for sorting the hash map
		List<Map.Entry<String, CorpusToken>> list = new LinkedList<>(
				qTokenMap.entrySet());
		list.sort(new Comparator<Map.Entry<String, CorpusToken>>() {

			@Override
			public int compare(Entry<String, CorpusToken> e1,
					Entry<String, CorpusToken> e2) {
				// TODO Auto-generated method stub
				CorpusToken t1 = e1.getValue();
				Double v1 = new Double(t1.tokenRankScore);
				CorpusToken t2 = e2.getValue();
				Double v2 = new Double(t2.tokenRankScore);
				return v2.compareTo(v1);
			}
		});
		return list;
	}

	public static List<Map.Entry<String, CorpusToken>> sortQTokensByCRIDF(
			HashMap<String, CorpusToken> qTokenMap) {
		// code for sorting the hash map
		List<Map.Entry<String, CorpusToken>> list = new LinkedList<>(
				qTokenMap.entrySet());
		list.sort(new Comparator<Map.Entry<String, CorpusToken>>() {

			@Override
			public int compare(Entry<String, CorpusToken> e1,
					Entry<String, CorpusToken> e2) {
				// TODO Auto-generated method stub
				CorpusToken t1 = e1.getValue();
				Double v1 = new Double(t1.crIDFScore);
				CorpusToken t2 = e2.getValue();
				Double v2 = new Double(t2.crIDFScore);
				return v2.compareTo(v1);
			}
		});
		return list;
	}

}
