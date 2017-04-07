package deid.classifiers;

import deid.Constants;
import deid.classifiers.eval.TokenEvaluator;
import deid.classifiers.ml.CRFAnnotator;
import deid.classifiers.ml.CRFUtil;
import deid.document.IDocument;
import deid.document.IToken;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import deid.utils.TimeLog;

public class Annotator {

	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
//		recordSet.loadFolder(new String[] { "113-05.xml" },true);
		recordSet.loadFolder(false);
		recordSet.partitionSections();
//		recordSet.loadObj();
		
		String phiType="BINARY";
		
		long base=TimeLog.getCurrent();
		CRFAnnotator.annotate(recordSet.documents,true,phiType);
		Debug.print("Time elapsed:"+TimeLog.getSpanMin(base));
		CRFUtil.labelTokensGold(recordSet.documents,phiType);
		
		CRFUtil.labelTokensTest(recordSet.documents);
//		CRFUtil.mergeTokens(recordSet.documents);

		
		TokenEvaluator.evaluateCategory(recordSet);
		TokenEvaluator.printEval(recordSet, true);
//		TokenEvaluator.debugAnnotation(recordSet);
//		TokenEvaluator.debugToken(recordSet);
		
		TokenEvaluator.getErrorStatistics(recordSet,false,true,true);
		
		

	}

}
