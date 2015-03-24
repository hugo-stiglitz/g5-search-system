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
		
		log(System.getProperty("user.dir"));
		
		documentCollection.importFolder(".\\..\\..\\subset");
		
		log("imported "+documentCollection.getCount()+" documents");
	}
	
	private void test() {
		Parser p = new Parser();
		
		ArrayList<String> terms = null;
		try {
			terms = p.parse(documentCollection.getContent(0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
