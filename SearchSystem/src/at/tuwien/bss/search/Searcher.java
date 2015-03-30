package at.tuwien.bss.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javafx.util.Pair;
import at.tuwien.bss.index.IdfTf;
import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.PostingsList;
import at.tuwien.bss.logging.SSLogger;

public class Searcher {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();

	public int[] search(List<String> queryTerms, Index index) {
		
		LOGGER.logTime("START SEARCH");
		
		TreeSet<PostingsList> postingsListSet = new TreeSet<PostingsList>();
		
		for(String term : queryTerms) {
			postingsListSet.add(index.getPostingsList(term));
		}
		
		/*
		// LOW LEVEL LOGGING
		for(PostingsList pl : postingsListSet) {
			LOGGER.log(pl.getTerm() +" ("+ pl.getDocumentFrequency() +"x): "+ pl.print());
		}
		*/
		
		//TODO remove all terms with document frequency 0
		
		//calculate the sum of idf-tf for each document (over all terms in query)
		LOGGER.logTime("START CALCULATING DOCUMENT VALUES");
		
		Map<Pair<Integer,String>, IdfTf> idfTfMap = index.getIdfTfMap();
		Map<Integer, IdfTf> documentIdfTfMap = new HashMap<Integer, IdfTf>();
		
		Set<Pair<Integer,String>> keySet = idfTfMap.keySet();
		Iterator<Pair<Integer,String>> it = keySet.iterator();
		while(it.hasNext()) {

			Pair<Integer, String> key = it.next();
			IdfTf idfTf = idfTfMap.get(key);

			/*
			// LOW LEVEL LOGGING
			LOGGER.log("Term: "+ key.getValue() +" / DocID: "+ key.getKey() +"\t -- value: "+ value);
			*/

			Integer term = key.getKey();
			if(documentIdfTfMap.containsKey(term)) {
				documentIdfTfMap.put(term, IdfTf.sum(documentIdfTfMap.get(term), idfTf));
			}
			else {
				documentIdfTfMap.put(term, idfTf);
			}
		}

		LOGGER.logTime("DOCUMENT VALUE CALCULATED");
		
		//TODO define threshold and filter documents (or terms?)

		
		// LOW LEVEL LOGGING
		Set<Integer> keySet2 = documentIdfTfMap.keySet();
		Iterator<Integer> it2 = keySet2.iterator();
		while(it2.hasNext()) {
			Integer key = it2.next();
			IdfTf value = documentIdfTfMap.get(key);
			
			LOGGER.log("Document: "+ key +"\t -- value: "+ value);
		}
		
		
		//TODO score documents
		
		//TODO rank first 10 documents with heap-sort
		
		return new int[10];
	}
}
