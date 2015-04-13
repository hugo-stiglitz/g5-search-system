package at.tuwien.bss;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.tuwien.bss.documents.DocumentCollection;
import at.tuwien.bss.index.Index;
import at.tuwien.bss.index.Indexer;
import at.tuwien.bss.logging.SSLogger;
import at.tuwien.bss.parse.Parser;
import at.tuwien.bss.parse.SegmenterBag;
import at.tuwien.bss.parse.SegmenterBi;
import at.tuwien.bss.search.DocumentScore;
import at.tuwien.bss.search.Filter;
import at.tuwien.bss.search.FilterContent;
import at.tuwien.bss.search.FilterMinimum;
import at.tuwien.bss.search.Query;
import at.tuwien.bss.search.Searcher;

public class SearchSystem {

	private static final SSLogger LOGGER = SSLogger.getLogger();

	private static final String INDEX_BAG_DAT = "index_bag.dat";
	private static final String INDEX_BI_DAT = "index_bi.dat";
	private static final String INDEX_BAG_CVS = "index_bag.csv";
	private static final String INDEX_BI_CVS = "index_bi.csv";

	private static final String FLAG_HELP = "-h";
	private static final String FLAG_INDEX = "-i";
	private static final String FLAG_SEARCH = "-s";
	private static final String FLAG_TOPIC = "-t";

	private static final String FLAG_BAG = "-bag";
	private static final String FLAG_BI = "-bi";

	private static final String FLAG_CONTENT = "-fc";
	private static final String FLAG_MINIMUM = "-fm";

	private static final double DEFAULT_FILTER_VALUE = 0.5d;

	private DocumentCollection documentCollection = new DocumentCollection();
	private DocumentCollection topicCollection = new DocumentCollection();

	private Map<String,Indexer> indexerMap = new HashMap<String,Indexer>();
	private Indexer indexerBoW = new Indexer(new SegmenterBag());
	private Indexer indexerBi = new Indexer(new SegmenterBi());

	private Map<String,Filter> filterMap = new HashMap<String,Filter>();
	private Filter filterContent = new FilterContent(DEFAULT_FILTER_VALUE);
	private Filter filterMinimum = new FilterMinimum();

	public static void main(String[] args) {

		LOGGER.log("Current Working Dir: "+System.getProperty("user.dir"));

		Scanner sc = new Scanner(System.in);

		SearchSystem s = new SearchSystem();
		s.load();

		//String searchQuery = "astronomy club sci space GPS uucp";		//good result: sci.space/62317
		//String searchQuery = "hello i want to buy a computer";

		System.out.print("Command: ");
		while(sc.hasNextLine()) {

			String[] input = sc.nextLine().split(" ");
			s.readCommand(input);

			System.out.print("\nCommand: ");
		}

		sc.close();
	}

	private void load() {

		//documents are placed in the subset folder
		documentCollection.importFolder(".\\..\\..\\subset");
		LOGGER.logTime("imported "+documentCollection.getCount()+" documents");

		//topics are placed in the subset folder
		topicCollection.importFolder(".\\..\\..\\topics");
		LOGGER.logTime("imported "+topicCollection.getCount()+" topics");

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

		filterMap.put(FLAG_CONTENT, filterContent);
		filterMap.put(FLAG_MINIMUM, filterMinimum);
	}

	private void index() {

		LOGGER.logTime("Indexing...");
		try {
			indexerBoW.index(documentCollection, documentCollection.getCount());
			indexerBi.index(documentCollection, documentCollection.getCount());
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

		// command "-h" for help
		if(input[0].equals(FLAG_HELP)) {
			//TODO show help
			System.out.println("help: in progress");
			return;
		}

		String searchQuery = "";

		// command "-i" for new indexing
		if(input[0].equals(FLAG_INDEX)) {
			this.index();
			return;
		}

		// command "-t" for topic search
		else if(input[0].equals(FLAG_TOPIC)) {

			//TODO set flags
			for(int i = 1; i < input.length; i++) {
				String command = input[i];
				if(!command.startsWith("-")) {
					try {
						searchQuery = topicCollection.getContent(command);
					} catch (IOException e) {
						System.out.println("topic file not existing");
					}
				}
			}
		}

		// command "-s" for free text search
		else if(input[0].equals(FLAG_SEARCH)) {

			for(int i = 1; i < input.length; i++) {
				String command = input[i];
				if(!command.startsWith("-")) {
					searchQuery += " " + command;
				}
			}
		}
		else {
			System.out.println("invalid command");
			return;
		}

		//init default values
		Indexer indexer = indexerMap.get(FLAG_BAG);
		Filter filter = filterMap.get(FLAG_CONTENT);

		Pattern numberPattern = Pattern.compile("\\d+");

		for(int i = 1; i < input.length; i++) {
			String fullCommand = input[i];
			if(fullCommand.startsWith("-")) {
				String command = fullCommand.split("[(]")[0];

				if(indexerMap.containsKey(command)) {
					indexer = indexerMap.get(command);
				}

				if(filterMap.containsKey(command)) {
					filter = filterMap.get(command);

					Matcher m = numberPattern.matcher(fullCommand);
					if(m.find()) {
						try {
							double value = Double.valueOf(m.group());

							if(value > 0 && value <= 100) {						
								filter.setValue(value / 100);
							}
							
						} catch(NumberFormatException e) {
							filter.setValue(DEFAULT_FILTER_VALUE);
						}
					}
				}
			}
		}

		DocumentScore[] result = search(searchQuery, indexer, filter);
		for(int i = 0; i < result.length; i++) {
			System.out.println(result[i] +" : "+ documentCollection.getName(result[i].getDocumentId()));
		}

		if(result.length == 0) {
			System.out.println("no search results");
		}

		if(result.length > 0 && result.length < 10) {
			System.out.println("warning: less than 10 search results available");
		}
	}

	private DocumentScore[] search(String searchQuery, Indexer indexer, Filter filter) {

		LOGGER.logTime("START SEARCH");

		Parser parser = new Parser();
		List<String> queryTerms = parser.parse(searchQuery);
		
		List<String> tmp = indexer.getSegmenter().segment(queryTerms);
		Query query = new Query(tmp);

		Searcher searcher = new Searcher(indexer.getIndex());
		DocumentScore[] scoreArray = searcher.searchCosineSimilarity(query, filter);

		LOGGER.logTime("SEARCH FINISHED");

		DocumentScore[] shortScoreArray = new DocumentScore[Math.min(10, scoreArray.length)];
		for(int i = 0; i < Math.min(10, scoreArray.length); i++) {
			shortScoreArray[i] = scoreArray[i];
		}

		return shortScoreArray;
	}
}
