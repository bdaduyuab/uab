//package deid.anns;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import deid.document.IDocument;
//import deid.utils.Debug;
//import deid.utils.intervaltree.IntervalTree;
//
//public class PHIAnnotation extends IAnnotation {
//
//	public String fileName;
//	public String type;
//	public String subType;
//	// public String text="";
//	public String method;
//
//	public int priority = 2;
//	
//	public static boolean DEBUG_COMBINE=false;
//
//	public PHIAnnotation() {
//
//	}
//	
//	public PHIAnnotation clone(){
//		PHIAnnotation phi=new PHIAnnotation(priority, priority, text);
//		phi.fileName=this.fileName;
//		phi.type=this.type;
//		phi.subType=this.subType;
//		phi.method=this.method;
//		phi.priority=this.priority;
//		
//		return phi;
//	}
//
//	public PHIAnnotation(int start, int end, String text) {
//		super(start, end, text,null);
//		// TODO Auto-generated constructor stub
//	}
//	public PHIAnnotation(IAnnotation ann,String label) {
//		super(ann.start, ann.end, ann.text,null);
//		subType=label;
//		// TODO Auto-generated constructor stub
//	}
//
//	public static Map<String, String> hmSubType2Type = getMapping();
//
//	public String toString2() {
//		return "[" + id + " " + fileName + " " + subType + " " + start + " " + end + " " + text + " " + priority + " " + method + "]";
//	}
//	
//	
//	public String toString4() {
//		return subType+": " +text + " ["+start+","+end+"]";
//	}
//	public String toString() {
//		return text ;
//	}
//
//	public boolean equals(PHIAnnotation otherPHI) {
//		if (start == otherPHI.start && end == otherPHI.end)
//			return true;
//		else
//			return false;
//	}
//
//	private static Map<String, String> getMapping() {
//		Map<String, String> map = new HashMap<String, String>();
//
//		map.put("DATE", "DATE");
//		map.put("DOCTOR", "NAME");
//		map.put("PATIENT", "NAME");
//		map.put("USERNAME", "NAME");
//		map.put("AGE", "AGE");
//		map.put("PHONE", "CONTACT");
//		map.put("FAX", "CONTACT");
//		map.put("EMAIL", "CONTACT");
//		map.put("URL", "CONTACT");
//		map.put("LICENSE", "ID");
//		map.put("MEDICALRECORD", "ID");
//		map.put("IDNUM", "ID");
//		map.put("DEVICE", "ID");
//		map.put("BIOID", "ID");
//		map.put("HEALTHPLAN", "ID");
//		map.put("HOSPITAL", "LOCATION");
//		map.put("CITY", "LOCATION");
//		map.put("STATE", "LOCATION");
//		map.put("STREET", "LOCATION");
//		map.put("ZIP", "LOCATION");
//		map.put("ORGANIZATION", "LOCATION");
//		map.put("COUNTRY", "LOCATION");
//		map.put("LOCATION-OTHER", "LOCATION");
//		map.put("PROFESSION", "PROFESSION");
//
//		return map;
//	}
//
//	public static List<PHIAnnotation> combine2(List<PHIAnnotation> phis) {
//		List<PHIAnnotation> allPhis = new ArrayList<>(phis);
//		Collections.sort(allPhis, new Comparator<PHIAnnotation>() {
//
//			@Override
//			public int compare(PHIAnnotation phi1, PHIAnnotation phi2) {
//				if (phi1.priority != phi2.priority) {
//					return phi1.priority - phi2.priority;
//				}
//
//				return phi2.getSize() - phi1.getSize();
//			}
//
//		});
//		if(DEBUG_COMBINE){
//			Debug.error("PRE-COMBINE:");
//
//			for(PHIAnnotation phi:allPhis){
//				Debug.error(phi);
//			}
//		}
//
//		IntervalTree<PHIAnnotation> it = new IntervalTree<PHIAnnotation>();
//
//		List<PHIAnnotation> selectedPhis = new ArrayList<>();
//
//		for (PHIAnnotation phi : allPhis) {
//			List<PHIAnnotation> searchPhis = it.get(phi.start, phi.end);
//			if (searchPhis.isEmpty()) {
//				it.addInterval(phi.start, phi.end, phi);
//				selectedPhis.add(phi);
//			}
//
//		}
//		Collections.sort(selectedPhis, new Comparator<PHIAnnotation>() {
//
//			@Override
//			public int compare(PHIAnnotation phi1, PHIAnnotation phi2) {
//
//				return phi1.start - phi2.start;
//			}
//
//		});
//
//		int id = 0;
//		for (PHIAnnotation phi : selectedPhis) {
//			phi.id = "P" + (id++);
//		}
//
//		return selectedPhis;
//	}
//
//}
