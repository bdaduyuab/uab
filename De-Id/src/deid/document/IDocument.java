package deid.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import deid.anns.IAnnotation;
import deid.classifiers.eval.EvalRecord;
import deid.utils.intervaltree.IntervalTree;

public class IDocument implements Serializable {

	public String fileName;
	public String filePath;

	public StringBuffer unTaggedText = new StringBuffer();
	private String text;

	/* Annotation Training */
	public IntervalTree<IAnnotation> goldPHIRegistry = new IntervalTree<>();
	public List<IAnnotation> goldPHIs = new ArrayList<>();

	/* Annotation Testing */
	public IntervalTree<IAnnotation> testTokenRegistry = new IntervalTree<IAnnotation>();
	public IntervalTree<IAnnotation> testAnnotationRegistry = new IntervalTree<IAnnotation>();

	

	/* Document Structure */
	public List<ISection> sections;
	public IntervalTree<IAnnotation> tokenRegistry = new IntervalTree<IAnnotation>();

	
	
	// evaluation
	public EvalRecord<IToken> tokenEvalRecord=new EvalRecord<IToken>();
	
	public String getText() {
		if (text == null)
			text = unTaggedText.toString();
		return text;
	}

	public String getTextPHITagged(List<IAnnotation> phis) {
		StringBuffer buf = new StringBuffer(unTaggedText);

		int offset = 0;
		for (IAnnotation phi : phis) {
			String replaceString = "[" + phi.label + ":" + phi.getText() + "]";
			buf.replace(phi.start + offset, phi.end + offset, replaceString);
			offset = offset - (phi.end - phi.start) + replaceString.length();
		}

		return buf.toString();
	}

	public String getGoldPHITagged() {
		return getTextPHITagged(goldPHIs);
	}

	public String getDebugContext(int start, int end, int windows) {
		String left = unTaggedText.substring(Math.max(0, start - windows), start);
		String right = unTaggedText.substring(end, Math.min(unTaggedText.length(), end + windows));
		return fileName + ":" + left + "[" + unTaggedText.substring(start, end) + "]" + right;
	}

	/* Tokens */
	public List<IToken> getTokens() {
		List<IToken> tokens = new ArrayList<>();

		for (ISection sec : sections) {
			tokens.addAll(sec.getAllTokens());

		}

		return tokens;
	}
	
	

	// /*Section Test*/
	// public void printSection(){
	// for(ISection sec:sections){
	// Debug.print(sec.titleSentence);
	// }
	// }
	// public void printSectionTokens(){
	// for(ISection sec:sections){
	// for(IToken token:sec.titleSentence.tokens){
	// Debug.print(token.getText());
	// }
	//
	// }
	// }

}
