package at.tuwien.bss;

import java.io.File;
import java.io.IOException;
import java.util.List;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.index.WeightingTfIdf;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.Segmenter;
import at.tuwien.bss.parse.SegmenterBag;
import at.tuwien.bss.parse.SegmenterBi;
import at.tuwien.bss.search.DocumentScore;
import at.tuwien.bss.search.Query;
import at.tuwien.bss.search.Searcher;

public class SearchSystem {

	private static final SSLogger LOGGER = SSLogger.getLogger();

	private static final String INDEX_BAG_DAT = "index_bag.dat";
	private static final String INDEX_BI_DAT = "index_bi.dat";
	private static final String INDEX_BAG_CVS = "index_bag.csv";
	private static final String INDEX_BI_CVS = "index_bi.csv";
	
	private DocumentCollection documentCollection = new DocumentCollection();

	private Indexer indexerBoW = new Indexer(new SegmenterBag());
	private Indexer indexerBi = new Indexer(new SegmenterBi());

	public static void main(String[] args) {

		LOGGER.log("Current Working Dir: "+System.getProperty("user.dir"));
		
		SearchSystem s = new SearchSystem();
		
		s.load();
		
		String searchQuery = "astronomy club sci space GPS uucp";		//good result: sci.space/62317
		//String searchQuery = "hello i want to buy a computer";
		
		s.search(searchQuery, s.indexerBoW);
	}

	private void load() {
		
		documentCollection = new DocumentCollection();
		//documents are placed in the subset folder
		documentCollection.importFolder(".\\..\\..\\subset");
		LOGGER.logTime("imported "+documentCollection.getCount()+" documents");

		File indexBagFile = new File(INDEX_BAG_DAT);
		File indexBiFile = new File(INDEX_BI_DAT);

		if(indexBagFile.exists() && indexBiFile.exists()) {
			//load existing index
			try {
				Index indexBoW = new Index();
				indexBoW.load(INDEX_BAG_DAT);
				indexBoW.exportCsv(INDEX_BAG_CVS);

				Index indexBi = new Index();
				indexBi.load(INDEX_BI_DAT);
				indexBi.exportCsv(INDEX_BI_CVS);

				indexerBoW.loadIndex(indexBoW, documentCollection);
				indexerBi.loadIndex(indexBi, documentCollection);
				
				LOGGER.logTime("data loaded from files");

			} catch (Exception e) {
				LOGGER.log("error loading data-files");
				index();
			}
		}
		else {
			index();
		}
	}

	private void index() {

		LOGGER.logTime("Indexing...");
		try {
			indexerBoW.index(documentCollection, documentCollection.getCount());
			indexerBi.index(documentCollection, documentCollection.getCount());
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.logTime("Export...");
		try {
			indexerBoW.getIndex().save(INDEX_BAG_DAT);
			indexerBoW.getIndex().exportCsv(INDEX_BAG_CVS);

			indexerBi.getIndex().save(INDEX_BI_DAT);
			indexerBi.getIndex().exportCsv(INDEX_BI_CVS);
		
		} catch(IOException e) {
			LOGGER.log("error exporting data-files");
		}
	}
	
	private void search(String searchQuery, Indexer indexer) {
		
		LOGGER.logTime("START SEARCH");

		Parser parser = new Parser();
		List<String> queryTerms = parser.parse(searchQuery);
		Query query = new Query(indexer.getSegmenter().segment(queryTerms));

		Searcher searcher = new Searcher(indexer.getIndex());
		DocumentScore[] scoreArray = searcher.searchCosineSimilarity(query);

		LOGGER.logTime("SEARCH FINISHED");

		for(int j = 0; j < 10; j++) {
			LOGGER.log(scoreArray[j] +" : "+ documentCollection.getName(scoreArray[j].getDocumentId()));
		}
	}
}
