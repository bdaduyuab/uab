package deid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PHI {
	
	public static List<String> hipaaTypes=getHIPAAs(); 
	
	private static List<String> getHIPAAs() {
		List<String> evalOrders = new ArrayList<>();

		evalOrders.add("PATIENT");
		evalOrders.add("AGE");
		evalOrders.add("CITY");
		evalOrders.add("STREET");
		evalOrders.add("ZIP");
		evalOrders.add("ORGANIZATION");
		evalOrders.add("DATE");
		evalOrders.add("PHONE");
		evalOrders.add("FAX");
		evalOrders.add("EMAIL");
		evalOrders.add("MEDICALRECORD");
		evalOrders.add("HEALTHPLAN");
		evalOrders.add("LICENSE");
		evalOrders.add("DEVICE");
		evalOrders.add("BIOID");
		evalOrders.add("IDNUM");

		evalOrders.add("USERNAME");
		evalOrders.add("DOCTOR");
		evalOrders.add("HOSPITAL");
		evalOrders.add("COUNTRY");
		evalOrders.add("STATE");
		evalOrders.add("LOCATION-OTHER");
		evalOrders.add("PROFESSION");
		evalOrders.add("URL");

		return evalOrders;
	}
	
	public static Map<String, String> hmSubCat2Cat=getMapping();
	
	private static Map<String, String> getMapping() {
		Map<String, String> map = new HashMap<String, String>();

		map.put("DATE", "DATE");
		map.put("DOCTOR", "NAME");
		map.put("PATIENT", "NAME");
		map.put("USERNAME", "NAME");
		map.put("AGE", "AGE");
		map.put("PHONE", "CONTACT");
		map.put("FAX", "CONTACT");
		map.put("EMAIL", "CONTACT");
		map.put("URL", "CONTACT");
		map.put("LICENSE", "ID");
		map.put("MEDICALRECORD", "ID");
		map.put("IDNUM", "ID");
		map.put("DEVICE", "ID");
		map.put("BIOID", "ID");
		map.put("HEALTHPLAN", "ID");
		map.put("HOSPITAL", "LOCATION");
		map.put("CITY", "LOCATION");
		map.put("STATE", "LOCATION");
		map.put("STREET", "LOCATION");
		map.put("ZIP", "LOCATION");
		map.put("ORGANIZATION", "LOCATION");
		map.put("COUNTRY", "LOCATION");
		map.put("LOCATION-OTHER", "LOCATION");
		map.put("PROFESSION", "PROFESSION");

		return map;
	}

}
