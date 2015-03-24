package at.tuwien.bss.parse;

import java.util.ArrayList;

public class Tokenizer {
	static final char[] delimiters = new char[] { ' ', '.', '?', '!', ':', ';', '/', '-', '(', ')', '[', ']', '{', '}', '<', '>', '\t', '\n', '\r' };
	
	public ArrayList<String> tokenize(String input) {
		ArrayList<String> result = new ArrayList<String>();
		
		StringBuilder token = new StringBuilder();
		
		for (int i=0; i<input.length(); i++) {
			char c = input.charAt(i);
			
			if (isDelimiter(c)) {
				if (token.length() > 0) {
					result.add(token.toString());
					
					token = new StringBuilder();
				}
			}
			else {
				token.append(c);
			}
		}
		
		return result;
	}
	
	private static boolean isDelimiter(char c) {
		for (int i=0; i<delimiters.length; i++) {
			if (delimiters[i] == c) { return true; }
		}
		return false;
	}
}
