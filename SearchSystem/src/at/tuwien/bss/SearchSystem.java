package at.tuwien.bss;

import java.io.IOException;
import java.util.List;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.index.IndexerBag;
import at.tuwien.bss.index.IndexerBi;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBag;
import at.tuwien.bss.search.Searcher;

public class SearchSystem {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();
	
	public static void main(String[] args) {
		
		SearchSystem s = new SearchSystem();
		s.test();
	}
	
	public SearchSystem() {
		
		documentCollection = new DocumentCollection();
		
		LOGGER.log("Current Working Dir: "+System.getProperty("user.dir"));
		
		// documents are placed in the subset folder
		documentCollection.importFolder(".\\..\\..\\subset");
		
		LOGGER.log("imported "+documentCollection.getCount()+" documents");
	}
	
	private void test() {
		
		// test parser by parse document with ID 0 - 10
		
		LOGGER.logTime("START INDEXING");
		
		Parser parser = new Parser();
		Indexer indexerBoW = new IndexerBag();
		
		for(int i = 0; i < documentCollection.getCount(); i++) {
		//for(int i = 0; i < 10; i++) {

			try {
				List<String> terms = parser.parse(documentCollection.getContent(i));
				indexerBoW.add(terms, i);
				
				/*
				// LOW LEVEL LOGGING
				LOGGER.logTime("document "+ i +" indexed");
				*/
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		LOGGER.logTime("INDEXING FINISHED");
		
		indexerBoW.calculateIdfTf();

		//LOGGER.log(indexerBoW.getIndex().print());
		
		String searchQuery = "i want to, buy a-new computer!";
		
		List<String> queryTerms = parser.parse(searchQuery);
		Segmenter segmenter = new SegmenterBag();
		queryTerms = segmenter.segment(queryTerms);
		
		Searcher searcher = new Searcher();
		int[] topDocumentIds = searcher.search(queryTerms, indexerBoW.getIndex());
	}

	private DocumentCollection documentCollection = new DocumentCollection();
	
}
