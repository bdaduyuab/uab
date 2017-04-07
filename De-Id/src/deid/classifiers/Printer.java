package deid.classifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import deid.Constants;
import deid.anns.IAnnotation;
import deid.classifiers.ml.CRFAnnotator;
import deid.classifiers.ml.CRFUtil;
import deid.document.IDocument;
import deid.document.IToken;
import deid.document.RecordCorpus;
import deid.utils.Debug;

public class Printer {
	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
		recordSet.loadFolder(new String[] { "113-05.xml" }, true);
		// recordSet.loadFolder(true);

		recordSet.loadObj();

		CRFUtil.labelTokensGold(recordSet.documents, "CATEGORY");

		// // print gold anns
		// for (IDocument doc : recordSet.documents) {
		// print(doc, doc.goldPHIRegistry.get(0, Integer.MAX_VALUE));
		// }

		// print tokens anns
		for (IDocument doc : recordSet.documents) {
			printTokens(doc, doc.getTokens());
		}

	}

	public static void printTokens(IDocument doc, List<IToken> anns) {

		Collections.sort(anns, new Comparator<IToken>() {

			@Override
			public int compare(IToken o1, IToken o2) {
				// TODO Auto-generated method stub
				return o1.start - o2.start;
			}
		});

		StringBuffer buf = new StringBuffer(doc.unTaggedText);

		int offset = 0;
		for (IToken ann : anns) {
			String label = ann.goldLabel;

			String replaceString = "[" + label + ":" + ann.getText() + "]";
			
			if(label.equals("O"))
				replaceString=ann.getText();
			
			buf.replace(ann.start + offset, ann.end + offset, replaceString);
			offset = offset - (ann.end - ann.start) + replaceString.length();
		}

		Debug.print(buf.toString());

	}

	public static void print(IDocument doc, List<IAnnotation> anns) {

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

			String replaceString = "[" + label + ":" + ann.getText() + "]";
			buf.replace(ann.start + offset, ann.end + offset, replaceString);
			offset = offset - (ann.end - ann.start) + replaceString.length();
		}

		Debug.print(buf.toString());

	}

}
