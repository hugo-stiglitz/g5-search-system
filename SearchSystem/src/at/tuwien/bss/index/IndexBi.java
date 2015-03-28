package at.tuwien.bss.index;

import java.util.List;

import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBi;

public class IndexBi extends Index {

	private Segmenter segmenter = new SegmenterBi();

	@Override
	public List<String> segment(String term) {
		return segmenter.segment(term);
	}

}
