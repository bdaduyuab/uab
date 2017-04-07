package deid.classifiers.ml;

import java.util.List;

import deid.anns.IAnnotation;
import deid.classifiers.cache.CacheAnnotation;
import deid.document.IDocument;
import deid.utils.intervaltree.IntervalTree;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CRFAnnotator {

	public static void annotate(IDocument doc, CacheAnnotation cache, AbstractSequenceClassifier<CoreLabel> classifier) {

	}

	public static CacheAnnotation getCache(String type) {

		CacheAnnotation cache = null;

		if (type.equals("BINARY")) {
			cache = CRFClassifier.binaryCache;
		} else if (type.equals("CATEGORY")) {
			cache = CRFClassifier.categoryCache;
		} else if (type.equals("SUBCATEGORY")) {
			cache = CRFClassifier.subCategoryCache;
		}

		return cache;
	}

	public static AbstractSequenceClassifier<CoreLabel> getClassifier(String type) {

		AbstractSequenceClassifier<CoreLabel> classifer = null;

		if (type.equals("BINARY")) {
			classifer = CRFClassifier.getBinaryClassifier();
		} else if (type.equals("CATEGORY")) {
			classifer = CRFClassifier.getCategoryClassifier();
		} else if (type.equals("SUBCATEGORY")) {
			classifer = CRFClassifier.getSubCategoryClassifier();
		}

		return classifer;
	}

	public static void annotate(List<IDocument> docs, boolean loadFromCache, String type) {

		CacheAnnotation cache = getCache(type);
		AbstractSequenceClassifier<CoreLabel> classifier = null;

		if (loadFromCache) {
			cache = cache.load();
		}

		for (IDocument doc : docs) {
			List<IAnnotation> anns = null;
			doc.testTokenRegistry=new IntervalTree<>();

			if (loadFromCache) {
				anns = cache.hmDoc2Annotations.get(doc.fileName);
			} else {
				anns = CRFUtil.getAnnotations(doc.getText(), getClassifier(type));
			}

			for (IAnnotation ann : anns) {
				doc.testTokenRegistry.addInterval(ann.start, ann.end, ann);
			}

			cache.hmDoc2Annotations.put(doc.fileName, anns);
		}

		if (!loadFromCache) {
			cache.save();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
