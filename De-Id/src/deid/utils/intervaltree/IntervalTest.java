package deid.utils.intervaltree;

import java.util.Arrays;
import java.util.List;

public class IntervalTest {

	public static void main(String[] args) {
		IntervalTree<Integer> it = new IntervalTree<Integer>();

		it.addInterval(1,5,1);
		it.addInterval(6, 10, 2);
		it.addInterval(4, 10, 3);
		it.addInterval(4, 10, 4);


		it.remove(2);
		it.removeAll(Arrays.asList(new Integer[]{1,3}));


		List<Integer> result1 = it.get(0,11);


		System.out.println("Intervals");
		for(int r : result1)
		    System.out.println(r);

		
	}

}
