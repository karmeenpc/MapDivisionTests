import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.Math;
import java.util.ArrayList;
import java.util.StringTokenizer;

//Naive Partitioning of 3 Regions
//Created by Karmeen Powell-Childress and Ella Holmes
//June 2015 S&T REU Secure Cloud Computing
public class AreaGridPartition{

	private double maxX, maxY, minX, minY;
	private ArrayList<Node> roadNodes = new ArrayList<Node>();

	public AreaGridPartition(String file_path) throws IOException{
		openFile( file_path );
	}

	//Opens road file, finds x y boundaries, and adds Nodes to roadNodes
	public void openFile(String path) throws IOException{
		//open to read file
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line = br.readLine();

		//traverse through file
		while((line = br.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			int no = Integer.parseInt(st.nextToken());

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
				roadNodes.add( new Node( nid, nx, ny ) );
			}
		}
		//close buffered reader
		br.close();
	}

	//puts nodes into partitioned areas of the map
	public Object[][] mapPartition( int partitionX, int partitionY ){
		//calculate width, and height
		double width = Math.ceil( maxX / partitionX );
		double height = Math.ceil( maxY / partitionY );

 		//create a 2D array so it is size x, y
		Object[][] area = new Object[ partitionX ][ partitionY ];

		//copy over roadNodes ArrayList to roadTemp
		ArrayList<Node> roadTemp = roadNodes; 

		//traverse through 2D array
		for( int i = 0; i < partitionX; i++){
			for(int j = 0; j < partitionY;  j++){
				//create a new Array List in each area
				area[i][j] = new ArrayList<Node>();

				//search through every node in roadTemp
				for( int k = 0; k < roadTemp.size(); k++){

					//calculate each x and y min and max for area
					double xMin = width * i;
					double xMax = xMin + width;
					double yMin = height * j;
					double yMax = yMin + height;
					Node node = roadTemp.get(k);

					//if this node fits into these limits
					if(  (xMin < node.x) && (node.x < xMax) ){
						if( (yMin < node.y) && (node.y < yMax) ){
							//add node to area
							((ArrayList<Node>) area[i][j]).add( node );
							//delete node from roadTemp for efficiency
							roadTemp.remove( k );
						}
					}
				}
			}
		}
		return area;   
	}

	public void outputAreas( String outputFile, Object[][] area) throws IOException{
		//open to file to write
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);

		//traversing through each area
		for(int i = 0; i < area.length; i++){
			for(int j = 0; j < area[i].length; j++){
				//write area id
				bw.write("R " + i + j + "\n");

				//traverse through each node in area
				for(int k = 0; k < ((ArrayList<Node>) area[i][j]).size(); k++){
					//write nodes coordinates
					Node temp = ((ArrayList<Node>) area[i][j]).get(k); 
					bw.write(temp.x + " " + temp.y + " " );
				}
			}
			bw.write("\n");
		}
		bw.close();
	}
}
