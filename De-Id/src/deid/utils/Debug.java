package deid.utils;

import java.io.File;

public class Debug {

	public static boolean GLOBAL_DEBUG = true;

	public static final void print(Object str) {
		if (GLOBAL_DEBUG)
			System.out.println(str);
	}

	public static final void printFileAbs(String path) {
		File f = new File(path);
		System.out.println(f.getAbsolutePath());
	}

	public static final void error(String str) {
		System.err.println(str);
	}

	public static final void error(Object str) {
		System.err.println(str);
	}

}
