package at.tuwien.bss.index;

import java.util.List;

import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBag;

public class IndexerBag extends Indexer {
	
	private Segmenter segmenter = new SegmenterBag();

	@Override
	public List<String> segment(List<String> terms) {
		return segmenter.segment(terms);
	}

}
