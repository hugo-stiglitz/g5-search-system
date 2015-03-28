package at.tuwien.bss.parse;

import java.util.ArrayList;
import java.util.List;

public class SegmenterBag implements Segmenter {

	@Override
	public List<String> segment(String term) {

		List<String> result = new ArrayList<String>();
		result.add(term);
		return result;
	}
}
