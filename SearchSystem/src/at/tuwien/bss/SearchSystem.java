package at.tuwien.bss;

import java.io.IOException;
import java.util.ArrayList;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.index.IndexerBag;
import at.tuwien.bss.index.IndexerBi;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;

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
		
		LOGGER.logTime("START");
		
		Parser parser = new Parser();
		Indexer indexerBoW = new IndexerBag();
		
		for(int i = 0; i < documentCollection.getCount(); i++) {
			
			try {
				ArrayList<String> terms = parser.parse(documentCollection.getContent(i));
				LOGGER.logTime("document "+ i +" parsed");
				indexerBoW.add(terms, i);
				LOGGER.logTime("document "+ i +" indexed");
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		LOGGER.logTime("FINISH");

		//LOGGER.log(indexerBoW.getIndex().print());
	}

	private DocumentCollection documentCollection = new DocumentCollection();
	
}
