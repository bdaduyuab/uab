package deid;

import deid.document.IDocument;
import deid.document.RecordCorpus;

public class Preprocessing {

	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TRAIN);
		recordSet.loadFolder(true);
		recordSet.partitionSections();
		recordSet.saveObj();
		
		
		recordSet = new RecordCorpus(Constants.TEST);
		recordSet.loadFolder(true);
		recordSet.partitionSections();
		recordSet.saveObj();
		

	}

}
