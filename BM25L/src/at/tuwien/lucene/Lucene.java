package at.tuwien.lucene;

import java.io.IOException;
import java.nio.file.FileSystems;

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

import at.tuwien.lucene.documents.DocumentCollection;

public class Lucene {
	public static void main(String[] args) throws IOException, ParseException {
		Lucene lucene = new Lucene();

		lucene.createIndex();

		lucene.searchIndex();
	}
	
	public static final String PATH_DOCUMENTS = ".\\..\\..\\subset";
	public static final String PATH_INDEX = ".\\index\\";

	public static final String FIELD_NAME = "name";
	public static final String FIELD_CONTENTS = "contents";
	
	
	public Lucene() throws IOException {
		indexDirectory = FSDirectory.open(FileSystems.getDefault().getPath(PATH_INDEX));
		analyzer = new StandardAnalyzer();
	}
	
	private Directory indexDirectory;
	private Analyzer analyzer;
	
	private void createIndex() throws IOException {
		
		IndexWriterConfig conf = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(indexDirectory, conf);
		
		DocumentCollection documentCollection = new DocumentCollection();
		documentCollection.importFolder(PATH_DOCUMENTS);
		
		for (Integer documentId : documentCollection) {
			Document document = new Document();

			document.add(new StringField(FIELD_NAME, documentCollection.getDirAndName(documentId), Store.YES));
			document.add(new TextField(FIELD_CONTENTS, documentCollection.getContent(documentId), Store.NO));

			indexWriter.addDocument(document);
		}
		
		//indexWriter.optimize();
		indexWriter.close();
	}
	
	private void searchIndex() throws IOException, ParseException {
		
		DirectoryReader indexReader = DirectoryReader.open(indexDirectory);
	    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	    indexSearcher.setSimilarity(new BM25Similarity());
	    
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser(FIELD_CONTENTS, analyzer);
	    Query query = parser.parse("text");
	    ScoreDoc[] hits = indexSearcher.search(query, 15).scoreDocs;
	    
	    // Iterate through the results:
	    for (ScoreDoc scoreDoc : hits) {
	    	Document hitDoc = indexSearcher.doc(scoreDoc.doc);
	    	
	    	System.out.println(hitDoc.get(FIELD_NAME));
		}

	    indexReader.close();
	}
}
