
import java.util.ArrayList;

public class Node implements Comparable<Node>{
	
	long id;
	double x, y;
	int degree = 0;
	boolean visited = false;
	ArrayList<Node> neighbors = new ArrayList<Node>();
	
	Node( long id, double x, double y){
		this.id = id;
		this.x = x;
		this.y = y;
	}

	//compares node degrees and sorts them in decending order. 
	//this is used for deapthfist search of neighbors based on degree
	@Override
	public int compareTo(Node node){
		int compareDegree = node.getDegree();
		return compareDegree - this.degree;
	}

	//equals is required for arraylist.contains
	//determins if object is the same as this node
	public boolean equals(Object o){
		Node node = (Node) o;
		if( x == node.x && y == node.y ){
			return true;
		}
		return false;
	}

	//add node to arraylist list that stores neighbors
	public void addNeighbor(Node n){
		neighbors.add(n);
		//update degree value
		degree += 1;
	}

	//get degree value
	public int getDegree(){
		return degree;
	}
}