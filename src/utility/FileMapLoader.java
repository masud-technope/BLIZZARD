package utility;

import java.util.ArrayList;
import java.util.HashMap;
import config.StaticData;

public class FileMapLoader {

	public static HashMap<Integer, String> loadFileMap(String repoName) {
		String mapFile = StaticData.BRICK_EXP + "/lemur-indri/corpus/"
				+ repoName + ".ckeys";
		HashMap<Integer, String> fileMap = new HashMap<>();
		ArrayList<String> lines = ContentLoader.getAllLinesList(mapFile);
		for (String line : lines) {
			if (line.trim().isEmpty())
				continue;
			String[] parts = line.split(":");
			int fileID = Integer.parseInt(parts[0].trim());
			String fileURL = parts[1] + ":" + parts[2];
			fileURL = fileURL.replace('\\', '.');
			fileMap.put(fileID, fileURL);
		}
		return fileMap;
	}

	public static HashMap<Integer, String> loadKeyMap(String repoName) {
		String keysFile = StaticData.BRICK_EXP + "/corpus/" + repoName
				+ ".ckeys";
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(keysFile);
		HashMap<Integer, String> keyMap = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.split(":");
			String key = parts[2].trim();
			String canonical = key.replace('\\', '.');
			int index = Integer.parseInt(parts[0].trim());
			keyMap.put(index, canonical);
		}
		return keyMap;
	}

	public static HashMap<Integer, ArrayList<String>> loadBRExceptions(
			String repoName) {
		// loading the exceptions
		String excepFile = StaticData.BRICK_EXP + "/BR-Exceptions/"
				+ repoName + ".txt";
		ArrayList<String> lines = ContentLoader.getAllLinesOptList(excepFile);
		HashMap<Integer, ArrayList<String>> excepKeyMap = new HashMap<>();
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			int bugID = Integer.parseInt(parts[0].trim());
			ArrayList<String> exceptList = new ArrayList<>();
			for (int i = 1; i < parts.length; i++) {
				exceptList.add(parts[i].trim());
			}
			excepKeyMap.put(bugID, exceptList);
		}
		return excepKeyMap;
	}
}
