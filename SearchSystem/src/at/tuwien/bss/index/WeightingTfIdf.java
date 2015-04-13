package at.tuwien.bss.index;

public class WeightingTfIdf implements Weighting {

	private static final long serialVersionUID = 1L;

	@Override
	public float calculate(Index index, String term, PostingsList postingList, Posting posting) {
		
		double idf = Math.log10(index.getDocumentCount() / (double)postingList.getDocumentFrequency());
		double tf = posting.getTermFrequency();
		
		return (float)(tf * idf);
	}
	
}
