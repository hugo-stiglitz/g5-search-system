package at.tuwien.bss.index;

import java.util.List;

import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBag;

public class IndexBag extends Index {
	
	private Segmenter segmenter = new SegmenterBag();

	@Override
	public List<String> segment(String term) {
		return segmenter.segment(term);
	}

}
