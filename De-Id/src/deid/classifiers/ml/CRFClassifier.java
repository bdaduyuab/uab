package deid.classifiers.ml;

import deid.classifiers.cache.CacheAnnotation;
import deid.classifiers.ml.train.TrainClassifiersBinary;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CRFClassifier {

	public static CacheAnnotation binaryCache = new CacheAnnotation("BINARY_CACHE");

	public static AbstractSequenceClassifier<CoreLabel> binaryClassifier;

	public static AbstractSequenceClassifier<CoreLabel> getBinaryClassifier() {
		if (binaryClassifier == null) {

			binaryClassifier = CRFUtil.importModelFile(TrainClassifiersBinary.binaryConfig.getTrainingModelFile());

		}
		return binaryClassifier;
	}

	public static CacheAnnotation categoryCache = new CacheAnnotation("CATEGORY_CACHE");

	public static AbstractSequenceClassifier<CoreLabel> categoryClassifier;

	public static AbstractSequenceClassifier<CoreLabel> getCategoryClassifier() {
		if (categoryClassifier == null) {
			categoryClassifier = CRFUtil.importModelFile(TrainClassifiersBinary.categoryConfig.getTrainingModelFile());
		}
		return categoryClassifier;
	}

	public static CacheAnnotation subCategoryCache = new CacheAnnotation("SUBCATEGORY_CACHE");

	public static AbstractSequenceClassifier<CoreLabel> subCategoryClassifier;

	public static AbstractSequenceClassifier<CoreLabel> getSubCategoryClassifier() {
		if (subCategoryClassifier == null) {
			subCategoryClassifier = CRFUtil.importModelFile(TrainClassifiersBinary.subCategoryConfig.getTrainingModelFile());
		}
		return subCategoryClassifier;
	}

}
