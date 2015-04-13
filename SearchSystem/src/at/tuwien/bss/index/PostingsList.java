package at.tuwien.bss.index;

import java.util.Iterator;

public class PostingsList implements Comparable<PostingsList>, Iterable<Posting> {

	private Posting firstPosting;
	private int documentFrequency;

	public PostingsList() {
		
		firstPosting = null;
		documentFrequency = 0;
	}
	
	public PostingsList(Posting firstPosting, int documentFrequency) {
		
		this.firstPosting = firstPosting;
		this.documentFrequency = documentFrequency;
	}
	
	public int getDocumentFrequency() { return documentFrequency; }
	private void incrementDocumentFrequency() { documentFrequency++; }

	private Posting createNewPosting(int documentId) {
		Posting posting = new Posting(documentId);
		incrementDocumentFrequency();
		
		return posting;
	}
	
	/**
	 * add a posting to the list or just increment the term frequency (if document already in list)
	 * @param documentId
	 */
	public void add(int documentId) {
		
		//check if document already in list
		Posting posting = firstPosting;
		
		if (posting == null) {
			// no postings yet
			posting = createNewPosting(documentId);
			firstPosting = posting;
		}
		else {
			
			Posting lastPosting = null;
			while(posting != null) {
				
				if(posting.getDocumentId() == documentId) {
					// posting found --> break
					break;
				}
				
				if (posting.getDocumentId() < documentId) {
					// the docId is smaller --> insert new Posting here
					
					// ATTENTION: when chaning the Ordering update getPosting(...) aswell as it relies on this ordering
					
					Posting newPosting = createNewPosting(documentId);

					// connect to last posting
					if (lastPosting == null) {
						// insert as first item in the list
						firstPosting = newPosting;
					}
					else {
						lastPosting.setNextPosting(newPosting);
					}
					
					// connect next posting
					newPosting.setNextPosting(posting);
					
					posting = newPosting;
					break;
				}
				
				lastPosting = posting;
				posting = posting.getNextPosting();
			}
			
			if (posting == null) {
				// all postings in the list had a greater docId --> insert new Posting at the end
				posting = createNewPosting(documentId);
				lastPosting.setNextPosting(posting);
			}
		}
		
		// do increment stuff
		posting.incrementTermFrequency();
	}
	
	public Posting getPosting(int documentId) {
		for (Posting posting : this) {
			if (posting.getDocumentId() == documentId) {
				return posting;
			}
			else if (posting.getDocumentId() < documentId) {
				// not in list
				break;
			}
		}
		
		return null;
	}

	public String print() {

		StringBuilder sb = new StringBuilder();

		for (Posting posting : this) {
			sb.append("doc"+ posting.getDocumentId() +" (tf: "+ posting.getTermFrequency() +" ; w: "+posting.getWeight()+"), ");
		}
		
		if (documentFrequency == 0) {
			sb.append("empty");
		}

		return sb.toString();
	}

	@Override
	public int compareTo(PostingsList other) {
		
		int value = this.getDocumentFrequency() - other.getDocumentFrequency();
		return value == 0 ? -1 : value;
	}

	@Override
	public Iterator<Posting> iterator() {
		return new PostingIterator();
	}
	
	private class PostingIterator implements Iterator<Posting> {

		private Posting posting = firstPosting;
		
		@Override
		public boolean hasNext() {
			return posting != null;
		}

		@Override
		public Posting next() {
			Posting p = posting;
			posting = posting.getNextPosting();
			
			return p;
		}
		
	}
}
