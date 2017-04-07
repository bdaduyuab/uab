package deid.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegexUtils {

	
	public static String buildRegex(List<String> terms) {
		Collections.sort(terms, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {

				return o2.length() - o1.length();
			}
		});

		StringBuffer buf = new StringBuffer();
		for (String string : terms) {
			string = getRegex(string, false);
			if (buf.length() > 0)
				buf.append("|");

			buf.append(string);
		}
		return buf.toString();

	}

	public static String getRegex(String term, boolean abstractNumber) {
		String s = term.trim().replaceAll("([\\*+\\[\\]\\^\\$\\|)(\\.?\\\\])", "\\\\$1").replaceAll("\\s+", "\\\\s+");

		if (abstractNumber)
			s = s.replaceAll("\\d+", "\\\\d+");

		return s;
	}

	public static void main(String[] args) {
		Debug.print(getRegex("2 A.\nAmbrose", true));
	}

}
