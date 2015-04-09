package at.tuwien.bss.index;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Index {
	
	private Map<String,PostingsList> indexMap = new HashMap<String,PostingsList>();

	private int documentCount = 0;
	public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
	public int getDocumentCount() { return documentCount; }
	
	private Weighting weightingMethod = null;
	
	/**
	 * add a term to the postings list
	 * @param term
	 */
	public void add(String term, int documentId) {

		PostingsList postingsList = indexMap.get(term);
		
		if (postingsList == null) {
			// term not in list yet
			postingsList = new PostingsList();
			indexMap.put(term, postingsList);
		}
		
		postingsList.add(documentId);
	}
	
	public PostingsList getPostingsList(String term) {
		
		PostingsList postingsList = indexMap.get(term);
		return postingsList;
	}
	
	public Posting getPosting(String term, int documentId) {
		PostingsList postingsList = getPostingsList(term);
		if (postingsList == null) {
			// term does not exist in index
			return null;
		}
		else {
			return postingsList.getPosting(documentId);
		}
	}
	
	public void calculateWeighting(Weighting weightingMethod) {
		
		for (Entry<String, PostingsList> entry : indexMap.entrySet()) {
			PostingsList postingsList = entry.getValue();
			String term = entry.getKey();
			
			for (Posting posting : postingsList) {
				posting.setWeight(weightingMethod.calculate(this, term, postingsList, posting));
			}
		}
		
		this.weightingMethod = weightingMethod;
	}
	
	public Weighting getWeightingMethod() {
		return this.weightingMethod;
	}
	
	public Map<Integer, Float> getTermWeighting(String term) {
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		
		PostingsList postingsList = indexMap.get(term);
		if (postingsList != null) {
			for (Posting posting : postingsList) {
				map.put(posting.getDocumentId(), posting.getWeight());
			}
		}
		
		return map;
	}

	public String print() {
		
		StringBuilder sb = new StringBuilder();
		
		for(String term : indexMap.keySet()) {
			
			PostingsList pl = indexMap.get(term);
			sb.append("* "+ term +":\t(document-frequency: "+ pl.getDocumentFrequency() +")\n");
			sb.append("\tdocuments: "+ pl.print());
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public void exportCsv() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("index.csv", "UTF-8");
			writer.println("Term;DocumentFrequency;docId;termFrequency;weight");
			
			for(String term : indexMap.keySet()) {
				
				PostingsList pl = indexMap.get(term);
				
				ArrayList<Posting> postings = new ArrayList<Posting>(pl.getDocumentFrequency());
				for (Posting posting : pl) {
					postings.add(posting);
				}
				postings.sort(new Comparator<Posting>() {

					@Override
					public int compare(Posting o1, Posting o2) {
						return Float.compare(o1.getWeight(), o2.getWeight());
					}
				});
				
				for (Posting posting : postings) {
					
					writer.println(term +";"+ pl.getDocumentFrequency()+";doc"+posting.getDocumentId()+";"+posting.getTermFrequency()+";"+posting.getWeight());
				}
			
				
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
