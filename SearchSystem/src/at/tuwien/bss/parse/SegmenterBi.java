package at.tuwien.bss.parse;

import java.util.ArrayList;
import java.util.List;

public class SegmenterBi implements Segmenter {

	@Override
	public List<String> segment(List<String> terms) {
		
		List<String> result = new ArrayList<String>();
		
		//first term			TODO macht ma des beim bi-word segmenten o so???
		String firstTerm = "$";
		String secondTerm = terms.get(0);
		result.add(firstTerm + " " + secondTerm);
		
		//2nd to n-1th term 
		for(int i = 0; i < terms.size() - 1; i++) {
			firstTerm = terms.get(i);
			secondTerm = terms.get(i+1);
			result.add(firstTerm + " " + secondTerm);
		}
		
		//last term
		firstTerm = terms.get(terms.size()-1);
		secondTerm = "$";
		result.add(firstTerm + " " + secondTerm);
		
		return result;
	}
}
