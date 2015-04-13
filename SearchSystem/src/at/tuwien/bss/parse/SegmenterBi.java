package at.tuwien.bss.parse;

import java.util.ArrayList;
import java.util.List;

public class SegmenterBi implements Segmenter {
	
	private static final char DIVIDER = '$';

	@Override
	public List<String> segment(List<String> terms) {
		
		List<String> result = new ArrayList<String>();
		
		//first term
		result.add(DIVIDER + terms.get(0));
		
		//2nd to n-1th term
		for(int i = 0; i < terms.size() - 1; i++) {
			result.add(terms.get(i) + DIVIDER + terms.get(i+1));
		}
		
		//last term
		result.add(terms.get(terms.size()-1) + DIVIDER);
		
		return result;
	}
}
