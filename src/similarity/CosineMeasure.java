package similarity;

public class CosineMeasure {

	public static double getCosineSimilarity(int[] list1, int[] list2) {
		double cosmeasure = 0;
		double mode1 = getMode(list1);
		double mode2 = getMode(list2);
		double upper = 0;
		for (int i = 0; i < list1.length; i++) {
			upper += list1[i] * list2[i];
		}
		if (mode1 > 0 && mode2 > 0) {
			cosmeasure = upper / (mode1 * mode2);
		}
		return cosmeasure;
	}

	protected static double getMode(int[] list) {
		double sum = 0;
		for (int i : list) {
			sum += i * i;
		}
		return Math.sqrt(sum);
	}
}
