package at.tuwien.bss.index;

import java.util.List;

import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBi;

public class IndexerBi extends Indexer {

	private Segmenter segmenter = new SegmenterBi();

	@Override
	protected List<String> segment(List<String> terms) {
		return segmenter.segment(terms);
	}

}
