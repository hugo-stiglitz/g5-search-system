package at.tuwien.bss.search;

import java.util.Set;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.search.CosineSimilarity;
import at.tuwien.bss.logging.SSLogger;

public class Searcher {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();

	private Index index;
	
	public Searcher(Index index) {
		this.index = index;
	}

	public DocumentScore[] searchCosineSimilarity(Query query, Filter filter) {
		
		// weight the query terms with the index's weighting method
		query.calculateWeighting(index, index.getWeightingMethod());
		
		CosineSimilarity cosineSimilarity = new CosineSimilarity(index);
		
		// filtering documents
		Set<Integer> documents = filter.filter(query, index);
		LOGGER.logTime(documents.size()+" most relevant documents filtered");
		
		// perform cosine similarity for these documents
		DocumentScore[] scoreArray = new DocumentScore[documents.size()];
		int i = 0;
		for(Integer documentId : documents) {
			scoreArray[i++] = new DocumentScore(documentId, cosineSimilarity.calculate(query, documentId));
		}
		
		LOGGER.logTime("Sorting...");
		HeapSort.heapSort(scoreArray, Math.min(scoreArray.length-1, 10));
		
		return scoreArray;
	}
}
