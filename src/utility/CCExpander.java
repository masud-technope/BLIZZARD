package utility;

import java.util.ArrayList;
import java.util.HashMap;
import samurai.splitter.SamuraiSplitter;

public class CCExpander {

	public static HashMap<String, String> expandCCTokens(
			ArrayList<String> ccTokens) {
		SamuraiSplitter ssplitter = new SamuraiSplitter(ccTokens);
		return ssplitter.getSplittedTokenMap();
	}

	public static ArrayList<String> getExpandedVersion(
			ArrayList<String> ccTokens) {
		HashMap<String, String> expanded = expandCCTokens(ccTokens);
		for (String key : expanded.keySet()) {
			String expandedSingle = expanded.get(key).trim();
			if (expandedSingle.length() > key.length()) {
				ccTokens.addAll(MiscUtility.str2List(expandedSingle));
			}
		}
		return ccTokens;
	}
	
	public static ArrayList<String> getExpandedVersionV2(
			ArrayList<String> ccTokens) {
		HashMap<String, String> expanded = expandCCTokens(ccTokens);
		ArrayList<String> temp=new ArrayList<>();
		for(String ccToken: ccTokens){
			temp.add(ccToken);
			if(expanded.containsKey(ccToken)){
				String expandedSingle = expanded.get(ccToken).trim();
				if (expandedSingle.length() > ccToken.length()) {
					temp.addAll(MiscUtility.str2List(expandedSingle));
				}
			}
		}
		return temp;
	}
	
}
