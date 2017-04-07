package deid.classifiers.ml;

import java.util.List;
import java.util.Properties;

import deid.Constants;
import deid.classifiers.ml.train.TrainClassifiersBinary;
import deid.document.IDocument;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class TestClassifier {

	public static void main(String[] args) {
		AbstractSequenceClassifier<CoreLabel> classifier = importModelFile(TrainClassifiersBinary.binaryConfig.getTrainingModelFile());

		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
		recordSet.loadFolder(new String[] { "113-05.xml" },true);

		recordSet.loadObj();

		for (IDocument doc : recordSet.documents) {
			List<Triple<String, Integer, Integer>> triples = classifier.classifyToCharacterOffsets(doc.getText());
			for (Triple<String, Integer, Integer> trip : triples) {
				int start = trip.second();
				int end =  trip.third();
				String label = trip.first();
				String text=doc.unTaggedText.substring(start, end);
				Debug.print(text);

			}
		}

	}

	// public static List<LabeledAnnotation> annotate2(IDocument doc, AbstractSequenceClassifier<CoreLabel> classifier,
	// String[] filterLabels) {
	//
	// Set<String> targetLabel = null;
	// if (filterLabels != null) {
	// targetLabel = new HashSet<>(Arrays.asList(filterLabels));
	// }
	//
	// List<LabeledAnnotation> mlAnns = new ArrayList<>();
	//
	// for (ISection section : doc.sections) {
	//
	// List<Triple<String, Integer, Integer>> triples = classifier
	// .classifyToCharacterOffsets(section.getContentText());
	// for (Triple<String, Integer, Integer> trip : triples) {
	// int start = section.titleSentence.end + trip.second();
	// int end = section.titleSentence.end + trip.third();
	// String label = trip.first();
	// LabeledAnnotation ann = new LabeledAnnotation(doc, start, end, doc.record.getSubString(start, end),
	// label, classifier.toString());
	//
	// if (targetLabel != null && !targetLabel.contains(ann.label))
	// continue;
	//
	// mlAnns.add(ann);
	//
	// }
	//
	// }
	//
	// return mlAnns;
	// }

	public static AbstractSequenceClassifier<CoreLabel> importModelFile(String modelFilePath) {

		AbstractSequenceClassifier<CoreLabel> classifier = null;
		try {
			Properties props = new Properties();
			// props.setProperty("tokenizerFactory",
			// "edu.stanford.nlp.process.WhitespaceTokenizer");
			classifier = CRFClassifier.getClassifier(modelFilePath, props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}

}
