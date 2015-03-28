package at.tuwien.bss.index;

public enum EnumIndexingType {

	BOW("bow", new IndexBag()),
	BI("2g", new IndexBi());
	
	private String name;
	private Index index;
	
	private EnumIndexingType(String name, Index index) {
		
		this.name = name;
		this.index = index;
	}
	
	public Index getIndex() { return index; }
	private String getName() { return name; }
	
	public static EnumIndexingType getByName(String name) {
		
		for(EnumIndexingType t : EnumIndexingType.values()) {
			if(name.equals(t.getName())) {
				return t;
			}
		}
		
		//TODO throw element not found exception
		return null;
	}
}
