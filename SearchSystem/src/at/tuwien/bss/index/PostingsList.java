package at.tuwien.bss.index;

import java.util.ArrayList;
import java.util.List;

public class PostingsList implements Comparable<PostingsList> {

	private String term;
	private Posting firstPosting;
	private int documentFrequency;
	
	public PostingsList(String term) {
		
		this.term = term;
		firstPosting = null;
		documentFrequency = 0;
	}
	
	/**
	 * creates a new postings-list and creates a new posting belonging to the document-id
	 * @param documentId
	 */
	public PostingsList(String term, int documentId) {
		
		this.term = term;
		firstPosting = new Posting(documentId);
		documentFrequency = 1;
	}
	
	public int getDocumentCount() { return documentFrequency; }
	public String getTerm() { return term; }
	
	/**
	 * add a posting to the list or just increment the term frequency (if document already in list)
	 * @param documentId
	 */
	public void add(int documentId) {
		
		//check if document already in list
		Posting posting = firstPosting;
		
		if(posting.getDocumentId() == documentId) {
			posting.incrementTermFrequency();
			return;
		}
		
		while(posting.hasNext()) {
			posting = posting.next();
			
			if(posting.getDocumentId() == documentId) {
				posting.incrementTermFrequency();
				return;
			}
		}
		
		//document not in list yet
		Posting newPosting = new Posting(documentId);
		posting.setNextPosting(newPosting);
		documentFrequency++;

	}

	public void createSkipList() {
		//TODO create skip list after list is completed
	}

	public List<Posting> getPostings() {

		List<Posting> result = new ArrayList<Posting>();

		Posting posting = firstPosting;

		if(posting != null) {

			result.add(posting);
			while(posting.hasNext()) {
				posting = posting.next();
				result.add(posting);
			}
		}
		
		return result;
	}

	public String print() {

		StringBuilder sb = new StringBuilder();

		Posting posting = firstPosting;

		if(posting != null) {
			sb.append("doc"+ posting.getDocumentId() +" (tf: "+ posting.getTermCount() +"), ");

			while(posting.hasNext()) {
				
				posting = posting.next();
				sb.append("doc"+ posting.getDocumentId() +" (tf: "+ posting.getTermCount() +"), ");
			}
		}
		else {
			sb.append("empty");
		}

		return sb.toString();
	}

	@Override
	public int compareTo(PostingsList other) {
		
		int value = this.getDocumentCount() - other.getDocumentCount();
		return value == 0 ? -1 : value;
	}
}
