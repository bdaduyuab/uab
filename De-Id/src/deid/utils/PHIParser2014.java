package deid.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import deid.PHI;
import deid.anns.IAnnotation;
import deid.document.IDocument;

public class PHIParser2014 extends DefaultHandler {

	public boolean hipaaOnly=false;
	
	public static List<String> hipaaPHIs=PHI.hipaaTypes;
	
	public PHIParser2014( boolean hipaaOnly){
		this.hipaaOnly=hipaaOnly;
	}
	
	
	boolean isBeginTags = false;
	boolean isBeginText = false;
	
	

	IDocument currentRecord;

	String lastTag = null;

	IAnnotation currentPHI;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		lastTag = qName;

		if (qName.equals("deIdi2b2") || qName.equals("NGRID_deId")) {
			currentRecord = new IDocument();
		}

		if (qName.equals("TAGS")) {
			isBeginTags = true;
			return;
		}
		if (qName.equals("TEXT")) {
			isBeginText = true;
			return;
		}

		if (isBeginTags) {
			IAnnotation phi = new IAnnotation();
//			phi.type = qName;
			phi.label = attributes.getValue("TYPE");
			phi.id = attributes.getValue("id");

			phi.start = Integer.parseInt(attributes.getValue("start"));
			phi.end = Integer.parseInt(attributes.getValue("end"));

			phi.setText(currentRecord.unTaggedText.substring(phi.start, phi.end));

			
			if(hipaaOnly && !hipaaPHIs.contains(phi.label)){
				return;
			}

			currentRecord.goldPHIs.add(phi);
			currentRecord.goldPHIRegistry.addInterval(phi.start,phi.end,phi);

		}

	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("TAGS")) {
			isBeginTags = false;
		}
		if (qName.equals("TEXT")) {
			isBeginText = false;
		}

	}

	public void characters(char ch[], int start, int length) throws SAXException {
		String s = new String(ch, start, length);

		if (isBeginText) {
			currentRecord.unTaggedText.append(s);
		}

	}

	/**
	 * @param args
	 */
	public static IDocument parse(String filePath, boolean hipaaOnly) {
		PHIParser2014 handler = new PHIParser2014(hipaaOnly);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(filePath, handler);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		File file = new File(filePath);
//		for (IAnnotation phi : handler.currentRecord.goldPHIs) {
//			phi.fileName = file.getName();
//		}

		handler.currentRecord.fileName = file.getName();
		handler.currentRecord.filePath = file.getAbsolutePath();

		return handler.currentRecord;
	}

	public static List<IDocument> importRecords(String folderPath,boolean hipaaOnly) {
		List<IDocument> records = new ArrayList<IDocument>();
		File folder = new File(folderPath);
		for (File file : folder.listFiles()) {
			// System.out.print(file.getName());
			IDocument rec = parse(file.getAbsolutePath(),hipaaOnly);
			rec.fileName = file.getName();
			rec.filePath = file.getAbsolutePath();
			records.add(rec);
		}
		return records;
	}

//	public static void genertateTrainingFile(List<Record> records, String modelFile, String taggedTokenDir) {
//		StringBuffer buf = new StringBuffer();
//		for (Record rec : records) {
//			buf.append("BEGIN\tO\n");
//			StringBuffer recBuf = new StringBuffer();
//			for (TokenAnn token : rec.tokens) {
//				recBuf.append(token.normText + "\t" + token.trainingLabel + "\n");
//				buf.append(token.normText + "\t" + token.trainingLabel + "\n");
//			}
//			FileUtil.writeFile(recBuf.toString(), taggedTokenDir + "/" + rec.fileName + ".tsv", false);
//
//			buf.append("END\tO\n");
//		}
//		// Debug.print(buf.toString());
//		FileUtil.writeFile(buf.toString(), modelFile, false);
//	}

	public static void main(String[] args) {

		// Record rec =
		// parse("resources/i2b2_2014/training-PHI-Gold-Set1/220-01.xml");
		//
		// rec.tokenize();
		//
		// rec.printAndValidateTokens();

		List<IDocument> recs = importRecords("resources/i2b2_2014/training-PHI-Gold-Set1",false);
		// List<Record>
		// recs=importRecords("resources/i2b2_2014/testing-PHI-Gold-fixed");

		for (IDocument rec : recs) {
			System.out.println(rec.fileName);
			// rec.tokenize();
			// rec.labelingTokensIOB();
			// rec.printAndValidatePHI();
		}
//		genertateTrainingFile(recs, "resources/i2b2_2014/crf-training-input.tsv", "resources/i2b2_2014/training-PHI-Gold-Set12");

	}

}
