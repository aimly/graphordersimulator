package ru.isp.svace.graphordersimulator;


public class Utils {
	private Utils() {
	}
	
	public static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}
	
	private static final int MAGIC_NUMBER1 = 101;
	private static final int MAGIC_NUMBER2 = 31;
	private static final int MAGIC_NUMBER3 = 31;
	
	public static int hash(int val1, int val2) {
		int res = MAGIC_NUMBER1;
		res = MAGIC_NUMBER2 * res + hash(val1);
		res = MAGIC_NUMBER3 * res + hash(val2);
		return res;
	}
	
	public static int hash(Object o, int val) {
		return hash(o.hashCode(), val);
	}
	
	
	public static <T> boolean equalsOrNull(T o1, T o2) {
		if(o1==null || o2==null)
			return o1==o2;
		
		if(o1==o2)
			return true;

		return o1.equals(o2);
	}
}
