package at.tuwien.bss;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.SegmenterBag;
import at.tuwien.bss.parse.SegmenterBi;
import at.tuwien.bss.search.DocumentScore;
import at.tuwien.bss.search.FilterContent;
import at.tuwien.bss.search.Query;
import at.tuwien.bss.search.Searcher;

public class SearchSystem {

	private static final SSLogger LOGGER = SSLogger.getLogger();

	private static final String INDEX_BAG_DAT = "index_bag.dat";
	private static final String INDEX_BI_DAT = "index_bi.dat";
	private static final String INDEX_BAG_CVS = "index_bag.csv";
	private static final String INDEX_BI_CVS = "index_bi.csv";

	private static final String FLAG_BAG = "bag";
	private static final String FLAG_BI = "bi";


	private DocumentCollection documentCollection = new DocumentCollection();

	private Indexer indexerBoW = new Indexer(new SegmenterBag());
	private Indexer indexerBi = new Indexer(new SegmenterBi());

	private static Map<String,Indexer> indexerMap = new HashMap<String,Indexer>();

	public static void main(String[] args) {

		LOGGER.log("Current Working Dir: "+System.getProperty("user.dir"));

		Scanner sc = new Scanner(System.in);

		SearchSystem s = new SearchSystem();
		s.load();

		//String searchQuery = "astronomy club sci space GPS uucp";		//good result: sci.space/62317
		//String searchQuery = "hello i want to buy a computer";

		System.out.print("Command: ");
		while(sc.hasNext()) {

			String[] input = sc.nextLine().split(" ");
			s.readCommand(input);

			System.out.print("\nCommand: ");
		}

		sc.close();
	}

	private void load() {

		documentCollection = new DocumentCollection();
		//documents are placed in the subset folder
		documentCollection.importFolder(".\\..\\..\\subset");
		LOGGER.logTime("imported "+documentCollection.getCount()+" documents");

		File indexBagFile = new File(INDEX_BAG_DAT);
		File indexBiFile = new File(INDEX_BI_DAT);

		if(indexBagFile.exists() && indexBiFile.exists()) {
			//load existing index
			try {
				Index indexBoW = new Index();
				indexBoW.load(INDEX_BAG_DAT);
				indexBoW.exportCsv(INDEX_BAG_CVS);

				Index indexBi = new Index();
				indexBi.load(INDEX_BI_DAT);
				indexBi.exportCsv(INDEX_BI_CVS);

				indexerBoW.loadIndex(indexBoW, documentCollection);
				indexerBi.loadIndex(indexBi, documentCollection);

				LOGGER.logTime("data loaded from files");

			} catch (Exception e) {
				LOGGER.log("error loading data-files");
				index();
			}
		}
		else {
			index();
		}

		indexerMap.put(FLAG_BAG, indexerBoW);
		indexerMap.put(FLAG_BI, indexerBi);
	}

	private void index() {

		LOGGER.logTime("Indexing...");
		try {
			indexerBoW.index(documentCollection, documentCollection.getCount());
			indexerBi.index(documentCollection, documentCollection.getCount());

			//indexerBoW.index(documentCollection, 200);
			//indexerBi.index(documentCollection, 200);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.logTime("Export...");
		try {
			indexerBoW.getIndex().save(INDEX_BAG_DAT);
			indexerBoW.getIndex().exportCsv(INDEX_BAG_CVS);

			indexerBi.getIndex().save(INDEX_BI_DAT);
			indexerBi.getIndex().exportCsv(INDEX_BI_CVS);

		} catch(IOException e) {
			LOGGER.log("error exporting data-files");
		}
	}

	private void readCommand(String[] input) {

		if(input.length == 0) {
			System.out.println("error: no command");
			return;
		}

		// command "-i" for new indexing
		if(input[0].equals("-i")) {
			this.index();
			return;
		}

		// command "-s" for new search
		if(input[0].equals("-s")) {

			//init default values
			Indexer indexer = indexerMap.get(FLAG_BAG);


			//TODO tmp
			String searchQuery = "";

			for(int i = 1; i < input.length; i++) {
				String command = input[i];
				if(command.startsWith("-")) {
					command = command.substring(1, command.length());

					if(indexerMap.containsKey(command)){
						indexer = indexerMap.get(command);
					}
				}
				else {
					searchQuery += " " + command;
				}
			}

			DocumentScore[] result = search(searchQuery, indexer);
			for(int i = 0; i < result.length; i++) {
				System.out.println(result[i] +" : "+ documentCollection.getName(result[i].getDocumentId()));
			}
			
			return;
		}
		
		System.out.println("invalid command");
	}

	private DocumentScore[] search(String searchQuery, Indexer indexer) {

		LOGGER.logTime("START SEARCH");

		Parser parser = new Parser();
		List<String> queryTerms = parser.parse(searchQuery);
		Query query = new Query(indexer.getSegmenter().segment(queryTerms));

		Searcher searcher = new Searcher(indexer.getIndex());
		DocumentScore[] scoreArray = searcher.searchCosineSimilarity(query, new FilterContent(0.5));

		LOGGER.logTime("SEARCH FINISHED");

		DocumentScore[] shortScoreArray = new DocumentScore[Math.min(10, scoreArray.length)];
		for(int i = 0; i < Math.min(10, scoreArray.length); i++) {
			shortScoreArray[i] = scoreArray[i];
		}

		return shortScoreArray;
	}
}
