package at.tuwien.bss.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tuwien.bss.logging.SSLogger;
import javafx.util.Pair;

public abstract class Indexer {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();
	
	private Index index = new Index();
	
	public abstract List<String> segment(List<String> terms);
	
	
	public void add(List<String> terms, int documentId) {
		
		List<String> segmentedTerms = segment(terms);
		for(String term : segmentedTerms) {
			index.add(term, documentId);
		}
	}
	
	public void calculateIdfTf() {
		
		LOGGER.logTime("START CALCULATE IDF-TF");
		
		Map<Pair<Integer,String>, IdfTf> idfTfMap = new HashMap<Pair<Integer,String>, IdfTf>();
		
		//calculate idf-tf for each term in each document
		//TODO weighting of idf and tf 
		for(PostingsList pl : index.getAllPostingsLists()) {
			for(Posting p : pl.getPostings()) {
				IdfTf idfTf = new IdfTf();
				idfTf.setDocumentCount(pl.getDocumentCount());
				idfTf.setTermCount(p.getTermCount());
				idfTfMap.put(new Pair<Integer, String>(p.getDocumentId(), pl.getTerm()), idfTf);
			}
		}
		
		index.setIdfTfMap(idfTfMap);
		
		LOGGER.logTime("IDF-TF CALCULATED");
	}
	
	public Index getIndex() {
		return index;
	}
}
