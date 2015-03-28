package at.tuwien.bss;

import java.io.IOException;
import java.util.ArrayList;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.index.IndexerBag;
import at.tuwien.bss.index.IndexerBi;
import at.tuwien.bss.parse.Parser;

public class SearchSystem {
	
	public static void main(String[] args) {
		
		SearchSystem s = new SearchSystem();
		s.test();
	}
	
	public SearchSystem() {
		documentCollection = new DocumentCollection();
		
		log("Current Working Dir: "+System.getProperty("user.dir"));
		
		// documents are placed in the subset folder
		documentCollection.importFolder(".\\..\\..\\subset");
		
		log("imported "+documentCollection.getCount()+" documents");
	}
	
	private void test() {
		
		// test parser by parse document with ID 0, 1, 2
		
		Parser parser = new Parser();
		Indexer indexerBoW = new IndexerBi();
		
		for(int i = 0; i < 10; i++) {
			ArrayList<String> terms = null;
			try {
				terms = parser.parse(documentCollection.getContent(i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// write out terms

			log("Terms:");
			for (String t : terms) {
				log(t);
			}

			indexerBoW.add(terms, i);
		}

		log("******************************************");
		log(indexerBoW.getIndex().print());
	}
	
	private static void log(String s) {
		System.out.println(s);
	}

	private DocumentCollection documentCollection = new DocumentCollection();
	
}
