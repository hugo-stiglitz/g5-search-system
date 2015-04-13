package at.tuwien.bss.index;

import java.io.IOException;
import java.util.List;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.Segmenter;

public class Indexer {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();
	
	private Index index = null;
	private Segmenter segmenter;
	
	public Indexer(Segmenter segmenter) {
		this.segmenter = segmenter;
	}
	
	/**
	 * Index a DocumentCollection.
	 * @param collection
	 * @throws IOException
	 */
	public void index(DocumentCollection collection) throws IOException {
		
		this.index = new Index();
		Parser parser = new Parser();
			
		for (int documentId=0; documentId < collection.getCount(); documentId++) {
			
			// get content, parse and segment
			String document = collection.getContent(documentId);
			List<String> terms = parser.parse(document);
			List<String> segmentedTerms = segmenter.segment(terms);
			
			// insert terms into index
			for(String term : segmentedTerms) {
				index.add(term, documentId);
			}
		}
		
		index.setDocumentCount(collection.getCount());
		
		// calculate the Tf-Idf Weighting
		index.calculateWeighting(new WeightingTfIdf());
		
		LOGGER.logTime("indexed "+ collection.getCount() +" documents");
		
		System.gc();
	}
	
	public Index getIndex() {
		return index;
	}
	
	public void setIndex(Index index) {
		this.index = index;
	}
	
	public Segmenter getSegmenter() {
		return segmenter;
	}
}
