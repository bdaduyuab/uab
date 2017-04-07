package deid.utils.dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import deid.utils.Debug;
import deid.utils.FileUtil;
import deid.utils.StringFormat;

public class HumanNames {
	
	public static String HUMAN_NAME_LEXICON="resources/dictionary/human/name.token.all";
	
	public static void main(String[] args) {

		Set<String> termSets = new HashSet<>();

		List<String> lines = FileUtil.readFileByline("resources/dictionary/human/dist.all.last");
		lines.addAll(FileUtil.readFileByline("resources/dictionary/human/dist.female.first"));
		lines.addAll(FileUtil.readFileByline("resources/dictionary/human/dist.male.first"));

		for (String line : lines) {
			String[] parts = line.split("\\s+");
			
			String name=parts[0].toLowerCase();
			
			
			termSets.add(name);
			termSets.add(name.substring(0, 1).toUpperCase()+name.substring(1));
			termSets.add(name.toUpperCase());
			
			if(name.length()==1){
				Debug.print(name);
			}

		}
		
		ArrayList<String> terms=new ArrayList<>(termSets);
		Collections.sort(terms);
		
		String text=StringFormat.listToString(terms, "\n").trim();
		
//		FileUtil.writeFile(text, HUMAN_NAME_LEXICON, false);
		
		Debug.print(termSets.size());

	}
}
