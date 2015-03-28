package at.tuwien.bss.index;

import java.util.HashMap;
import java.util.Map;

public class Index {
	
	private Map<String,PostingsList> indexMap = new HashMap<String,PostingsList>();

	/**
	 * add a term to the postings list
	 * @param term
	 */
	public final void add(String term, int documentId) {

		//term already exists in list
		if(indexMap.containsKey(term)) {
			indexMap.get(term).add(documentId);
		}
		//term not in list yet
		else {

			PostingsList postingsList = new PostingsList(documentId);
			indexMap.put(term, postingsList);
		}
	}

	public final String print() {
		
		StringBuilder sb = new StringBuilder();
		
		for(String term : indexMap.keySet()) {
			
			PostingsList pl = indexMap.get(term);
			sb.append("* "+ term +":\t(document-frequency: "+ pl.getDocumentFrequency() +")\n");
			sb.append("\tdocuments: "+ pl.print());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
