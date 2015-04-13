package at.tuwien.bss.search;

import java.util.Set;

import at.tuwien.bss.index.Index;

public interface Filter {
	
	public Set<Integer> filter(Query query, Index index);
	
	public void setMinResultLength(int resultLength);
	
	public void setValue(double value);
}
