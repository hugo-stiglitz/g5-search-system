package at.tuwien.bss.parse;

import java.util.ArrayList;

public class Parser {
	
	private Tokenizer tokenizer = new Tokenizer();
	private Normalizer normalizer = new Normalizer();
	
	public ArrayList<String> parse(String input) {
		
		ArrayList<String> result;
		
		// Tokenize input		
		result = tokenizer.tokenize(input);
		
		// Normalize every token
		for (int i=0; i<result.size(); i++) {
			String token = result.get(i);
			//TODO stoplist
			String term = normalizer.normalize(token);
			result.set(i, term);
		}
		
		// TODO: make sure each token is only present once (or is this the indexer's job?)
		// XXX: indexers job ;) indexer needs term frequency of document...
		
		return result;
	}
	
}
