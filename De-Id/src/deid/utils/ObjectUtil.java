package deid.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil{
	public static void save(String filePath,Object obj){
		ObjectOutputStream out=null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
			
			out.writeObject(obj);

//			Debug.print("Save: "+filePath);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(out!=null){
				try {
					out.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
        
	}
	public static Object load(String filePath){
		
		
		
		ObjectInputStream in=null;
		Object obj=null;
		
		try {
			
//			Debug.print("Load: "+filePath);
			in= new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			obj=in.readObject();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return obj;
	}
}
