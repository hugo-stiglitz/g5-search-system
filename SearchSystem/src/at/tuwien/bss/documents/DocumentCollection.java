package at.tuwien.bss.documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DocumentCollection {
	
	public DocumentCollection() {
	}
	
	/**
	 * Import all files in the given directory and all subdirectories into this collection
	 * @param folderPath Path to the folter to import
	 */
	public void importFolder(String folderPath) {
		File root = new File(folderPath);
		
		File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
            	importFolder(f.getAbsolutePath());
            }
            else {
                documentPaths.add(f.getAbsolutePath());
                documentIdNameMap.put(f.getName(), documentPaths.size()-1);
            }
        }
	}
	
	private ArrayList<String> documentPaths = new ArrayList<String>();
	private Map<String,Integer> documentIdNameMap = new HashMap<String,Integer>();
	
	public int getCount() { return documentPaths.size(); }
	
	private String getPath(int documentId) {
		return documentPaths.get(documentId);
	}
	
	public String getName(int documentId) {
		File f = new File(getPath(documentId));
		return f.getParentFile().getName()+"/"+f.getName();
	}
	
	public String getContent(int documentId) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(getPath(documentId)));
		return new String(bytes);
	}
	
	public String getContent(String documentName) throws IOException {
		
		if(!documentIdNameMap.containsKey(documentName))
			return null;
		
		return this.getContent(documentIdNameMap.get(documentName));
	}
}
