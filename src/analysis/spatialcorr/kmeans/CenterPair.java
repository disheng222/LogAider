package analysis.spatialcorr.kmeans;

public class CenterPair {

	private Center a;
	private Center b;

	public CenterPair(Center a, Center b) {
		this.a = a;
		this.b = b;
	}
	
	public Center getA() {
		return a;
	}
	public void setA(Center a) {
		this.a = a;
	}
	public Center getB() {
		return b;
	}
	public void setB(Center b) {
		this.b = b;
	}
	public Center getMergeCenter(KMeansSolution sol)
	{
		a.setI((a.getI()+b.getI())/2);
		a.setJ((a.getJ()+b.getJ())/2);
		a.setK((a.getK()+b.getK())/2);
		a.setT((a.getT()+b.getT())/2);
		a.getKMeansSet().addAll(b.getKMeansSet());
		sol.kmeansSetList.remove(b.getKMeansSet());
		return a;
	}
}
