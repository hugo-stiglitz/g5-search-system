package at.tuwien.lucene;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import at.tuwien.lucene.bm25l.BM25LSimilarity;
import at.tuwien.lucene.documents.DocumentCollection;
import at.tuwien.lucene.logging.Logger;

public class Lucene {
	public static void main(String[] args) throws IOException, ParseException {
		Lucene lucene = new Lucene();

		LOGGER.log("create index...");
		lucene.createIndex();

		LOGGER.log("search...");
		lucene.searchIndex();
	}
	
	private static final Logger LOGGER = Logger.getLogger();
	
	/** WINDOOF **/
	public static final String PATH_DOCUMENTS = ".\\..\\..\\subset";
	public static final String PATH_TOPICS = ".\\..\\..\\topics";
	public static final String PATH_INDEX = ".\\index\\";
	/** UBUNTU **/
	//public static final String PATH_DOCUMENTS = "./../../subset";
	//public static final String PATH_TOPICS = "./../../topics";
	//public static final String PATH_INDEX = "./index/";
	

	public static final String FIELD_NAME = "name";
	public static final String FIELD_CONTENTS = "contents";
	
	
	public Lucene() throws IOException {
		indexDirectoryPath = FileSystems.getDefault().getPath(PATH_INDEX);
		indexDirectory = FSDirectory.open(indexDirectoryPath);
		analyzer = new StandardAnalyzer();
	}
	
	private Path indexDirectoryPath;
	private Directory indexDirectory;
	private Analyzer analyzer;
	
	private DocumentCollection topicCollection = new DocumentCollection();
	
	private void createIndex() throws IOException {
		
		// delete index if already exists
		if (Files.exists(indexDirectoryPath)) {
			for(String file : indexDirectory.listAll()) {
				indexDirectory.deleteFile(file);
			}
			Files.delete(indexDirectoryPath);
		}
		
		// create and configure indexer
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(indexDirectory, conf);
		
		// get documents
		DocumentCollection documentCollection = new DocumentCollection();
		documentCollection.importFolder(PATH_DOCUMENTS);
		
		// add documents to index
		for (Integer documentId : documentCollection) {
			Document document = new Document();

			document.add(new StringField(FIELD_NAME, documentCollection.getDirAndName(documentId), Store.YES));
			document.add(new TextField(FIELD_CONTENTS, documentCollection.getContent(documentId), Store.NO));

			indexWriter.addDocument(document);
		}
		
		//indexWriter.optimize();
		indexWriter.close();
		
		// Load Topics
		topicCollection.importFolder(PATH_TOPICS);
	}
	
	private void searchIndex() throws IOException, ParseException {
		
		DirectoryReader indexReader = DirectoryReader.open(indexDirectory);
	    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	    indexSearcher.setSimilarity(new BM25LSimilarity());
	    
	    String runname = "run_bm25L";
	    
	    for (int i = 0; i < topicCollection.getCount(); i++) {
	    	
	    	
		    // Parse a simple query that searches for "text":
		    QueryParser parser = new QueryParser(FIELD_CONTENTS, analyzer);
		    String topicContent = topicCollection.getContent(i);
		    
		    topicContent = topicContent.replaceAll("[\\+\\-\\&\\|\\!\\(\\)\\{\\}\\[\\]\\^\\\\\\\"~\\*\\?\\:/]", " ");
		    
		    Query query = parser.parse(topicContent);
		    ScoreDoc[] hits = indexSearcher.search(query, 10).scoreDocs;
		    
		    
		    // Iterate through the results:
		    int j = 1;
		    for (ScoreDoc scoreDoc : hits) {
		    	Document hitDoc = indexSearcher.doc(scoreDoc.doc);
		    	
		    	System.out.println(String.format(topicCollection.getName(i) +" Q0 "+  hitDoc.get(FIELD_NAME) +" "+ (j+1) +" "+ scoreDoc.score +" "+ runname));
		    	j++;
			}
			
	    }

	    
	    indexReader.close();
	}
	
	private void explainQueryScoring(IndexSearcher indexSearcher) throws IOException, ParseException {
		
	    QueryParser parser = new QueryParser(FIELD_CONTENTS, analyzer);
	    Query query = parser.parse("Lucene Similarity");
	    ScoreDoc[] hits = indexSearcher.search(query, 2).scoreDocs;
	    
	    for (ScoreDoc scoreDoc : hits) {
	    	System.out.println(indexSearcher.explain(query, scoreDoc.doc));
	    }
	}
}
