package at.tuwien.bss.parse;

import java.util.ArrayList;

public class Tokenizer {
	// Delimiters which end a token
	static final char[] delimiters = new char[] { ' ', ',', '.', '?', '!', ':', ';', '/', '\\', '|', '-', '(', ')', '[', ']', '{', '}', '<', '>', '\t', '\n', '\r', '"', '#', '*' };
	
	/**
	 * Generates a List of Tokens from the input String
	 * @param input
	 * @return
	 */
	public ArrayList<String> tokenize(String input) {
		ArrayList<String> result = new ArrayList<String>();
		
		// contains the current token
		StringBuilder token = new StringBuilder();
		
		// Parse every char of the input
		for (int i=0; i<input.length(); i++) {
			char c = input.charAt(i);
			
			if (isDelimiter(c)) {
				// when delimiter, add to result if it isn't empty and reset current token
				if (token.length() > 0) {
					token = addToken(result, token);
				}
			}
			else {
				// add char to current token
				token.append(c);
			}
		}
		
		// don't forget last token
		token = addToken(result, token);
		
		return result;
	}
	
	private StringBuilder addToken(ArrayList<String> result, StringBuilder token) {
		
		if (token.length() == 0) {
			// do not add empty token			
			return token;
		}
		else {
			result.add(token.toString());
			return new StringBuilder();
		}
	}
	
	private static boolean isDelimiter(char c) {
		for (int i=0; i<delimiters.length; i++) {
			if (delimiters[i] == c) { return true; }
		}
		return false;
	}
}
