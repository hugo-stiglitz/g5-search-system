package at.tuwien.bss.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import at.tuwien.bss.index.Index;
import at.tuwien.bss.logging.SSLogger;

public class Searcher {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();

	public DocumentScore[] search(List<String> queryTerms, Index index) {
		
		//TODO remove all terms with document frequency 0
		
		//calculate the sum of idf-tf for each document (over all terms in query)
		LOGGER.logTime("START CALCULATING DOCUMENT VALUES");
		
		Map<Pair<Integer,String>, Float> idfTfMap = new HashMap<Pair<Integer,String>, Float>();
		Map<Integer, Float> documentIdfTfMap = new HashMap<Integer, Float>();
		
		//get idf-tf's of terms in search query
		for(String term : queryTerms) {
			Map<Integer,Float> tmpMap = index.getTermWeighting(term);
			for(Integer documentId : tmpMap.keySet()) {
				idfTfMap.put(new Pair<Integer,String>(documentId, term), tmpMap.get(documentId));
			}
		}
		
		//sum up all (the queries terms) idf-tf's of a document
		for(Pair<Integer,String> key : idfTfMap.keySet()) {
			
			Integer documentId = key.getKey();
			float idfTf = idfTfMap.get(key);
			
			/*
			// LOW LEVEL LOGGING
			LOGGER.log("Term: "+ key.getValue() +" / DocID: "+ documentId +"\t -- value: "+ idfTf);
			*/

			if(documentIdfTfMap.containsKey(documentId)) {
				documentIdfTfMap.put(documentId, documentIdfTfMap.get(documentId) + idfTf);
			}
			else {
				documentIdfTfMap.put(documentId, idfTf);
			}
		}

		LOGGER.logTime("DOCUMENT VALUE CALCULATED");
		
		//TODO define threshold and filter documents (or terms?)

		/*
		// LOW LEVEL LOGGING
		for(Integer key : documentIdfTfMap.keySet()) {
			float value = documentIdfTfMap.get(key);
			
			LOGGER.log("Document: "+ key +"\t -- value: "+ value);
		}
		*/
		
		
		
		//TODO score documents
		
		//TODO rank first 10 documents with heap-sort
		//XXX test ranking with idf-tf
		DocumentScore[] scoreArray = new DocumentScore[documentIdfTfMap.size()];
		int i = 0;
		for(Integer key : documentIdfTfMap.keySet()) {
			scoreArray[i] = new DocumentScore(key, documentIdfTfMap.get(key));
			i++;
		}
		
		LOGGER.logTime("START SORTING");
		HeapSort.heapSort(scoreArray, 10);
		LOGGER.logTime("SORTED");
		
		return scoreArray;
	}
}
