package at.tuwien.bss.index;

public class Indexer {
	
	private EnumIndexingType type;
	private Index index;
	
	public Indexer(EnumIndexingType type) {
		
		this.type = type;
		index = type.getIndex();
	}
	
	public void add(String fullTerm, int documentId) {
		index.add(fullTerm, documentId);
	}
	
	public Index getIndex() {
		return index;
	}
}
