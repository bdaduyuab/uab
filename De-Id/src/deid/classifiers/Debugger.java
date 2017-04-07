package deid.classifiers;

import deid.Constants;
import deid.classifiers.eval.TokenEvaluator;
import deid.classifiers.ml.CRFAnnotator;
import deid.classifiers.ml.CRFUtil;
import deid.document.RecordCorpus;

public class Debugger {
	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
//		recordSet.loadFolder(new String[] { "316-01.xml","236-04.xml","213-02.xml","203-03.xml" },true);
//		recordSet.loadFolder(new String[] { "211-02.xml" ,"232-04.xml"},true);

		
		
		recordSet.partitionSections();
//		recordSet.loadObj();
		
		String phiType="SUBCATEGORY";
		
		CRFAnnotator.annotate(recordSet.documents,true,phiType);
		CRFUtil.labelTokensGold(recordSet.documents,phiType);
		CRFUtil.labelTokensTest(recordSet.documents);
		CRFUtil.mergeTokens(recordSet.documents);
		

//		TokenEvaluator.evaluate(recordSet);
//		TokenEvaluator.printEval(recordSet, true);
//		TokenEvaluator.debugToken(recordSet);
//		TokenEvaluator.debugAnnotation(recordSet,false);
		TokenEvaluator.printForEvaluation(recordSet);
		
//		TokenEvaluator.getErrorStatistics(recordSet,false,true,true);
	}
}
