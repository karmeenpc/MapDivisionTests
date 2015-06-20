
public class Node {
	long id;
	double x, y;
	int cluster;
	Node( long id, double x, double y){
		this.id = id;
		this.x = x;
		this.y = y;
	}
	Node (double x, double y){
		this.x = x;
		this.y = y;
	}
	public void setCluster(int clusterNum){
		cluster = clusterNum;
	}
	public int getCluster(){
		return cluster;
	}
}
