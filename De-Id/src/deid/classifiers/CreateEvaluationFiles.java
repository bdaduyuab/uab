package deid.classifiers;

import deid.Constants;
import deid.classifiers.eval.TokenEvaluator;
import deid.classifiers.ml.CRFAnnotator;
import deid.classifiers.ml.CRFUtil;
import deid.document.IDocument;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import deid.utils.FileUtil;

public class CreateEvaluationFiles {

	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
//		recordSet.loadFolder(new String[] { "316-01.xml","236-04.xml","213-02.xml","218-04.xml" },true);
//		recordSet.loadFolder(new String[] { "203-03.xml" ,"194-02.xml"},true);
		
		recordSet.loadFolder(new String[] { "316-01.xml","213-02.xml","216-02.xml","203-04.xml","115-04.xml" },true);
		
//		recordSet.loadFolder(false);
		recordSet.partitionSections();
		
		
		String docType="test";

		
		String filename=docType+"_set.SUBCATEGORY.txt";

		
		CRFUtil.labelTokensGold(recordSet.documents,"SUBCATEGORY");		
		CRFAnnotator.annotate(recordSet.documents,true,"SUBCATEGORY");
		CRFUtil.labelTokensTest(recordSet.documents);
		CRFUtil.mergeTokens(recordSet.documents);
		
		String content=TokenEvaluator.printForEvaluation(recordSet,false);
		
		Debug.print(Constants.EVAL_DOCS);
		
		FileUtil.writeFile(content, Constants.EVAL_DOCS+"/"+docType+"_set.SUBCATEGORY.txt", false);
		
		content=TokenEvaluator.printForEvaluation(recordSet,true);
		FileUtil.writeFile(content, Constants.EVAL_DOCS+"/"+docType+"_set.BINARY.txt", false);

		
		
		/*gold standard*/
		filename=docType+"_set.gold.txt";

		
		StringBuffer buf=new StringBuffer();
		for(IDocument doc:recordSet.documents){
			buf.append("-------------------------"+doc.fileName+"------------------------\n");
			buf.append(""+doc.filePath+"\n");


			
			buf.append(doc.getGoldPHITagged());
		}
		FileUtil.writeFile(buf.toString(), Constants.EVAL_DOCS+"/"+filename, false);

		
		
	}

}
