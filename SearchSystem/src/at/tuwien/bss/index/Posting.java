package at.tuwien.bss.index;

public class Posting {
	
	private int documentId;
	private int termFrequency;
	private float weight = 0;
	
	private Posting nextPosting;

	/**
	 * creates a new posting and sets the document-id
	 * @param documentId
	 * 			document-id
	 */
	public Posting(int documentId) {
		this.documentId = documentId;
		termFrequency = 0;
	}
	
	public Posting(int documentId, int termFrequency, float weight) {
		this.documentId = documentId;
		this.termFrequency = termFrequency;
		this.weight = weight;
	}

	public int getDocumentId() { return documentId; }
	
	public void incrementTermFrequency() { termFrequency++; }
	public int getTermFrequency() { return termFrequency; }
	
	public void setWeight(float weight) { this.weight = weight; }
	public float getWeight() { return this.weight; }
	
	public void setNextPosting(Posting nextPosting) { this.nextPosting = nextPosting; }
	public Posting getNextPosting() { return nextPosting; }
}
