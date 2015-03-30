package at.tuwien.bss.index;

public class IdfTf {

	private double inverseDocumentFrequency;
	private double termFrequency;
	
	private int totalDocumentCount = 8000;		//TODO get from document collection
	
	public IdfTf() {
		
	}
	
	private IdfTf(double inverseDocumentFrequency, double termFrequency) {
		this.inverseDocumentFrequency = inverseDocumentFrequency;
		this.termFrequency = termFrequency;
	}
	
	public void setDocumentCount(int count) {
		inverseDocumentFrequency = Math.log10(totalDocumentCount / count);
	}
	
	public void setTermCount(int count) {
		termFrequency = Math.log10(count);
	}
	
	public double getInverseDocumentFrequency() { return inverseDocumentFrequency; }
	public double getTermFrequency() { return termFrequency; }
	public double getIdfTf() {
		//TODO weighting of idf and tf
		return inverseDocumentFrequency * termFrequency;
	}
	
	//TODO fix method!!
	public static IdfTf sum(IdfTf val1, IdfTf val2) {
		return new IdfTf(val1.getInverseDocumentFrequency() + val2.getInverseDocumentFrequency(), val1.getTermFrequency() + val2.getTermFrequency());
	}
	
	@Override
	public String toString() {
		return String.format("%.5g%n", getIdfTf());
	}
}
