package at.tuwien.bss.index;

public class PostingsList {

	private Posting firstPosting;
	private int documentFrequency;
	
	/**
	 * creates a new postings-list and creates a new posting belonging to the document-id
	 * @param documentId
	 */
	public PostingsList(int documentId) {
		
		firstPosting = new Posting(documentId);
		documentFrequency = 1;
	}
	
	public int getDocumentFrequency() { return documentFrequency; }
	
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



	public String print() {

		StringBuilder sb = new StringBuilder();

		Posting posting = firstPosting;

		sb.append("doc"+ posting.getDocumentId() +" (tf: "+ posting.getTermFrequency() +"), ");

		while(posting.hasNext()) {

			posting = posting.next();
			sb.append("doc"+ posting.getDocumentId() +" (tf: "+ posting.getTermFrequency() +"), ");
		}

		return sb.toString();
	}
}
