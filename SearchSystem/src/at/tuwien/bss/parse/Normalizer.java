package at.tuwien.bss.parse;

public class Normalizer {
	
	private PorterStemming porter = new PorterStemming();
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public String normalize(String token) {
		
		token = token.toLowerCase();
		token = porter.stripAffixes(token);
		
		return token;
		
	}
}
