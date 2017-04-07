//package deid.document;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import deid.utils.Constants;
//import deid.utils.Debug;
//import deid.utils.FileUtil;
//import deid.utils.RegexUtils;
//import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
//import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
//import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
//import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
//import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
//import edu.stanford.nlp.ling.CoreLabel;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.util.CoreMap;
//
//public class ISectionUtil {
//
//	public static List<String> headingTerms = FileUtil.readFileByline("resources/gazette/SectionHeading.txt");
//	public static String regex = RegexUtils.buildRegex(headingTerms);
//	public static Pattern pattern = Pattern.compile(regex);
//
//	public static StanfordCoreNLP corenlp = getCoreNLP();
//
//	private static StanfordCoreNLP getCoreNLP() {
//		Properties props = new Properties();
//		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
//		props.setProperty("tokenize.options", "tokenizeNLs=true");
//
//		return new StanfordCoreNLP(props);
//	}
//
//	public static List<ISection> splitSection(IDocument document) {
//		String text = document.getText();
//		Matcher m = pattern.matcher(document.unTaggedText);
//
//		List<ISection> sections = new ArrayList<>();
//		ISection rootSection = new ISection(document, 0, 0, null, null);
//		rootSection.titleSentence = new ISentence(rootSection, 0, 0, null, null);
//		sections.add(rootSection); // root heading;
//
//		while (m.find()) { // heading tokenizer
//			ISection section = new ISection(document, m.start(), -1, null, null);
//			sections.add(section);
//
//			section.titleSentence = new ISentence(section, m.start(), m.end(), null, null);
//
//			Annotation annotation = new Annotation(text.substring(section.titleSentence.start, section.titleSentence.end));
//
//			corenlp.annotate(annotation);
//
//			List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
//
//			for (CoreMap sentence : sentences) {
//
//				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
//					int tstart = section.start + token.beginPosition();
//					int tend = section.start + token.endPosition();
//
//					IToken itoken = new IToken(section.titleSentence, tstart, tend, text.substring(tstart, tend), null);
//					itoken.normText = token.get(TextAnnotation.class);
//					itoken.pos = token.get(PartOfSpeechAnnotation.class);
//					itoken.lemma = token.get(LemmaAnnotation.class);
//					itoken.lemmaLC = itoken.lemma.toLowerCase();
//					
//
//					section.titleSentence.tokens.add(itoken);
//					document.tokenRegistry.addInterval(itoken.start, itoken.end, itoken);
//
//				}
//
//			}
//
//			section.titleSentence.linkToken();
//		}
//
//		/* tokenize section content */
//		for (int i = 0; i < sections.size(); i++) {
//			ISection currSection = sections.get(i);
//			int contentStart = currSection.titleSentence.end;
//			int contentEnd = -1;
//			if (i < sections.size() - 1) {
//				ISection nextHeading = sections.get(i + 1);
//				contentEnd = nextHeading.start;
//			} else {
//				contentEnd = text.length();
//			}
//			currSection.end = contentEnd;
//
//			if (contentEnd > contentStart) {
//				String content = text.substring(contentStart, contentEnd);
//
//				Annotation annotation = new Annotation(content);
//
//				corenlp.annotate(annotation);
//
//				List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
//
//				for (CoreMap sentence : sentences) {
//
//					ISentence isentence = new ISentence(currSection, -1, -1, null, null);
//					currSection.contentSentences.add(isentence);
//
//					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
//						int tstart = contentStart + token.beginPosition();
//						int tend = contentStart + token.endPosition();
//
//						IToken itoken = new IToken(isentence, tstart, tend, text.substring(tstart, tend), null);
//						itoken.normText = token.get(TextAnnotation.class);
//						itoken.pos = token.get(PartOfSpeechAnnotation.class);
//						itoken.lemma = token.get(LemmaAnnotation.class);
//						itoken.lemmaLC = itoken.lemma.toLowerCase();
//						
//
//						isentence.tokens.add(itoken);
//						document.tokenRegistry.addInterval(itoken.start, itoken.end, itoken);
//
//					}
//					isentence.start = isentence.tokens.get(0).start;
//					isentence.end = isentence.tokens.get(isentence.tokens.size() - 1).end;
//					isentence.linkToken();
//
//				}
//			}
//
//		}
//
//		return sections;
//	}
//
//	public static void main(String[] args) {
//
//		RecordSet docSet = new RecordSet(Constants.TEST_DIR3);
//
//		List<IDocument> records = docSet.loadAll();
//
//		records = docSet.load(new String[] { "0028_gs.xml","0029_gs.xml","0028_gs.xml","0028_gs.xml" });
//
//		IDocumentCollection docCol = new IDocumentCollection(records);
//
//		docCol.partitionSections();
//		
//		for(IDocument doc:docCol.documents){
//			Debug.print(doc.toString2());
//		}
//
//	}
//}
