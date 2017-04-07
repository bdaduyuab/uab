package deid.classifiers.ml.train;

import java.util.List;

import deid.Constants;
import deid.classifiers.ml.CRFUtil;
import deid.document.IDocument;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import deid.utils.TimeLog;

public class TrainClassifiersBinary {

	public static String TRAIN_DIR = deid.Constants.TRAIN;

	public static String OUTPUT_DIR = Constants.ML_MODELS;

	public static CRFBinaryConfig binaryConfig = new CRFBinaryConfig("BINARY");
	public static CRFBinaryConfig categoryConfig = new CRFBinaryConfig("CATEGORY");
	public static CRFBinaryConfig subCategoryConfig = new CRFBinaryConfig("SUBCATEGORY");

	
	public static CRFBinaryConfig config=subCategoryConfig;

	public static void main(String[] args) {

		train(config);

	}

	public static void train(CRFBinaryConfig config) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TRAIN);
		recordSet.loadFolder(false);
		 recordSet.partitionSections();
		// recordSet.saveObj();
//		recordSet.loadObj();
		
		

		String labelType = config.experimentName;
		
		Debug.print("\nTrain:"+config.experimentName);

		CRFUtil.labelTokensGold(recordSet.documents, labelType);

		config.prepareDirBinary(recordSet.documents, "resources/stanford.ner.prop", null);

		long base=TimeLog.getCurrent();
		Debug.print("base="+base);
		
		try {
			edu.stanford.nlp.ie.crf.CRFClassifier.main(new String[] { "-prop", config.getTrainingPropFile() });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Debug.print("Elapsed Time: "+TimeLog.getSpanMin(base));
	}

}
