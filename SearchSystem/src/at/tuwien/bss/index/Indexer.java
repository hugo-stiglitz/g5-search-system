package at.tuwien.bss.index;

import java.util.List;

public abstract class Indexer {
	
	private Index index = new Index();
	
	public abstract List<String> segment(List<String> terms);
	
	
	public void add(List<String> terms, int documentId) {
		
		List<String> segmentedTerms = segment(terms);
		for(String term : segmentedTerms) {
			index.add(term, documentId);
		}
	}
	
	public Index getIndex() {
		return index;
	}
}
