package deid.classifiers.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import deid.Constants;
import deid.PHI;
import deid.anns.IAnnotation;
import deid.document.IDocument;
import deid.document.IToken;
import deid.document.RecordCorpus;
import deid.utils.Debug;
import deid.utils.intervaltree.IntervalTree;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

public class CRFUtil {

	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
		recordSet.loadFolder(new String[] { "113-05.xml" }, true);
		recordSet.partitionSections();
		recordSet.saveObj();
		// recordSet.loadObj();
		
//		labelTokensGold(recordSet.documents,1);

		for (IDocument doc : recordSet.documents.subList(0, 1)) {

			for (IToken token : doc.getTokens()) {
				Debug.print(token.getText() + "\t" + token.label);
			}

		}

	}

	// public static void labelTokensGold(IDocument doc) {
	// for (IToken token : doc.getTokens()) {
	// List<IAnnotation> phiAnns = doc.goldPHIRegistry.get(token.start, token.end);
	// String label = "O";
	// if (!phiAnns.isEmpty()) {
	// label = phiAnns.get(0).label;
	// }
	// token.goldLabel = label;
	// }
	// }

	public static void labelTokensGold(List<IDocument> docs,String phiType) { 
		for (IDocument doc : docs) {
			for (IToken token : doc.getTokens()) {
				List<IAnnotation> phiAnns = doc.goldPHIRegistry.get(token.start, token.end);
				String label = "O";
				if (!phiAnns.isEmpty()) {
					if(phiType.equals("BINARY"))
						label="PHI";
					if(phiType.equals("SUBCATEGORY"))
						label=phiAnns.get(0).label;
					if(phiType.equals("CATEGORY"))
						label = PHI.hmSubCat2Cat.get(phiAnns.get(0).label);
					
				}
				token.goldLabel = label;
			}
		}
	}

	
	public static void labelTokensTest(List<IDocument> docs) {
		for (IDocument doc : docs) {			
			for (IToken token : doc.getTokens()) {
				List<IAnnotation> anns = doc.testTokenRegistry.get(token.start, token.end);
				String label = "O";
				if (!anns.isEmpty()) {
					label = anns.get(0).label;
				}
				token.testLabel = label;
			}
		}
	}
	
	public static void mergeTokens(List<IDocument> docs) {
		
		Map<String,Integer> hmDoc2FPCount=new HashMap<>();
		
		for (IDocument doc : docs) {
			
			List<IToken> tokens=doc.getTokens();
			
			for (IToken token : tokens) {
//				List<IAnnotation> goldAnns = doc.gold.get(token.start, token.end);
//				List<IAnnotation> testAnns = doc.testTokenRegistry.get(token.start, token.end);
				
				String label = null;
				String goldLabel=null;
				if(!token.goldLabel.equals("O"))
					goldLabel=token.goldLabel;
				String testLabel=null;
				if(!token.testLabel.equals("O"))
					testLabel=token.testLabel;
				
				
				// evaluation
				if(testLabel!=null){
					if(goldLabel!=null &&goldLabel.equals(testLabel))
						label=testLabel+"";
					else
						label=testLabel+":FP";
				} else{
					if(goldLabel!=null)
						label=testLabel+":FN";
				}
				
				
			
				if(label==null)
					label = "O";
				
				token.testDebugLabel = label;
			}
			
			
			List<IAnnotation> mergedAnns=new ArrayList<>();
			
			IAnnotation currAnn=null;

			int FPCount=0;
			for (IToken token :tokens) {
				
				if(!token.testDebugLabel.equals("O")){
					boolean newAnn=false;
					
					if(currAnn==null)
						newAnn=true;
					else if (!currAnn.label.equals(token.testDebugLabel)){
						newAnn=true;
					} else{
						String midText=doc.unTaggedText.substring(currAnn.end, token.start);
						if(midText.contains("\n")){
							newAnn=true;
						}
					}
					
					if(newAnn){
						currAnn=new IAnnotation(token.start,token.end,token.getText(),token.source);
						currAnn.label=token.testDebugLabel;
						mergedAnns.add(currAnn);
						
						if(currAnn.label.contains("FP"))
							FPCount++;
					} else{
						currAnn.end=token.end;
						currAnn.setText(doc.unTaggedText.substring(currAnn.start,currAnn.end));
					}
					
					
				} else{
					currAnn=null;
				}
				
			}
			
			hmDoc2FPCount.put(doc.fileName, FPCount);
			
			doc.testAnnotationRegistry=new IntervalTree<>();
			
			for(IAnnotation ann:mergedAnns){
				doc.testAnnotationRegistry.addInterval(ann.start, ann.end, ann);
			}
		}
		
		
		List<String> documents=new ArrayList<>(hmDoc2FPCount.keySet());
		Collections.sort(documents,new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return hmDoc2FPCount.get(arg1)-hmDoc2FPCount.get(arg0);
			}
		});
		for(String doc:documents){
			Debug.print(doc+"\t"+hmDoc2FPCount.get(doc));
		}
	}

	/* CRF model */
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

	public static List<IAnnotation> getAnnotations(String text, AbstractSequenceClassifier<CoreLabel> classifier) {
		List<IAnnotation> anns = new ArrayList<>();
		List<Triple<String, Integer, Integer>> triples = classifier.classifyToCharacterOffsets(text);
		for (Triple<String, Integer, Integer> trip : triples) {
			int start = trip.second();
			int end = trip.third();
			String label = trip.first();
			String annText = text.substring(start, end);

			IAnnotation ann = new IAnnotation(start, end, annText, "CRF");
			ann.label = label;

			anns.add(ann);

		}
		return anns;
	}

}
