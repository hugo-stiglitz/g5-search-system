package at.tuwien.bss.search;

import java.util.HashSet;
import java.util.Set;

import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Posting;
import at.tuwien.bss.index.PostingsList;
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
		
		
		// find documents to compare with query
		Set<Integer> documents = new HashSet<Integer>();
		
		for (String term : query.terms()) {
			PostingsList postingsList = index.getPostingsList(term);
			if (postingsList != null) {
				for (Posting posting : postingsList) {
					documents.add(posting.getDocumentId());
				}
			}
		}
		
		LOGGER.logTime(documents.size()+" relevant documents found");
		
		if(documents.size() < 10) {
			//TODO write to console that less than 10 documents contain at least one word of query
		}
		
		// filtering documents
		documents = filter.filter(query, documents, index);
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
