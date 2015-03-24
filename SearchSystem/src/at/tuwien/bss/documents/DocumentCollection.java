package at.tuwien.bss.documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DocumentCollection {
	
	public DocumentCollection() {
	}
	
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
            }
        }
		
	}
	
	private ArrayList<String> documentPaths = new ArrayList<String>();
	
	public int getCount() { return documentPaths.size(); }
	
	public String getPath(int documentId) {
		return documentPaths.get(documentId);
	}
	
	public String getContent(int documentId) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(getPath(documentId)));
		return new String(bytes);
	}
}
