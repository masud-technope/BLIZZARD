package utility;

import java.io.File;
import java.util.ArrayList;
import config.StaticData;

public class GoldsetLoader {
	public static ArrayList<String> goldsetLoader(String repoName, int bugID) {
		// loading the gold set for any repo and bug ID
		ArrayList<String> goldset = new ArrayList<>();
		String goldFile = StaticData.BRICK_EXP + "/Goldset/" + repoName
				+ "/" + bugID + ".txt";
		File f = new File(goldFile);
		if (f.exists()) { // if the solution file exists
			String content = ContentLoader.loadFileContent(goldFile);
			String[] items = content.split("\n");
			for (String item : items) {
				if (!item.trim().isEmpty())
					goldset.add(item);
			}
		}
		return goldset;
	}

}
