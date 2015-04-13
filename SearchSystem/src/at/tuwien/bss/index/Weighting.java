package at.tuwien.bss.index;

import java.io.Serializable;

public interface Weighting extends Serializable {
	
	public float calculate(Index index, String term, PostingsList postingList, Posting posting);
}
