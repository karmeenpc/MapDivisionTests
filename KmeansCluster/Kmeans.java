import java.util.*;
import java.io.*;
import java.lang.*;
//Kmeans Clustering Partition Method
//Created by Karmeen Powell-Childress and Ella Holmes
//June 2015 S&T REU Secure Cloud Computing
public class Kmeans {
	int numOfClusters = 3; //total clusters inserted at command line
	//int numOfNodes;
	private ArrayList<Node> roadNodes = new ArrayList<Node>(); 
	private ArrayList<Centroid> centroids = new ArrayList<Centroid>();
	//private ArrayList<Node> allNodes = new ArrayList<Node>();

	/*for iteration purposes the number indicating the cluster 
	will be one less than the cluster's numerical name
    private ArrayList<Node> clusterOne = new ArrayList<Node>(); //clusterNum = 0
    private ArrayList<Node> clusterTwo = new ArrayList<Node>(); //clusterNum = 1
    private ArrayList<Node> clusterThree = new ArrayList<Node>(); //clusterNum = 2
	 */
	//calculate number of node and insert them in a the array list

	public Kmeans(String fileName)throws IOException{
		openRoadFile(fileName);
	}
	//opens road file to get x and y coordinates 
	public void openRoadFile(String path) throws IOException{
		//open to read file
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		//traverse through file
		try{
			while((line = br.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line);
				@SuppressWarnings("unused")
				int id = Integer.parseInt(st.nextToken());
				int no = Integer.parseInt(st.nextToken());

				for(int i = 0; i < no; i++){
					//get Id, x, y values
					long nid = Long.parseLong(st.nextToken());
					double nx = Double.parseDouble(st.nextToken());
					double ny = Double.parseDouble(st.nextToken());

					//create node and add it to arrayList roadNodes
					roadNodes.add(new Node (nid, nx, ny));
					//allNodes.add(new Node (nid, nx, ny));
					//numOfNodes++;
				}
			}
			//close buffered reader
			br.close();
		}catch(FileNotFoundException e){
			System.out.println("ERROR: Unable to locate "+ path + "." );
		} catch(IOException e){
			System.out.println("ERROR: Unable to read from "+ path + "." );
		}
	}
	//create # of centroids based on numOfClusters
	public void createCentroids(){
		//shuffle all nodes to easily get random points
		Collections.shuffle(roadNodes);
		//get random points and remove them from roadNodes because in the next step we compare all of the nodes to the centroids
		for(int i = 0; i < numOfClusters; i++){
			centroids.add(new Centroid(roadNodes.get(i).x , roadNodes.get(i).y));
			//roadNodes.remove(centroids.get(i));
		}
		//print out initial centroids (just for checking randomization during testing)
		System.out.println("Centroids initialized at: ");
		for(int i = 0; i < numOfClusters; i++){
			System.out.println(centroids.get(i).x + "," + centroids.get(i).y);
		}
	}
	//calculate Euclidean distance
	public double calcDist(Node n, Centroid c){
		return Math.sqrt(Math.pow((c.y - n.y), 2) + Math.pow((c.x - n.x), 2));
	}

	//add in remainder nodes in roadNodes and assign them to clusters (set their cluster variable)

	public void initialClusterCreation(){
		//initialize centroids when numOfClusters is predefined to 3
		Centroid a = centroids.get(0);
		Centroid b = centroids.get(1);
		Centroid c = centroids.get(2);
		//calulate Euclidean distance from each node to all centroids
		//while(allNodes.size() < numOfNodes){
		for(int i = 0; i < roadNodes.size(); i++){
			Node n = roadNodes.get(i);
			Double c1 = calcDist(n,a);
			Double c2 = calcDist(n,b);
			Double c3 = calcDist(n,c);
			if(c1 < c2 && c1 <c3){
				n.setCluster(0);
				//clusterOne.add(n);
			}else if(c2 < c3 && c2 < c3){
				n.setCluster(1);
				//clusterTwo.add(n);
			}else{
				n.setCluster(2);
				//clusterThree.add(n);
			}

		}	
	}		
	
	//equivalent to the method above. this will be called after the mean is calculated for each cluster
	public void finalClusterCreation(){
		Centroid a = centroids.get(0);
		Centroid b = centroids.get(1);
		Centroid c = centroids.get(2);

		for(int i = 0; i < roadNodes.size(); i++){
			Node n = new Node(roadNodes.get(i).x,roadNodes.get(i).y);
			Double c1 = calcDist(n,a);
			Double c2 = calcDist(n,b);
			Double c3 = calcDist(n,c);
			if(c1 < c2 && c1 <c3){
				n.setCluster(0);
				//clusterOne.add(n);
			}else if(c2 < c3 && c2 < c3){
				n.setCluster(1);
				//clusterTwo.add(n);
			}else{
				n.setCluster(2);
				//clusterThree.add(n);
			}
		}	
	}

	//calculate the mean for each cluster. this results in a new centroid establishment
	public void calcMean(){
		for(int i = 0; i < numOfClusters; i++){
			double totalX = 0;
			double totalY = 0;
			double totalInCluster = 0;
			for(int j = 0; j < roadNodes.size(); j++){
				if(roadNodes.get(j).getCluster() == i){
					totalX += roadNodes.get(j).x;
					totalY += roadNodes.get(j).y;
					totalInCluster++;
				}
			}
			if(totalInCluster > 0){
				double avgX = totalX/totalInCluster; 
				centroids.get(i).x = avgX;
				double avgY = totalY/totalInCluster; 
				centroids.get(i).y = avgY;  
			}	 
		}
	}

	public void outputClusters(String outputFile) throws IOException{
		//open to file to write
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);

		for(int i = 0; i < numOfClusters; i++){
			//write cluster id
			bw.write("R " + i + "\n");
			//traverse through each node 
			for(int j = 0; j < roadNodes.size(); j++){
				if(roadNodes.get(j).getCluster() == i){
					//write nodes coordinates
					Node temp = roadNodes.get(j); 
					bw.write(temp.x + " " + temp.y + " " );
				}
			}
			bw.write("\n");
		}
		bw.close();
	}
}