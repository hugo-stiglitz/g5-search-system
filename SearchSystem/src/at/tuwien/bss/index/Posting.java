package at.tuwien.bss.index;

public class Posting {
	
	private int documentId;
	private int termFrequency;
	
	private Posting nextPosting;

	/**
	 * creates a new posting and sets the document-id
	 * @param documentId
	 * 			document-id
	 */
	public Posting(int documentId) {
		setDocumentId(documentId);
		termFrequency = 1;
	}

	private void setDocumentId(int documentId) { this.documentId = documentId; }
	public int getDocumentId() { return documentId; }
	
	public void incrementTermFrequency() { termFrequency++; }
	public int getTermCount() { return termFrequency; }
	
	public void setNextPosting(Posting nextPosting) { this.nextPosting = nextPosting; }
	private Posting getNextPosting() { return nextPosting; }

	public boolean hasNext() {
		return getNextPosting() != null;
	}

	public Posting next() {
		
		if(getNextPosting() == null) {
			throw new NullPointerException("next posting is null!");
		}
		
		return getNextPosting();
	}
}
