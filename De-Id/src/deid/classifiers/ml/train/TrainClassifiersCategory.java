//package deid.classifiers.ml.train;
//
//import java.util.List;
//
//import deid.Constants;
//import deid.classifiers.ml.CRFUtil;
//import deid.document.IDocument;
//import deid.document.RecordCorpus;
//
//public class TrainClassifiersCategory {
//
//	public static String TRAIN_DIR = deid.Constants.TRAIN;
//
//	public static String OUTPUT_DIR = Constants.ML_MODELS;
//
//	public static CRFBinaryConfig config = new CRFBinaryConfig("CATEGORY");
//
//	public static void main(String[] args) {
//
//		train(config);
//
//	}
//
//	public static void train(CRFBinaryConfig config) {
//		RecordCorpus recordSet = new RecordCorpus(Constants.TRAIN);
//		recordSet.loadFolder(true);
//		// recordSet.partitionSections();
//		// recordSet.saveObj();
//		recordSet.loadObj();
//
//		CRFUtil.labelTokensGold(recordSet.documents, "CATEGORY");
//
//		config.prepareDirBinary(recordSet.documents, "resources/stanford.ner.prop", null);
//
//		try {
//			edu.stanford.nlp.ie.crf.CRFClassifier.main(new String[] { "-prop", config.getTrainingPropFile() });
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
