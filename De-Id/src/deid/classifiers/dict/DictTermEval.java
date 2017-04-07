package deid.classifiers.dict;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deid.Constants;
import deid.anns.IAnnotation;
import deid.classifiers.eval.EvalRecord;
import deid.document.IDocument;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import deid.utils.dict.HumanNames;

public class DictTermEval {

	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TRAIN);
//		recordSet.loadFolder(new String[] { "113-05.xml" },true);
		recordSet.loadFolder(true);
		
		
		Dictionary dic=new Dictionary(new String[]{HumanNames.HUMAN_NAME_LEXICON}, true, true, true);
		
		
		
		EvalRecord<IAnnotation> evalAll=new EvalRecord<>();
		
		for(IDocument doc:recordSet.documents){
			List<IAnnotation> anns=dic.matchDoc(doc);
			
			EvalRecord<IAnnotation> evalRecord=evaluate(doc, anns);
			evalAll.mergeWith(evalRecord);
		}
		Map<String,EvalRecord<IAnnotation>> hmTerm2Eval=normalizeToTerm(evalAll);
		
		Debug.print(evalAll.toString());
		
		Set<String> termsSet=new HashSet<>(dic.terms);

		
		for(String term:hmTerm2Eval.keySet()){
			if(termsSet.contains(term)){
				Debug.print(term);
				Debug.print(hmTerm2Eval.get(term).toString());
			}
			
		}
		
		Set<String> unusedTerms=new HashSet<>(termsSet);
		unusedTerms.removeAll(hmTerm2Eval.keySet());
		Debug.print(unusedTerms);
		
	}
	public static void generateLexiconFile(){
		
	}
	
	public static Map<String,EvalRecord<IAnnotation>> normalizeToTerm(EvalRecord<IAnnotation> masterEvalRecord){
		Map<String,EvalRecord<IAnnotation>> hmTerm2Eval=new HashMap<>();
		
		for(IAnnotation ann:masterEvalRecord.TPs){
			if(!hmTerm2Eval.containsKey(ann.getText()))
				hmTerm2Eval.put(ann.getText(), new EvalRecord<>());
			hmTerm2Eval.get(ann.getText()).TPs.add(ann);
		}
		for(IAnnotation ann:masterEvalRecord.FPs){
			if(!hmTerm2Eval.containsKey(ann.getText()))
				hmTerm2Eval.put(ann.getText(), new EvalRecord<>());
			hmTerm2Eval.get(ann.getText()).FPs.add(ann);
		}
		for(IAnnotation ann:masterEvalRecord.FNs){
			if(!hmTerm2Eval.containsKey(ann.getText()))
				hmTerm2Eval.put(ann.getText(), new EvalRecord<>());
			hmTerm2Eval.get(ann.getText()).FNs.add(ann);
		}
		
		return hmTerm2Eval;
	}
	

	
	public static EvalRecord<IAnnotation> evaluate(IDocument doc,List<IAnnotation> anns){
		EvalRecord<IAnnotation> evalResult=new EvalRecord<>();
		
		
		for(IAnnotation ann:anns){
			List<IAnnotation> phis=doc.goldPHIRegistry.get(ann.start, ann.end);
			if(!phis.isEmpty()){
				evalResult.TPs.add(ann);
			} else{
				evalResult.FPs.add(ann);
			}
			
		}
		
		evalResult.FNs.addAll(doc.goldPHIs);
		evalResult.FNs.removeAll(evalResult.TPs);
		
		return evalResult;
		
	}

}
