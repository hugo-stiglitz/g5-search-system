package at.tuwien.bss.search;

import java.util.HashSet;
import java.util.Set;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Posting;
import at.tuwien.bss.index.PostingsList;

public class FilterMinimum implements Filter {

	@Override
	public Set<Integer> filter(Query query, Index index) {

		Set<Integer> result = new HashSet<Integer>();
		
		for (String term : query.terms()) {
			PostingsList postingsList = index.getPostingsList(term);
			if (postingsList != null) {
				for (Posting posting : postingsList) {
					result.add(posting.getDocumentId());
				}
			}
		}
		
		return result;
	}
	
	@Override
	public void setMinResultLength(int resultLength) {
		//do nothing
	}

	@Override
	public void setValue(double value) {
		//do nothing
	}
}
