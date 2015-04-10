package at.tuwien.bss.index;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import at.tuwien.bss.logging.SSLogger;

public class Index {
	
	private static final SSLogger LOGGER = SSLogger.getLogger();
	
	private HashMap<String,PostingsList> indexMap = new HashMap<String,PostingsList>();

	private int documentCount = 0;
	public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }
	public int getDocumentCount() { return documentCount; }
	
	private Weighting weightingMethod = null;
	
	/**
	 * add a term to the postings list
	 * @param term
	 */
	public void add(String term, int documentId) {

		PostingsList postingsList = indexMap.get(term);
		
		if (postingsList == null) {
			// term not in list yet
			postingsList = new PostingsList();
			indexMap.put(term, postingsList);
		}
		
		postingsList.add(documentId);
	}
	
	public PostingsList getPostingsList(String term) {
		
		PostingsList postingsList = indexMap.get(term);
		return postingsList;
	}
	
	public Posting getPosting(String term, int documentId) {
		PostingsList postingsList = getPostingsList(term);
		if (postingsList == null) {
			// term does not exist in index
			return null;
		}
		else {
			return postingsList.getPosting(documentId);
		}
	}
	
	public void calculateWeighting(Weighting weightingMethod) {
		
		for (Entry<String, PostingsList> entry : indexMap.entrySet()) {
			PostingsList postingsList = entry.getValue();
			String term = entry.getKey();
			
			for (Posting posting : postingsList) {
				posting.setWeight(weightingMethod.calculate(this, term, postingsList, posting));
			}
		}
		
		this.weightingMethod = weightingMethod;
	}
	
	public Weighting getWeightingMethod() {
		return this.weightingMethod;
	}
	
	public Map<Integer, Float> getTermWeighting(String term) {
		Map<Integer, Float> map = new HashMap<Integer, Float>();
		
		PostingsList postingsList = indexMap.get(term);
		if (postingsList != null) {
			for (Posting posting : postingsList) {
				map.put(posting.getDocumentId(), posting.getWeight());
			}
		}
		
		return map;
	}

	public String print() {
		
		StringBuilder sb = new StringBuilder();
		
		for(String term : indexMap.keySet()) {
			
			PostingsList pl = indexMap.get(term);
			sb.append("* "+ term +":\t(document-frequency: "+ pl.getDocumentFrequency() +")\n");
			sb.append("\tdocuments: "+ pl.print());
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public void save(String filename) throws FileNotFoundException, IOException {
		ObjectOutputStream outputStream = null;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(filename));
		
			// write term count
			outputStream.writeInt(indexMap.size());
					
			for (Entry<String,PostingsList> entry : indexMap.entrySet()) {
				String term = entry.getKey();
				PostingsList postingsList = entry.getValue();
				
				// write term
				outputStream.writeShort(term.length());
				outputStream.writeChars(term);
				
				// write DF (=postings count)
				outputStream.writeShort(postingsList.getDocumentFrequency());
			
				// write postings
				for (Posting posting : postingsList) {
					outputStream.writeShort(posting.getDocumentId());
					outputStream.writeInt(posting.getTermFrequency());
					outputStream.writeFloat(posting.getWeight());
				}
				
			}
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	public void load(String filename) throws FileNotFoundException, IOException {
		ObjectInputStream inputStream = null;
		
		try {
			inputStream = new ObjectInputStream(new FileInputStream(filename));
			
			// read term count
			int termCount = inputStream.readInt();
			
			indexMap = new HashMap<String, PostingsList>(termCount);
			
			for (int ti=0; ti<termCount; ti++) {
				// read term
				int termLength = inputStream.readShort();
				StringBuilder termBuilder = new StringBuilder(termLength);
				for (int i = 0; i < termLength; i++) {
					termBuilder.append(inputStream.readChar());
				}
				String term = termBuilder.toString();
				termBuilder = null;
				
				// read DF (=postingsCount)
				int documentFrequency = inputStream.readShort();
				
				Posting lastPosting = null;
				Posting firstPosting = null;
				for (int i = 0; i < documentFrequency; i++) {
				
					int documentId = inputStream.readShort();
					int termFrequency = inputStream.readInt();
					float weight = inputStream.readFloat();
					
					Posting posting = new Posting(documentId, termFrequency, weight);
					
					if (lastPosting == null) {
						firstPosting = posting;
					}
					else {
						lastPosting.setNextPosting(posting);
					}
					
					lastPosting = posting;
				}
				
				PostingsList postingsList = new PostingsList(firstPosting, documentFrequency);
				indexMap.put(term, postingsList);
			}
		}
		finally {
			if (inputStream != null) {
			    SSLogger.getLogger().log(inputStream.available()+" - read "+indexMap.size()+"terms");
				inputStream.close();
			}
		}
	}
	
	public void exportCsv() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("index.csv", "UTF-8");
			writer.println("Term;DocumentFrequency;docId;termFrequency;weight");
			
			for(String term : indexMap.keySet()) {
				
				PostingsList pl = indexMap.get(term);
				
				ArrayList<Posting> postings = new ArrayList<Posting>(pl.getDocumentFrequency());
				for (Posting posting : pl) {
					postings.add(posting);
				}
				postings.sort(new Comparator<Posting>() {

					@Override
					public int compare(Posting o1, Posting o2) {
						return Float.compare(o1.getWeight(), o2.getWeight());
					}
				});
				
				for (Posting posting : postings) {
					
					writer.println(term +";"+ pl.getDocumentFrequency()+";doc"+posting.getDocumentId()+";"+posting.getTermFrequency()+";"+posting.getWeight());
				}
			
				
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
