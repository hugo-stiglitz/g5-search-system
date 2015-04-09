package at.tuwien.bss.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Posting;
import at.tuwien.bss.index.PostingsList;
import at.tuwien.bss.index.Weighting;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.Segmenter;

public class Query implements Iterable<Entry<String, Posting>> {
	
	public Query(String input, Parser parser, Segmenter segmenter) {
		List<String> terms = parser.parse(input);
		terms = segmenter.segment(terms);
		
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
				// term does not exist in index --> term can be removed
				queryTermPostings.remove(term);
			}
			else {
				// term does exist in query and in index

				// weight the query term with the given weighting function

				// the temporary posting from the query is used (it is not added to the index
				// but is used for the weight calculation)
				
				if (queryPosting.getWeight() == Float.NaN) { // check if weight already has been calculated
					queryPosting.setWeight(weightingMethod.calculate(index, term, postingsList, queryPosting));
				}
			}
		}
	}

	private HashMap<String, Posting> queryTermPostings;

	@Override
	public Iterator<Entry<String, Posting>> iterator() {
		return queryTermPostings.entrySet().iterator();
	}
}
