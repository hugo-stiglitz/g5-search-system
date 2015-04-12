package at.tuwien.bss.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Posting;
import at.tuwien.bss.index.PostingsList;

public class FilterContent implements Filter {

	private double threshold;

	public FilterContent(double threshold) {

		this.threshold = threshold;
	}

	public Set<Integer> filter(Query query, Index index) {

		double threshold = this.threshold;

		Map<Integer,Integer> documentQueryCount = new HashMap<Integer,Integer>();

		for (String term : query.terms()) {
			PostingsList postingsList = index.getPostingsList(term);
			if (postingsList != null) {
				for (Posting posting : postingsList) {
					int documentId = posting.getDocumentId();
					if(documentQueryCount.containsKey(documentId)) {
						documentQueryCount.put(documentId, documentQueryCount.get(documentId) + 1);
					}
					else {
						documentQueryCount.put(documentId, 1);
					}
				}
			}
		}

		Set<Integer> result = new HashSet<Integer>();

		while(result.size() < 10 && documentQueryCount.size() >= 10) {
			for (Integer documentId : documentQueryCount.keySet()) {
				if(documentQueryCount.get(documentId) >= query.size() * threshold) {
					result.add(documentId);
				}
			}
			threshold = threshold * 0.95;
		}

		return result;
	}
	
	@Override
	public void setValue(double threshold) {
		this.threshold = threshold;
		
	}
}
