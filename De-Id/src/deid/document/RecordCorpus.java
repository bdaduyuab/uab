package deid.document;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import deid.Constants;
import deid.classifiers.eval.EvalRecord;
import deid.utils.Debug;
import deid.utils.PHIParser2014;
import deid.utils.para.CallablePreprocessRecord;
import deid.utils.para.CallableReadDocumentObj;
import deid.utils.para.CallableWriteDocumentObj;

public class RecordCorpus {
	public List<IDocument> documents;

	public Map<String, IDocument> hmDocId2Record;
	public String folderPath;
	
	public EvalRecord<IToken> tokenEvalRecord=new EvalRecord<IToken>();

	public RecordCorpus(String folderPath) {
		this.folderPath = folderPath;
		// loadFolder(folderPath);
	}

	public void loadFolder(String[] Ids,boolean hipaaOnly) {
		loadFolder(new ArrayList<String>(Arrays.asList(Ids)), -1,hipaaOnly);
	}

	public void loadFolder(int limit,boolean hipaaOnly) {
		loadFolder(null, limit,hipaaOnly);
	}

	public void loadFolder(boolean hipaaOnly) {
		loadFolder(null, -1,hipaaOnly);
	}

	public void loadFolder(Collection<String> Ids, int limit, boolean hipaaOnly) {

		documents = new ArrayList<IDocument>();
		hmDocId2Record = new HashMap<String, IDocument>();

		File folder = new File(folderPath);
		int count = 0;

		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".xml")) {
				if (Ids != null && !Ids.contains(file.getName()))
					continue;

				IDocument rec = PHIParser2014.parse(file.getAbsolutePath(),hipaaOnly);

				rec.fileName = file.getName();
				documents.add(rec);
				hmDocId2Record.put(rec.fileName, rec);

				count++;
				if (limit > 0 && count >= limit)
					break;
			}

		}

	}

	/* get documents */
	public IDocument getDocument(String id,boolean hipaaOnly) {
		loadFolder(new String[] { id },hipaaOnly);
		return documents.get(0);
	}

	public List<IDocument> getDocuments(String[] docIds,boolean hipaaOnly) {
		loadFolder(docIds,hipaaOnly);
		return documents;
	}

	/* Partition */

	public void saveObj() {

		List<Callable<CallableWriteDocumentObj>> threads = new ArrayList<>();

		ExecutorService executor = Executors.newFixedThreadPool(10);

		for (IDocument document : documents) {
			CallableWriteDocumentObj thread = new CallableWriteDocumentObj(document);
			threads.add(thread);
		}

		try {
			List<Future<CallableWriteDocumentObj>> futures = executor.invokeAll(threads);

			for (Future<CallableWriteDocumentObj> future : futures) {
				future.get();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdown();
		Debug.print("Finished Save");

	}

	public void loadObj() {

		List<Callable<CallableReadDocumentObj>> threads = new ArrayList<>();

		ExecutorService executor = Executors.newFixedThreadPool(10);

		for (IDocument document : documents) {
			CallableReadDocumentObj thread = new CallableReadDocumentObj(document);
			threads.add(thread);
		}

		documents = new ArrayList<>();
		try {
			List<Future<CallableReadDocumentObj>> futures = executor.invokeAll(threads);

			for (Future<CallableReadDocumentObj> future : futures) {
				CallableReadDocumentObj obj = future.get();
				documents.add(obj.inputRecord);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdown();
		Debug.print("Finished Load");

	}

	public void partitionSections() {

		ExecutorService executor = Executors.newFixedThreadPool(10);

		List<Callable<CallablePreprocessRecord>> threads = new ArrayList<>();

		for (int i = 0; i < documents.size(); i++) {
			threads.add(new CallablePreprocessRecord(documents.get(i)));
		}

		try {
			List<Future<CallablePreprocessRecord>> futures = executor.invokeAll(threads);

			for (Future<CallablePreprocessRecord> future : futures) {
				CallablePreprocessRecord thread = future.get();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executor.shutdown();

	}

	public static void main(String[] args) {
		RecordCorpus recordSet = new RecordCorpus(Constants.TEST);
		recordSet.loadFolder(new String[] { "113-05.xml" },false);
		recordSet.partitionSections();
		 recordSet.saveObj();
//		recordSet.loadObj();
		
		

		for (IDocument doc : recordSet.documents.subList(0, 1)) {
			Debug.print(doc.filePath);
			for (IToken token : doc.getTokens()) {
				Debug.print(token+"\t"+token.start+"\t"+token.end+"\t"+token.goldLabel);
			}

		}
	}

}
