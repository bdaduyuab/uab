package deid.classifiers.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deid.anns.IAnnotation;
import deid.document.IDocument;
import deid.document.IToken;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import edu.stanford.nlp.util.StringUtils;

public class TokenEvaluator {

	public static void debugToken(RecordCorpus corpus) {

		for (IDocument doc : corpus.documents) {

			List<IToken> tokens = new ArrayList<>();

			tokens.addAll(doc.tokenEvalRecord.TPs);
			tokens.addAll(doc.tokenEvalRecord.FPs);
			tokens.addAll(doc.tokenEvalRecord.FNs);

			Collections.sort(tokens, new Comparator<IToken>() {

				@Override
				public int compare(IToken o1, IToken o2) {
					// TODO Auto-generated method stub
					return o1.start - o2.start;
				}
			});

			StringBuffer buf = new StringBuffer(doc.unTaggedText);

			int offset = 0;
			for (IToken token : tokens) {
				String label = null;
				if (doc.tokenEvalRecord.TPs.contains(token))
					label = "TP:"+token.testLabel;
				if (doc.tokenEvalRecord.FPs.contains(token))
					label = "FP:"+token.testLabel;
				if (doc.tokenEvalRecord.FNs.contains(token))
					label = "FN:"+token.testLabel;

				String replaceString = "[" + label + ":" + token.getText() + "]";
				buf.replace(token.start + offset, token.end + offset, replaceString);
				offset = offset - (token.end - token.start) + replaceString.length();
			}

			Debug.print(buf.toString());

		}

	}
	
	public static void debugAnnotation(RecordCorpus corpus,boolean redactPHI) {

		for (IDocument doc : corpus.documents) {
			Debug.print("-------------------------"+doc.fileName+"------------------------");

			List<IAnnotation> anns=doc.testAnnotationRegistry.get(0, Integer.MAX_VALUE);

			Collections.sort(anns, new Comparator<IAnnotation>() {

				@Override
				public int compare(IAnnotation o1, IAnnotation o2) {
					// TODO Auto-generated method stub
					return o1.start - o2.start;
				}
			});

			StringBuffer buf = new StringBuffer(doc.unTaggedText);

			int offset = 0;
			for (IAnnotation ann : anns) {
				String label = ann.label;
				
				String text=ann.getText();
				if(redactPHI){
					text=StringUtils.repeat("X", ann.getText().length());
				}



				String replaceString = "[" + label + ":" + text + "]";

				buf.replace(ann.start + offset, ann.end + offset, replaceString);
				offset = offset - (ann.end - ann.start) + replaceString.length();
			}

			Debug.print(buf.toString());

		}

	}
	
	public static String printForEvaluation(RecordCorpus corpus) {
		
		StringBuffer allBuf=new StringBuffer();
		int id=1;
		for (IDocument doc : corpus.documents) {
			
			
			
			allBuf.append("-------------------------"+doc.fileName+"------------------------\n");
			

			List<IAnnotation> anns=doc.testAnnotationRegistry.get(0, Integer.MAX_VALUE);

			Collections.sort(anns, new Comparator<IAnnotation>() {

				@Override
				public int compare(IAnnotation o1, IAnnotation o2) {
					// TODO Auto-generated method stub
					return o1.start - o2.start;
				}
			});
			StringBuffer buf = new StringBuffer(doc.unTaggedText);

			int offset = 0;
			for (IAnnotation ann : anns) {
				String label = ann.label;
				
				if(label.contains("FN"))
					continue;
				
				String replaceText=ann.getText();
				
				if(label.contains("PHI")){
//					replaceText=StringUtils.repeat("X", ann.getText().length());
					replaceText="REDACTED";
					if(label.contains("FP"))
						replaceText=replaceText+":FP";
				} else{
					replaceText=label;
				}
				


				String replaceString = "["+(id++)+":" + replaceText + "]";

				buf.replace(ann.start + offset, ann.end + offset, replaceString);
				offset = offset - (ann.end - ann.start) + replaceString.length();
			}

			allBuf.append(buf.toString());

		}
		return allBuf.toString();

	}
	
	
	public static String printForEvaluation(RecordCorpus corpus,boolean binary) {
		
		int id=1;
		StringBuffer allBuf=new StringBuffer();
		for (IDocument doc : corpus.documents) {
			
			
			
			allBuf.append("-------------------------"+doc.fileName+"------------------------\n");
			

			List<IAnnotation> anns=doc.testAnnotationRegistry.get(0, Integer.MAX_VALUE);

			Collections.sort(anns, new Comparator<IAnnotation>() {

				@Override
				public int compare(IAnnotation o1, IAnnotation o2) {
					// TODO Auto-generated method stub
					return o1.start - o2.start;
				}
			});
			StringBuffer buf = new StringBuffer(doc.unTaggedText);

			int offset = 0;
			for (IAnnotation ann : anns) {
				String label = ann.label;
				
				if(label.contains("FN"))
					continue;
				
				Debug.print(ann.label);
				
				String replaceText=ann.getText();
				
				if(binary){
					replaceText="REDACTED";
					if(label.contains("FP"))
						replaceText=replaceText+":FP";
				} else{
					replaceText=label;
				}
				


				String replaceString = "["+(id++)+":" + replaceText + "]";

				buf.replace(ann.start + offset, ann.end + offset, replaceString);
				offset = offset - (ann.end - ann.start) + replaceString.length();
			}

			allBuf.append(buf.toString());

		}
		return allBuf.toString();

	}

