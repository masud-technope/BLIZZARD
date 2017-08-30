package lucenecheck;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import config.StaticData;

public class TFIDFManager {

	String indexFolder;
	String repoName;
	public static HashMap<String, Double> idfMap;
	public static HashMap<String, Long> tfMap;
	public static HashMap<String, Double> tpMap;
	public static HashMap<String, Double> dfRatioMap;
	public static int sumAllTermFreq;
	public static final String FIELD_CONTENTS = "contents";

	String targetTerm;
	HashSet<String> keys;
	long totalTermFreqCorpus = 0;
	boolean stem;

	public TFIDFManager(String targetTerm, String repoName, boolean stem) {
		this.stem = stem;
		if (stem) {
			this.indexFolder = StaticData.BRICK_EXP
					+ "/lucene/index-class-stem/" + repoName;
		} else {
			this.indexFolder = StaticData.BRICK_EXP
					+ "/lucene/index-class/" + repoName;
		}

		this.targetTerm = targetTerm;
	}

	public TFIDFManager(String repoName, boolean stem) {
		this.stem = stem;
		if (stem) {
			this.indexFolder = StaticData.BRICK_EXP
					+ "/lucene/index-class-stem/" + repoName;
		} else {
			this.indexFolder = StaticData.BRICK_EXP
					+ "/lucene/index-class/" + repoName;
		}
		this.repoName = repoName;
		this.keys = new HashSet<>();
		this.idfMap = new HashMap<>();
		this.tfMap = new HashMap<>();
		this.tpMap = new HashMap<>();
		this.dfRatioMap = new HashMap<>();
	}

	protected double getIDF(int N, int DF) {
		// getting the IDF
		if (DF == 0)
			return 0;
		return Math.log(1 + (double) N / DF);
	}

	public HashMap<String, Long> calculateTF() {
		HashMap<String, Long> termFreqMap = new HashMap<>();
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder).toPath()));
			// String targetTerm = "breakpoint";

			Fields fields = MultiFields.getFields(reader);
			for (String field : fields) {
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator();
				BytesRef bytesRef;
				while ((bytesRef = termsEnum.next()) != null) {
					if (termsEnum.seekExact(bytesRef)) {
						String term = bytesRef.utf8ToString();
						this.keys.add(term);
					}
				}
			}

			for (String term : this.keys) {
				Term t = new Term(FIELD_CONTENTS, term);
				// calculating the TF
				long totalTermFreq = reader.totalTermFreq(t);
				if (!termFreqMap.containsKey(term)) {
					termFreqMap.put(term, totalTermFreq);
					totalTermFreqCorpus += totalTermFreq;
				}
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return termFreqMap;
	}

	public HashMap<String, Double> calculateTermProb(HashMap<String, Long> TFMap) {
		// getting term probability from the corpus
		HashMap<String, Double> termProbMap = new HashMap<>();
		for (String key : TFMap.keySet()) {
			long termFreq = TFMap.get(key);
			double termProb = (double) termFreq / totalTermFreqCorpus;
			if (!termProbMap.containsKey(key)) {
				termProbMap.put(key, termProb);
			}
		}
		return termProbMap;
	}

	public HashMap<String, Double> calculateIDFOnly() {
		IndexReader reader = null;
		HashMap<String, Double> inverseDFMap = new HashMap<>();
		try {
			reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder).toPath()));
			// String targetTerm = "breakpoint";

			Fields fields = MultiFields.getFields(reader);
			for (String field : fields) {
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator();
				BytesRef bytesRef;
				while ((bytesRef = termsEnum.next()) != null) {
					if (termsEnum.seekExact(bytesRef)) {
						String term = bytesRef.utf8ToString();
						this.keys.add(term);
					}
				}
			}
			// now go for the IDF
			int N = reader.numDocs();
			double maxIDF = 0;
			for (String term : this.keys) {
				Term t = new Term(FIELD_CONTENTS, term);
				int docFreq = reader.docFreq(t);
				double idf = getIDF(N, docFreq);
				if (!inverseDFMap.containsKey(term)) {
					inverseDFMap.put(term, idf);
					if (idf > maxIDF) {
						maxIDF = idf;
					}
				}
			}
			// now normalize the IDF scores
			for (String term : this.keys) {
				double idf = inverseDFMap.get(term);
				idf = idf / maxIDF;
				inverseDFMap.put(term, idf);
			}

		} catch (Exception exc) {
			// handle the exception
		}
		return inverseDFMap;
	}

	public HashMap<String, Double> calculateIDF() {
		// calculate IDF of a term
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory
					.open(new File(indexFolder).toPath()));
			// String targetTerm = "breakpoint";

			Fields fields = MultiFields.getFields(reader);
			for (String field : fields) {
				Terms terms = fields.terms(field);
				TermsEnum termsEnum = terms.iterator();
				BytesRef bytesRef;
				while ((bytesRef = termsEnum.next()) != null) {
					if (termsEnum.seekExact(bytesRef)) {
						String term = bytesRef.utf8ToString();
						this.keys.add(term);
					}
				}
			}

			// now get their DF
			int N = reader.numDocs();
			int sumTotalTermFreq = 0;
			for (String term : this.keys) {
				Term t = new Term(FIELD_CONTENTS, term);
				// calculating the TF
				long totalTermFreq = reader.totalTermFreq(t);
				if (!tfMap.containsKey(term)) {
					tfMap.put(term, totalTermFreq);
					sumTotalTermFreq += totalTermFreq;
				}
				// calculating the IDF
				int docFreq = reader.docFreq(t);
				double idf = getIDF(N, docFreq);

				// adding IDF values
				if (!this.idfMap.containsKey(term)) {
					this.idfMap.put(term, idf);
				}
				// storing the DF ratios
				if (!this.dfRatioMap.containsKey(term)) {
					double dfR = (double) docFreq / N;
					this.dfRatioMap.put(term, dfR);
				}
			}

			// storing total term frequencies
			TFIDFManager.sumAllTermFreq = sumTotalTermFreq;

			// now calculate the term probability for RSV
			for (String term : this.tfMap.keySet()) {
				long termCount = this.tfMap.get(term);
				double probability = (double) termCount
						/ TFIDFManager.sumAllTermFreq;
				if (!tpMap.containsKey(term)) {
					this.tpMap.put(term, probability);
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return idfMap;
	}

	protected static void clear() {
		// clearing the static items
		tfMap.clear();
		idfMap.clear();
		tpMap.clear();
		sumAllTermFreq = 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String repoName = "log4j";
		String queryTerm = "warn";
		boolean stem = false;
		new TFIDFManager(repoName, stem).calculateIDF();
		// new TFIDFManager(repoName).calculateEntropy(queryTerm);
	}
}
