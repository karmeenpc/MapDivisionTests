import java.lang.Math;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Collections;


//Custom Reservoir sampling
//Ella Holmes June 2015
public class CustomReservoirSamp{

	private double maxX, maxY, minX, minY;
	private int sampleTotal, samplePerRed;
	private int percent;
	private int reducers;
	private ArrayList<Node> roadNodes = new ArrayList<Node>();
	private ArrayList[] reservoir;

	public CustomReservoirSamp(String input, String output, String percent, String reducers) throws IOException{
		this.percent = Integer.parseInt(percent);
		this.reducers = Integer.parseInt(reducers);
		setUp(input);
		ModResSamp();
		depthSearch();
		breadthSearch();
		findUnvisited();
		printClusters(output);
	}

	//adds nodes to roadNodes, finds min + max
	//and calc sampleTotal and samplePerRed
	private void setUp(String input) throws IOException{
		//read in the input file
		FileReader fr = new FileReader(input);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		//counts the number of nodes
		int count = 0;

		//read each line in the file
		while((line = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			int no = Integer.parseInt(st.nextToken());
			//create neighbor array
			Node[] nbors = new Node[no];

			for(int i = 0 ; i < no ; i++ ){
				//get Id, x, y values
				long nid = Long.parseLong(st.nextToken());
				double nx = Double.parseDouble(st.nextToken());
				double ny = Double.parseDouble(st.nextToken());

				//update min and max values
				if(nx > maxX){ maxX = nx; }
				if(nx < minX){ minX = nx; }
				if(ny > maxY){ maxY = ny; }
				if(ny < minY){ minY = ny; }

				//create node and add it to arraylist roadNodes
				Node node = new Node( nid, nx, ny );

				//if the node is contained in roadnodes already
				if( roadNodes.contains(node) ){
					//get the node that is the same
					int index = roadNodes.indexOf(node);
					Node copy = roadNodes.get(index);
					//put it into out array
					nbors[i] = copy;
				}else{
					//else add node to array
					nbors[i] = node;
					//add nodes to roadsList
					roadNodes.add(node);
					//add one to count
					count++;
				}
			}

			//traverse through roadArray to set neighbors
			for( int j = 0; j < no - 1; j++){
				//add each node to be eachothers neighbor
				nbors[j].addNeighbor( nbors[j+1] );
				nbors[j+1].addNeighbor( nbors[j] );
			}
		}
		//close br
		br.close();

		//cal the total number of nodes we want to sample
		sampleTotal = (count * percent)/100;
		//cal how many sample nodes per reducer
		samplePerRed = (sampleTotal / reducers) + 1 ;
		//lastly shuffle the roads file (for testing)
		Collections.shuffle(roadNodes);
	}


	private void ModResSamp(){
		//create a 2D arraylist of nodes.
		reservoir = new ArrayList[reducers];
		//create arraylists in the reservoir
		for(int r = 0; r < reducers; r++){
			reservoir[r] = new ArrayList<Node>();
		}
		
		//customize threshold as a space ratio
		double threshold = 1 / (double) reducers;
		double thX = threshold * maxX;
		double thY = threshold * maxY;

		//add the first node into the first reservoir
		// reservoir[0].add( roadNodes.get(0) );

		//find start nodes that fits in the origainal threshold
		findStarter(thX, thY);

		//traverse through the arraylist of Nodes starting at the 2nd node
		loop:
		for( int i = 1; i < roadNodes.size(); i++){
			Node node = roadNodes.get(i);

			//traverse through each reservoir
			for( int j = 0; j < reservoir.length; j++){
				
				try{
					//try to get the node we are comparing to
					Node start = (Node) reservoir[j].get(0);

					//if the node is within the X threshold of the starting node
					if( node.x < (start.x + thX) && node.x > (start.x - thX)){
						//if the node is within the Y threshold of the starting node
						if( node.y < (start.y + thY) && node.y > (start.y - thY)){
							//add that node to the arraylist of j
							reservoir[j].add(node);
							break;
						}
					}
					//check if the reservoir is full
					if( reservoir[j].size() == samplePerRed){
						RandomModRes( i + 1 , thX, thY);
						break loop;
					}
				}catch(IndexOutOfBoundsException iobe){
					//if the start node does not exist
					//node will become the start node
					reservoir[j].add(node);
					//break the loop b/c we added the node
					break;
				}
			}
		}
	}

	private void findStarter(double x, double y){
		//traverse through all nodes 
		for( int i = 0; i < roadNodes.size(); i ++){
			Node node = roadNodes.get(i);
			//if the node x y is in the threshold xy
			if( (node.x < x) && (node.y < y) ){
				//that node become the starter node
				reservoir[0].add(node);
				return;
			}			
		}
	}

	private void RandomModRes( int index, double thX, double thY ){
		//traverse through the rest of the nodes
		for( int i = index; i < roadNodes.size(); i++){
			//generate a random number from 1 to i of roadnodes
			int random = (int) (Math.random() * i) + 1;
			//if the random number is less than sample per reducer
			if( random < samplePerRed){
				//get the node
				Node node = (Node) roadNodes.get(i);
				//traverse through each reservoir
				for( int j = 0; j < reservoir.length; j++){
					//try get the starter node
					try{
						Node start = (Node) reservoir[j].get(0);
						//if the node is within the X and Y threshold of the starting node
						if( node.x < (start.x + thX) && node.x > (start.x - thX)){
							if( node.y < (start.y + thY) && node.y > (start.y - thY)){
								//if reservoir is full
								if(reservoir[j].size() == samplePerRed){
									//replace the element at index random with node
									reservoir[j].set( random, node );
								}//if reservoir is not full
								else if(reservoir[j].size() < samplePerRed){
									//add it to the end
									reservoir[j].add(node);
								}//break from the inner loop
								break;	
							}
						}
					}catch(IndexOutOfBoundsException iobe){
						//if the start node does not exist
						//node will become the start node
						reservoir[j].add(node);
					}
				}
			}
		}			
	}

	//res 1 is usually always bigger because we always start at j = 0. what if it were a 
	//continuous variable that way mod by 3?

	private void depthSearch(){
		//traverse through each reservoir and its nodes
		for (int i = 0; i < reservoir.length; i++){
			for(int j = 0; j < reservoir[i].size(); j++){
				depthSearch( (Node) reservoir[i].get(j), 10, reservoir[i]);
			}
		}	
	}

    private int depthSearch( Node n, int depth, ArrayList<Node> area){
		//if N has been visited
		if ( n.visited){
			return depth + 1;
		} 
		//if node has not been visited
		else{

			//set nodes visited to true
			n.visited = true;

			//if area does not contain this node
			if( !area.contains(n)){
				//add node to that area
				area.add(n);
			}
			
			//if the deapth is 0
			if( depth == 0 ){
				//stop recursing deapth search
		    	return depth + 1;
			}
			
			//otherwise, sort nodes neighbors based on degree in descending order
			Collections.sort( n.neighbors );

			//deapth search for all of the neighbors of deapth -1 
			for( int i = 0; i < n.neighbors.size(); i++){
                depthSearch( n.neighbors.get(i) , depth -1 , area );
			}
			//after return deapth
			return depth;
		}
	}

	private void breadthSearch(){
		//traverse through each reservoir and its nodes
		// for (int i = 0; i < reservoir.length; i++){
		// 	for(int j = 0; j < reservoir[i].size(); j++){
		// 		breadthSearch( (Node) reservoir[i].get(j), reservoir[i]);
		// 	}
		// }

		ArrayList<Node> unvisited = findUnvisited();
		for( int i = 0; i < unvisited.size(); i++){
			breadthSearch( unvisited.get(i) );
		}
	}

	private void breadthSearch( Node n){
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(n);
		n.visited = true;
		//while queue is not empty
		while( queue.peek() != null){
			//get the first item of the queue
			Node node = queue.remove();

			//find area for the node
			findArea(node);

			//for all of nodes neighbors
			for(int i = 0; i <node.degree; i ++){
				//get the neighbor
				Node neighborNode = node.neighbors.get(i);
				//if neighbor has not been visited
				if(!neighborNode.visited){
					//visit it
					neighborNode.visited = true;
					//and add it to the queue
					queue.add(neighborNode);
				}
			}
		}
	}

	//find an area for node to be added to based on distance
	private void findArea(Node node){
		//minimum distance. default is one of the maps maxXY
		double minDist = maxX;
		//reservoir number. default if the last reservoir
		int resNumber = reservoir.length - 1;
		//for each reservoir
		for (int i = 0; i < reservoir.length; i++){
			//get the starter node
			Node start = (Node) reservoir[i].get(0);
			//calculate the distance btw the two nodes
			double distance= Math.sqrt(Math.pow((start.x - node.x),2) + Math.pow((start.y - node.y), 2));
			//if the distance is smaller
			if ( minDist > distance) {
				//set the minimum distance to distance
				minDist = distance;
				//save the reservoir number
				resNumber = i;
			}
		}
		//add the node to the reservoir that it is closest to.
		reservoir[ resNumber ].add(node);
	}


	//find all nodes are not in a area yet
	private ArrayList<Node> findUnvisited(){
		System.out.println("Finding Unvisited");
		//create a list for the unvistited nodes
		ArrayList<Node> unvisited = new ArrayList<Node>();

		//iterate through all the node
		for(Node node: roadNodes){
			boolean inArea = false;

			//see if any of the areas contain th node
			for(int i = 0; i < reservoir.length; i++){
				if(reservoir[i].contains(node)){
					inArea = true;
				}
			}

			//if no area contains node
			if(!inArea){
				unvisited.add(node);
				System.out.println( node.id + " " + node.x + " " + node.y);
			}
		}
		//return list of unvisted nodes
		return unvisited;
	}


	private void printClusters(String output) throws IOException{

		FileWriter fw = new FileWriter(output);
		BufferedWriter bw =  new BufferedWriter(fw);

		for (int i = 0; i < reservoir.length; i++){
			bw.write("R " + i +"\n");

			for(int j = 0; j < reservoir[i].size(); j++){
				Node node = (Node) reservoir[i].get(j);
				bw.write(node.x + " " + node.y +" ");
			}
			bw.write("\n");
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException{
		//input, output, sample percentage, # reducers
		CustomReservoirSamp crs = new CustomReservoirSamp(args[0], args[1], args[2], args[3]);
	}
}