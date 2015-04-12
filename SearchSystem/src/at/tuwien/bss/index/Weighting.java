package at.tuwien.bss.index;

public interface Weighting {
	
	public float calculate(Index index, String term, PostingsList postingList, Posting posting);
}
