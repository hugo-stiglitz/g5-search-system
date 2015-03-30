package at.tuwien.bss.search;

public class DocumentScore implements Comparable<DocumentScore> {

	private int documentId;
	private double score;
	
	public DocumentScore(int documentId, double score) {
		this.documentId = documentId;
		this.score = score;
	}
	
	public int getDocumentId() { return documentId; }
	public double getScore() { return score; }

	@Override
	public int compareTo(DocumentScore other) {
		
		if(this.getScore() > other.getScore())
			return 1;
		return -1;
	}
	
	@Override
	public String toString() {
		return /*"Document "+ getDocumentId() +": "+ */String.format("%.6g%n", getScore());
	}
}
