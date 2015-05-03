package at.tuwien.lucene.bm25l;

import java.io.IOException;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

public class BM25LSimilarity extends Similarity {
	
	private float delta;
	
	public BM25LSimilarity() {
		this.delta = 0.5f;
	}
	
	public BM25LSimilarity(float delta) {
		this.delta = delta;
	}

	@Override
	public long computeNorm(FieldInvertState state) {
		return SmallFloat.floatToByte315(state.getBoost() / (float) Math.sqrt(state.getLength() - state.getNumOverlap()));
	}

	@Override
	public SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics... termStats) {
		// TODO
		return new BM25LWeight();
	}

	@Override
	public SimScorer simScorer(SimWeight weight, LeafReaderContext context) throws IOException {
		// TODO
		return new BM25LScorer();
	}
	
	private class BM25LWeight extends SimWeight {
		
		public BM25LWeight() {
			//TODO
		}

		@Override
		public float getValueForNormalization() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void normalize(float queryNorm, float topLevelBoost) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class BM25LScorer extends SimScorer {
		
		public BM25LScorer() {
			//TODO
		}

		@Override
		public float score(int doc, float freq) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float computeSlopFactor(int distance) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
