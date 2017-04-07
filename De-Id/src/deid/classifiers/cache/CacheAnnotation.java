package deid.classifiers.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deid.Constants;
import deid.anns.IAnnotation;
import deid.utils.Debug;
import deid.utils.FileUtil;
import deid.utils.ObjectUtil;

public class CacheAnnotation implements Serializable{
	
	
	public Map<String,List<IAnnotation>> hmDoc2Annotations=new HashMap<String, List<IAnnotation>>();
	public String name;
	public String filePath;
	public CacheAnnotation(String name) {
		super();
		this.name = name;
		this.filePath=Constants.CACHE+"/"+name;
	}
	
	public void populate(String docId, List<IAnnotation> anns){
		hmDoc2Annotations.put(docId, anns);
	}
	
	public CacheAnnotation load(){
		Debug.print("Load:"+filePath);
		return (CacheAnnotation) ObjectUtil.load(filePath);
	}
	public void save(){
		Debug.print("Save:"+filePath);
		ObjectUtil.save(filePath, this);
	}
	
//	public static void save(CacheAnnotation cache){
//		ObjectUtil.save(cache.filePath, cache);
//	}
//	public static CacheAnnotation load(CacheAnnotation cache){
//		return (CacheAnnotation) ObjectUtil.load(cache.filePath);
//	}
	
}
