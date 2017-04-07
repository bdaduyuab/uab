package deid.utils.para;

import java.util.concurrent.Callable;

import deid.Constants;
import deid.document.IDocument;
import deid.utils.ObjectUtil;

public class CallableReadDocumentObj implements Callable {
	public IDocument inputRecord;

	public CallableReadDocumentObj(IDocument inputRecord) {
		super();

		this.inputRecord = inputRecord;

	}

	@Override
	public Object call() throws Exception {
		inputRecord = (IDocument) ObjectUtil.load(Constants.OBJECT + "/" + inputRecord.fileName + "");

		return this;
	}
}
