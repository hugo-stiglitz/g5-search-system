package at.tuwien.bss.search;

import java.util.Set;

import at.tuwien.bss.index.Index;

public interface Filter {
	
	public Set<Integer> filter(Query query, Set<Integer> documents, Index index);
}
