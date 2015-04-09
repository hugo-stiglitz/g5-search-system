package at.tuwien.bss.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Posting;
import at.tuwien.bss.index.PostingsList;
import at.tuwien.bss.index.Weighting;

public class Query implements Iterable<Entry<String, Posting>> {
	
	public Query(List<String> terms) {
		
		// Determine Term Frequency in Query
		queryTermPostings = new HashMap<String, Posting>();
		
		for (String term : terms) {
			Posting p = queryTermPostings.get(term);
			if (p == null) { p = new Posting(-1); }
			
			p.incrementTermFrequency();
			
			queryTermPostings.put(term, p);
		}
		
	}
	
	public void calculateWeighting(Index index, Weighting weightingMethod) {
		for (Entry<String, Posting> queryEntry : this) {
			
			String term = queryEntry.getKey();
			Posting queryPosting = queryEntry.getValue();
			
			PostingsList postingsList = index.getPostingsList(term);
			if (postingsList == null) {
				// term does not exist in index --> weight = 0
				queryPosting.setWeight(0);
				
				// TODO: maybe remove from query queryTermPostings??
			}
			else {
				// term does exist in query and in index

				// weight the query term with the given weighting function

				// the temporary posting from the query is used (it is not added to the index
				// but is used for the weight calculation)
				
				queryPosting.setWeight(weightingMethod.calculate(index, term, postingsList, queryPosting));
			}
		}
	}

	private HashMap<String, Posting> queryTermPostings;

	@Override
	public Iterator<Entry<String, Posting>> iterator() {
		return queryTermPostings.entrySet().iterator();
	}
	
	public Iterable<String> terms() {
		return queryTermPostings.keySet();
	}
}
