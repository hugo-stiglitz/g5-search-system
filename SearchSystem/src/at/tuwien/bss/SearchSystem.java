package at.tuwien.bss;

import java.io.IOException;
import java.util.List;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.index.IndexerBag;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBag;
import at.tuwien.bss.search.DocumentScore;
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
		
		LOGGER.logTime("START INDEXING");
		
		Indexer indexerBoW = new IndexerBag();
		
		try {
			indexerBoW.index(documentCollection, 100);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LOGGER.logTime("INDEXING FINISHED");
		
		//LOGGER.log(indexerBoW.getIndex().print());
		
		//LOGGER.logTime("Export...");
		//indexerBoW.getIndex().exportCsv();
		
		//String searchQuery = "astronomy club sci space GPS uucp";		//good result: sci.space/62317
		String searchQuery = "hello i want to buy a computer";
		
		LOGGER.logTime("START SEARCH");
		
		Parser parser = new Parser();
		List<String> queryTerms = parser.parse(searchQuery);
		Segmenter segmenter = new SegmenterBag();
		queryTerms = segmenter.segment(queryTerms);
		
		Searcher searcher = new Searcher(indexerBoW.getIndex(), queryTerms);
		
		//DocumentScore[] scoreArray = searcher.searchIdfTf();
		DocumentScore[] scoreArray = searcher.searchCosineSimilarity();
		
		LOGGER.logTime("SEARCH FINISHED");
		
		for(int j = 0; j < 10; j++) {
			LOGGER.log(documentCollection.getName(scoreArray[j].getDocumentId()) +" --- "+ scoreArray[j]);
		}
	}

	private DocumentCollection documentCollection = new DocumentCollection();
	
}
