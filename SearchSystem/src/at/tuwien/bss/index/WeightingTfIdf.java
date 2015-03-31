package at.tuwien.bss.index;

public class WeightingTfIdf implements Weighting {

	@Override
	public float calculate(Index index, String term, PostingsList postingList, Posting posting) {
		
		float idf = (float)Math.log10(index.getDocumentCount() / (float)postingList.getDocumentFrequency());
		float tf = posting.getTermFrequency();
		
		return tf * idf;
	}
	
}
