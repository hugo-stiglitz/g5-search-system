package at.tuwien.bss.search;

public class HeapSort {
	
	public static void heapSort(DocumentScore[] values, int count) {
		
		//generate maximum heap
		for (int i = values.length / 2; i >= 0; i--) {
			percolate(values, i, values.length);
		}

		//sort first "count" elements
		for(int i = count; i > 0; i--) {
			//swap last element with first
			swap(values, 0, i); 
			// create heap from position 0 to i
			percolate(values, 0, i); 
		}
	}
	
	private static void percolate(DocumentScore[] values, int element, int last) {
		
		int i = element + 1;
		int j;
		
		while (2 * i <= last) {
			j = 2 * i;
			
			if (j < last) {
				if (values[j-1].compareTo(values[j]) > 0)  {
					j++;
				}
			}
			
			if (values[i-1].compareTo(values[j-1]) > 0) {
				swap(values,i-1,j-1);
				i = j;
			}
			else {
				break;
			}
		}
	}

	private static void swap(DocumentScore[] values, int i1, int i2) {
		
		DocumentScore tmp = values[i1];
		values[i1] = values[i2];
		values[i2] = tmp;
	}
}
