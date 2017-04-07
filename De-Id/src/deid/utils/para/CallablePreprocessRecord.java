package deid.utils.para;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import deid.Constants;
import deid.document.IDocument;
import deid.document.ISection;
import deid.document.ISentence;
import deid.document.IToken;
import deid.utils.FileUtil;
import deid.utils.RegexUtils;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CallablePreprocessRecord implements Callable {
	public IDocument inputRecord;

	public List<String> headingTerms = FileUtil.readFileByline(Constants.SECTION_HEADING);
	public String regex = RegexUtils.buildRegex(headingTerms);
	public Pattern pattern = Pattern.compile(regex);

	public StanfordCoreNLP corenlp = getCoreNLP();

	private StanfordCoreNLP getCoreNLP() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		props.setProperty("tokenize.options", "tokenizeNLs=true");

		return new StanfordCoreNLP(props);
	}

	public CallablePreprocessRecord(IDocument inputRecord) {
		this.inputRecord = inputRecord;
	}

	@Override
	public Object call() throws Exception {
		splitSection(inputRecord);

		return this;
	}

	public List<ISection> splitSection(IDocument document) {
		String text = document.getText();
		Matcher m = pattern.matcher(document.unTaggedText);

		document.sections = new ArrayList<>();
		ISection rootSection = new ISection(document, 0, 0, null, null);
		rootSection.titleSentence = new ISentence(rootSection, 0, 0, null, null);
		document.sections.add(rootSection); // root heading;

		while (m.find()) { // heading tokenizer
			ISection section = new ISection(document, m.start(), -1, null, null);
			document.sections.add(section);

			section.titleSentence = new ISentence(section, m.start(), m.end(), null, null);

			Annotation annotation = new Annotation(text.substring(section.titleSentence.start, section.titleSentence.end));

			corenlp.annotate(annotation);

			List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

			for (CoreMap sentence : sentences) {

				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					int tstart = section.start + token.beginPosition();
					int tend = section.start + token.endPosition();

					IToken itoken = new IToken(section.titleSentence, tstart, tend, text.substring(tstart, tend), null);
					itoken.normText = token.get(TextAnnotation.class);
					itoken.pos = token.get(PartOfSpeechAnnotation.class);
					itoken.lemma = token.get(LemmaAnnotation.class);
					itoken.lemmaLC = itoken.lemma.toLowerCase();

					section.titleSentence.tokens.add(itoken);
					document.tokenRegistry.addInterval(itoken.start, itoken.end, itoken);

				}

			}

			section.titleSentence.linkToken();
		}

		/* tokenize section content */
		for (int i = 0; i < document.sections.size(); i++) {
			ISection currSection = document.sections.get(i);
			int contentStart = currSection.titleSentence.end;
			int contentEnd = -1;
			if (i < document.sections.size() - 1) {
				ISection nextHeading = document.sections.get(i + 1);
				contentEnd = nextHeading.start;
			} else {
				contentEnd = text.length();
			}
			currSection.end = contentEnd;

			if (contentEnd > contentStart) {
				String content = text.substring(contentStart, contentEnd);

				Annotation annotation = new Annotation(content);

				corenlp.annotate(annotation);

				List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);

				for (CoreMap sentence : sentences) {

					ISentence isentence = new ISentence(currSection, -1, -1, null, null);
					currSection.contentSentences.add(isentence);

					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
						int tstart = contentStart + token.beginPosition();
						int tend = contentStart + token.endPosition();

						IToken itoken = new IToken(isentence, tstart, tend, text.substring(tstart, tend), null);
						itoken.normText = token.get(TextAnnotation.class);
						itoken.pos = token.get(PartOfSpeechAnnotation.class);
						itoken.lemma = token.get(LemmaAnnotation.class);
						itoken.lemmaLC = itoken.lemma.toLowerCase();

						isentence.tokens.add(itoken);
						document.tokenRegistry.addInterval(itoken.start, itoken.end, itoken);

					}
					isentence.start = isentence.tokens.get(0).start;
					isentence.end = isentence.tokens.get(isentence.tokens.size() - 1).end;
					isentence.linkToken();

				}
			}

		}

		return document.sections;
	}

}
