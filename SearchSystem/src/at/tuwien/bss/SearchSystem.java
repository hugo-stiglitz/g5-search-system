package at.tuwien.bss;

import java.io.IOException;
import java.util.ArrayList;

import at.tuwien.bss.documents.DocumentCollection;
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
		
		// test parser by parse document with ID 0
		
		Parser p = new Parser();
		
		ArrayList<String> terms = null;
		try {
			terms = p.parse(documentCollection.getContent(0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// write out terms
		
		log("Terms:");
		for (String t : terms) {
			log(t);
		}
	}
	
	private static void log(String s) {
		System.out.println(s);
	}

	private DocumentCollection documentCollection = new DocumentCollection();
	
}
