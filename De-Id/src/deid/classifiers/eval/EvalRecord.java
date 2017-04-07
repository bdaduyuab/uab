package deid.classifiers.eval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import deid.utils.StringFormat;

public class EvalRecord<T> implements Serializable{
	public List<T> TPs = new ArrayList<T>();
	public List<T> FPs = new ArrayList<T>();
	public List<T> FNs = new ArrayList<T>();

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("TP="+TPs.size()+" FP="+FPs.size()+" FN="+FNs.size()+"\n");
		buf.append("Recall="+getRecall()+"\nPrecision="+getPrecision()+"\nF-Measure="+getFScore()+"\n");


		return buf.toString();
	}

	public String toString2() {

		return String.format("%-10s%-10s%-10s%-10s%-10s%-10s", TPs.size(), FPs.size(), FNs.size(), StringFormat.numberRound4(getRecall()), StringFormat.numberRound4(getPrecision()), StringFormat.numberRound4(getFScore()));

	}

	public double getFScore() {
		return 2 * getRecall() * getPrecision() / (getRecall() + getPrecision());
	}

	public double getPrecision() {
		if (TPs.size() == 0 && FPs.size() == 0)
			return 1.0;
		return 1.0 * TPs.size() / (TPs.size() + FPs.size());
	}

	public double getRecall() {
		return 1.0 * TPs.size() / (TPs.size() + FNs.size());
	}

	public void mergeWith(EvalRecord<T> triple) {
		TPs.addAll(triple.TPs);
		FPs.addAll(triple.FPs);
		FNs.addAll(triple.FNs);
	}

	public int getTotalMatched() {
		return TPs.size() + FPs.size();
	}

}
