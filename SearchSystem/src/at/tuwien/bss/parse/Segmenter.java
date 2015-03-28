package at.tuwien.bss.parse;

import java.util.List;

public interface Segmenter {
	
	List<String> segment(List<String> terms);
}