	public static void getErrorStatistics(RecordCorpus corpus,boolean TP, boolean FP, boolean FN) {
		
		StringBuffer buf=new StringBuffer();
		
		if(TP){
			buf.append("------------TPs---------------\n");
			buf.append(getTokenStatistics(corpus.tokenEvalRecord.TPs)+"\n");
		}
		
		if(FP){
			buf.append("------------FPs---------------\n");
			buf.append(getTokenStatistics(corpus.tokenEvalRecord.FPs)+"\n");
		}
		
		if(FN){
			buf.append("------------FNs---------------\n");
			buf.append(getTokenStatistics(corpus.tokenEvalRecord.FNs)+"\n");
		}
		
		Debug.print(buf.toString());
	}

	public static String getTokenStatistics(List<IToken> tokens) {
		Map<String, List<String>> hmType2Count = new HashMap<String, List<String>>();
		for (IToken token : tokens) {
			String type = token.goldLabel;
			if (!hmType2Count.containsKey(type))
				hmType2Count.put(type, new ArrayList<>());

			hmType2Count.get(type).add(token.getText()+"_"+token.sentence.section.document.fileName);

		}
		List<String> types=new ArrayList<String>(hmType2Count.keySet());
		Collections.sort(types,new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return hmType2Count.get(o2).size()-hmType2Count.get(o1).size();
			}
			
		});
		
		StringBuffer buf=new StringBuffer();
		for(String type:types){
			buf.append(type+" --> "+hmType2Count.get(type).size()+" --> "+hmType2Count.get(type)+"\n");
		}
//		Debug.print(buf.toString());

		return buf.toString();
	}

	public static void printEval(RecordCorpus corpus, boolean printDocEval) {
		Debug.print(" ALL DOCS (N=" + corpus.documents.size() + ")");
		Debug.print(corpus.tokenEvalRecord);
		if (printDocEval) {
			
			List<IDocument> docs=new ArrayList<>(corpus.documents);
			Collections.sort(docs,new Comparator<IDocument>(){

				@Override
				public int compare(IDocument arg0, IDocument arg1) {
					if(arg1.tokenEvalRecord.FPs.size()>arg0.tokenEvalRecord.FPs.size())
						return 1;
					if(arg1.tokenEvalRecord.FPs.size()<arg0.tokenEvalRecord.FPs.size())
						return -1;
					
					if(arg1.tokenEvalRecord.FNs.size()>arg0.tokenEvalRecord.FNs.size())
						return 1;
					if(arg1.tokenEvalRecord.FNs.size()<arg0.tokenEvalRecord.FNs.size())
						return -1;

					return 0;
				}
				
			});
			
			for (IDocument doc : docs) {
				Debug.print(doc.fileName);
				Debug.print(doc.tokenEvalRecord);

			}
		}

	}

	public static void evaluateBinary(RecordCorpus corpus) {

		for (IDocument doc : corpus.documents) {

			for (IToken token : doc.getTokens()) {
				if (!token.goldLabel.equals("O") && !token.testLabel.equals("O"))
					doc.tokenEvalRecord.TPs.add(token);
				if (!token.goldLabel.equals("O") && token.testLabel.equals("O"))
					doc.tokenEvalRecord.FNs.add(token);
				if (token.goldLabel.equals("O") && !token.testLabel.equals("O"))
					doc.tokenEvalRecord.FPs.add(token);
			}
			corpus.tokenEvalRecord.mergeWith(doc.tokenEvalRecord);

		}

	}
	public static void evaluateCategory(RecordCorpus corpus) {

		for (IDocument doc : corpus.documents) {

			for (IToken token : doc.getTokens()) {
				if (!token.testLabel.equals("O")){
					if(token.testLabel.equals(token.goldLabel))
						doc.tokenEvalRecord.TPs.add(token);
					else
						doc.tokenEvalRecord.FPs.add(token);
				} else{
					if (!token.goldLabel.equals("O")){
						doc.tokenEvalRecord.FNs.add(token);

					}
				}
			}
			corpus.tokenEvalRecord.mergeWith(doc.tokenEvalRecord);

		}

	}
	

}
