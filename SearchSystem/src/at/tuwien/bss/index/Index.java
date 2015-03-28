package at.tuwien.bss.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Index {

	public abstract List<String> segment(String term);
	
	
	private Map<String,PostingsList> indexMap = new HashMap<String,PostingsList>();

	/**
	 * add a term to the postings list
	 * @param term
	 */
	public final void add(String fullTerm, int documentId) {
		
		List<String> segmentedTerm = this.segment(fullTerm);

		for(String term : segmentedTerm) {
			
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
	}
	
	public final String print() {
		
		StringBuilder sb = new StringBuilder();
		
		for(String term : indexMap.keySet()) {
			
			PostingsList pl = indexMap.get(term);
			sb.append("* "+ term +":\t\t"+ pl.getDocumentFrequency() +"\n");
			sb.append("\tdocuments: "+ pl.print());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
