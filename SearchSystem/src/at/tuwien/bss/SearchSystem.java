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
	// private static final String INDEX_BAG_CVS = "index_bag.csv";
	// private static final String INDEX_BI_CVS = "index_bi.csv";

	private static final String FLAG_HELP = "-h";
	private static final String FLAG_INDEX = "-i";
	private static final String FLAG_SEARCH = "-s";
	private static final String FLAG_TOPIC = "-t";
	private static final String FLAG_TOPIC_ALL = "-tp";

	private static final String FLAG_BAG = "-bag";
	private static final String FLAG_BI = "-bi";

	private static final String FLAG_CONTENT = "-fc";
	private static final String FLAG_MINIMUM = "-fm";

	private static final double DEFAULT_FILTER_VALUE = 0.5d;
	private static final int DEFAULT_RESULT_LENGTH = 10;

	private DocumentCollection documentCollection = new DocumentCollection();
	private DocumentCollection topicCollection = new DocumentCollection();

	private Map<String, Indexer> indexerMap = new HashMap<String, Indexer>();
	private Indexer indexerBoW = new Indexer(new SegmenterBag());
	private Indexer indexerBi = new Indexer(new SegmenterBi());

	private Map<String,Filter> filterMap = new HashMap<String,Filter>();
	private Filter filterContent = new FilterContent(DEFAULT_FILTER_VALUE, DEFAULT_RESULT_LENGTH);
	private Filter filterMinimum = new FilterMinimum();

	public static void main(String[] args) {

		LOGGER.log("Current Working Dir: " + System.getProperty("user.dir"));

		Scanner sc = new Scanner(System.in);

		SearchSystem s = new SearchSystem();
		s.load();

		// searchQuery: "astronomy club sci space GPS uucp" --> good result: sci.space/62317

		System.out.print("Command: ");
		while (sc.hasNextLine()) {

			String[] input = sc.nextLine().split(" ");
			s.readCommand(input);

			System.out.print("\nCommand: ");
		}

		sc.close();
	}

	private void load() {

		// documents are placed in the subset folder
		documentCollection.importFolder(".\\subset");
		LOGGER.logTime("imported " + documentCollection.getCount() + " documents");

		// topics are placed in the subset folder
		topicCollection.importFolder(".\\topics");
		LOGGER.logTime("imported " + topicCollection.getCount() + " topics");

		File indexBagFile = new File(INDEX_BAG_DAT);
		File indexBiFile = new File(INDEX_BI_DAT);

		if (indexBagFile.exists() && indexBiFile.exists()) {
			// load existing index
			try {
				Index indexBoW = new Index();
				indexBoW.load(INDEX_BAG_DAT);
				// indexBoW.exportCsv(INDEX_BAG_CVS);

				Index indexBi = new Index();
				indexBi.load(INDEX_BI_DAT);
				// indexBi.exportCsv(INDEX_BI_CVS);

				indexerBoW.setIndex(indexBoW);
				indexerBi.setIndex(indexBi);

				LOGGER.logTime("data loaded from files");

			} catch (Exception e) {
				LOGGER.log("error loading data-files");
				index();
			}
		} else {
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
			// indexerBoW.getIndex().exportCsv(INDEX_BAG_CVS);

			indexerBi.getIndex().save(INDEX_BI_DAT);
			// indexerBi.getIndex().exportCsv(INDEX_BI_CVS);

		} catch (IOException e) {
			LOGGER.log("error exporting data-files");
		}
		
		LOGGER.logTime("Indexing finished");
	}

	private void readCommand(String[] input) {
		
		LOGGER.setConsoleLogging(true);

		if (input.length == 0) {
			System.out.println("error: no command");
			return;
		}

		// command "-h" for help
		if (input[0].equals(FLAG_HELP)) {
			System.out.println(HELP_MESSAGE);
			return;
		}

		boolean topicProcessing = false;
		String runname = "";
		String searchQuery = "";

		// command "-i" for new indexing
		if (input[0].equals(FLAG_INDEX)) {
			this.index();
			return;
		}

		// command "-t" for topic search
		else if (input[0].equals(FLAG_TOPIC)) {
			for(int i = 1; i < input.length; i++) {
				String command = input[i];
				if (!command.startsWith("-")) {
					try {
						searchQuery = topicCollection.getContent(command);
					} catch (IOException e) {
						System.out.println("topic file not existing");
					}
				}
			}
		}

		// command "-ta" for search of all topics
		else if(input[0].equals(FLAG_TOPIC_ALL)) {
			LOGGER.setConsoleLogging(false);
			topicProcessing = true;
			if(input.length > 1) {
				runname = input[1];
			}
		}

		// command "-s" for free text search
		else if (input[0].equals(FLAG_SEARCH)) {

			for (int i = 1; i < input.length; i++) {
				String command = input[i];
				if (!command.startsWith("-")) {
					searchQuery += " " + command;
				}
			}
		} else {
			System.out.println("invalid command");
			return;
		}

		// init parameters
		int resultLength = DEFAULT_RESULT_LENGTH;
		Indexer indexer = indexerMap.get(FLAG_BAG);
		Filter filter = filterMap.get(FLAG_CONTENT);

		Pattern numberPattern = Pattern.compile("\\d+");

		for (int i = 1; i < input.length; i++) {
			String fullCommand = input[i];
			if (fullCommand.startsWith("-")) {
				String command = fullCommand.split("[(]")[0];

				if (indexerMap.containsKey(command)) {
					indexer = indexerMap.get(command);
				}

				if (filterMap.containsKey(command)) {
					filter = filterMap.get(command);

					Matcher m = numberPattern.matcher(fullCommand);
					if (m.find()) {
						try {
							double value = Double.valueOf(m.group());

							if (value > 0 && value <= 100) {
								filter.setValue(value / 100);
							}
							
						} catch(NumberFormatException e) {
							filter.setValue(DEFAULT_FILTER_VALUE);
						}
					}
				}

				if(command.equals("-r")) {
					Matcher m = numberPattern.matcher(fullCommand);
					if(m.find()) {
						try {
							int value = Integer.valueOf(m.group());

							if(value > 0 && value <= documentCollection.getCount()) {						
								resultLength = value;
							}
							else {
								System.out.println("invalid result length parameter");
							}

						} catch(NumberFormatException e) {
							System.out.println("invalid result length parameter");
						}
					}
				}
			}
		}

		filter.setMinResultLength(resultLength);

		// topic processing
		if(topicProcessing) {
			for(int i = 0; i < topicCollection.getCount(); i++) {
				try {
					searchQuery = topicCollection.getContent(i);
					DocumentScore[] result = search(searchQuery, indexer, filter, resultLength);
					for(int j = 0; j < result.length; j++) {
						//System.out.println(String.format("%-15s %2d. %-35s %-10s %s", topicCollection.getName(i) +":", j+1, documentCollection.getName(result[j].getDocumentId()), result[j], runname));
						//TODO what is Q0?
						System.out.println(String.format(topicCollection.getName(i) +" Q0 "+  documentCollection.getDirAndName(result[j].getDocumentId()) +" "+ (j+1) +" "+ result[j] +" "+ runname));
					}
				} catch (IOException e) {
					System.out.println("unexpected error");
				}
			}
		}
		// other search
		else {
			if(searchQuery == null || searchQuery.equals("")) {
				System.out.println("invalid search query");
				return;
			}

			DocumentScore[] result = search(searchQuery, indexer, filter, resultLength);
			for(int i = 0; i < result.length; i++) {
				System.out.println(String.format("%2d. %-35s %s", i+1, documentCollection.getDirAndName(result[i].getDocumentId()), result[i]));
			}

			if(result.length == 0) {
				System.out.println("no search results");
			}

			if(result.length > 0 && result.length < resultLength) {
				System.out.println("warning: less than "+ resultLength +" search results available");
			}
		}
	}

	private DocumentScore[] search(String searchQuery, Indexer indexer, Filter filter, int resultLength) {

		LOGGER.logTime("Search...");

		Parser parser = new Parser();
		List<String> queryTerms = parser.parse(searchQuery);
		Query query = new Query(indexer.getSegmenter().segment(queryTerms));

		Searcher searcher = new Searcher(indexer.getIndex());
		DocumentScore[] scoreArray = searcher.searchCosineSimilarity(query, filter, resultLength);

		LOGGER.logTime("Search finished");

		DocumentScore[] shortScoreArray = new DocumentScore[Math.min(resultLength, scoreArray.length)];
		for(int i = 0; i < shortScoreArray.length; i++) {
			shortScoreArray[i] = scoreArray[i];
		}

		return shortScoreArray;
	}

	private static final String HELP_MESSAGE = ""+
			"* (necessary) operation command\n"+
			" ** -h ... help\n"+
			" ** -i ... re-index document collection\n"+
			" ** -s “query” ... search via free text query\n"+
			" ** -t “topic-name” ... search via a specific topic\n"+
			" ** -tp “run-name” ...  run topic processing over all indexed topics\n"+
			"\n* (optional) index flag\n"+
			" ** -bag ... user bag of words index (DEFAULT)\n"+
			" ** -bi ... use bi-word index\n"+
			"\n* (optional) filter flag\n"+
			" ** -fc ... use “content filter” with threshold 50% (DEFAULT)\n"+
			" ** -fc(30) ... use “content filter” with threshold 30% (value: 0-100)\n"+
			" ** -fm ... use “minimum filter”\n"+
			"\n* (optional) result length\n"+
			" ** -r(20) ... show 20 search results (DEFAULT: 10) (value > 0)\n"+
			"\n* examples:\n"+
			" ** get best 15 results for query \"astronomy club sci space GPS uucp\" in bag of words index using the content filter with a threshold of 70%:\n"+
			"       -s -fc(70) -r(15) astronomy club sci space GPS uucp\n"+
			" ** get best 10 results for topic4 in bi-word index using the minimum filter:\n"+
			"       -t -bi -fm topic4\n"+
			" ** run topic processing with run-name “test” for bi-word index using conetent filter with a threshold of 50%:\n"+
			"       -ta -bi test";
}
