package deid.utils.para;

import java.util.concurrent.Callable;

import deid.Constants;
import deid.document.IDocument;
import deid.utils.ObjectUtil;

public class CallableWriteDocumentObj implements Callable {
	public IDocument document;// output

	public CallableWriteDocumentObj(IDocument document) {
		super();

		this.document = document;

	}

	@Override
	public Object call() throws Exception {

		ObjectUtil.save(Constants.OBJECT + "/" + document.fileName + "", document);

		return this;
	}

}
