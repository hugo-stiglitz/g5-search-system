package at.tuwien.bss.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;

public class Index {
	
	private Map<String,PostingsList> indexMap = new HashMap<String,PostingsList>();
	private Map<Pair<Integer,String>, IdfTf> idfTfMap;

	/**
	 * add a term to the postings list
	 * @param term
	 */
	public void add(String term, int documentId) {

		//term already exists in list
		if(indexMap.containsKey(term)) {
			indexMap.get(term).add(documentId);
		}
		//term not in list yet
		else {

			PostingsList postingsList = new PostingsList(term, documentId);
			indexMap.put(term, postingsList);
		}
	}
	
	public Set<PostingsList> getAllPostingsLists() {
		
		Set<PostingsList> result = new HashSet<PostingsList>();
		
		Set<String> keySet = indexMap.keySet();
		for(String term : keySet) {
			result.add(indexMap.get(term));
		}
		
		return result;
	}
	
	public PostingsList getPostingsList(String term) {
		
		if(indexMap.containsKey(term)) {
			return indexMap.get(term);
		}
		else {
			return new PostingsList(term);
		}
	}
	
	public void setIdfTfMap(Map<Pair<Integer,String>, IdfTf> idfTfMap) { this.idfTfMap = idfTfMap; }
	public Map<Pair<Integer,String>, IdfTf> getIdfTfMap() { return idfTfMap; }

	public String print() {
		
		StringBuilder sb = new StringBuilder();
		
		for(String term : indexMap.keySet()) {
			
			PostingsList pl = indexMap.get(term);
			sb.append("* "+ term +":\t(document-frequency: "+ pl.getDocumentCount() +")\n");
			sb.append("\tdocuments: "+ pl.print());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
