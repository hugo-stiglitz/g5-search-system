package at.tuwien.bss.search;

import java.util.Map.Entry;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Posting;

public class CosineSimilarity {
	
	public CosineSimilarity(Index index) {
		this.index = index;
	}
	
	private Index index;
	
	public float calculate(Query query, int documentId) {
				
		//              V(q) * V(d)                               dotProduct
		//  sim(q,d) = -------------- = ----------------------------------------------------------------
		//              |V(q)|*|V(d)    sqrt(euclidianLengthSumQuery) * sqrt(euclidianLengthSumDocument)
		
		double dotProduct = 0;
		double euclidianLengthSumQuery = 0;
		double euclidianLengthSumDocument = 0;
		
		for (Entry<String, Posting> queryEntry : query) {
			
			String term = queryEntry.getKey();
			Posting queryPosting = queryEntry.getValue();
			
			Posting documentPosting = index.getPosting(term, documentId);
			if (documentPosting == null) {
				// term does not exist in index or document --> weighing is 0 and therefore
				// is not considered in ConsineSimilarity
				
				//XXX bugfix --> TODO calculate euclidianLengthSumQuery only once for a query
				double qw = queryPosting.getWeight();
				euclidianLengthSumQuery += qw * qw;
			}
			else {
				// Term exists in query and in document
				
				double qw = queryPosting.getWeight();
				double dw = documentPosting.getWeight();
				
				dotProduct += qw * dw;
				euclidianLengthSumQuery += qw * qw;
				euclidianLengthSumDocument += dw * dw;
			}
		}
		
		double sim = dotProduct / (Math.sqrt(euclidianLengthSumQuery) * Math.sqrt(euclidianLengthSumDocument));
		
		return (float)sim;
	}
}
