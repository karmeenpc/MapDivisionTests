import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class drawHotSpots extends JPanel {

	public static void main(String[] args) throws IOException {
		//create frame
		JFrame frame = new JFrame("Display Hot Spots");
		frame.setSize(1000, 1000);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//add spots panel
		frame.add(new drawHotSpots(args[0], args[1]));
		frame.setVisible(true);
	}

	//constant integer that determines scale of map
	private int c = 40;
	private int percentage;
	private String file;

	drawHotSpots(String file, String percentage) throws IOException{
		//set up JPanel
		super();
		//set variables
		this.file = file;
		this.percentage = Integer.parseInt(percentage);
		//paint the data set
		repaint();
	}

	public void paint(Graphics g){
		//need try cat b/c read file can throw an IOException
		try {
			if( file.equals("roads.txt")){
				drawRoads(g);
			}
			else // if( file.equals("naive_output.txt"))
			{
				drawNaive(g);
			}
			
		}catch (IOException e) {e.printStackTrace();}
	}

	//draws the road file
	void drawRoads(Graphics g) throws IOException{

		//read the file
		BufferedReader reader = new BufferedReader( new FileReader(file));
		String line = reader.readLine();
		
		//Sample percentage
		int totalroad = 2350;
		int sample =  (totalroad * percentage )/100;
		int count = 0;
		
		//read through whole file
		while( (line = reader.readLine()) != null & count <= sample ){
			StringTokenizer st = new StringTokenizer(line);
			int id = Integer.parseInt(st.nextToken());
			int no = Integer.parseInt(st.nextToken());

			//array to store roads
			Node[] road = new Node[no];

			//draw each node Node
			g.setColor(Color.BLACK);
			for(int i = 0 ; i < no ; i++ ){
				//get node ID, x and y coord
				long nid = Long.parseLong(st.nextToken());
				double nx = Double.parseDouble(st.nextToken());
				double ny = Double.parseDouble(st.nextToken());
				g.drawOval( (int) nx/c, (int) ny/c, 1, 1);
				road[i] = new Node( (int) nid, (int) nx/c, (int) ny/c);
			}

			//draw each road
			for (int i = 0 ; i < no - 1 ; i++ ){
				g.setColor(Color.red);
				g.drawLine(road[i].x, road[i].y, road[i+1].x, road[i+1].y);
			}	
			//update count by 1
			count++;
		}
	}

	//draws the naive partitioning files
	private void drawNaive(Graphics g) throws IOException{
		//read the file
		BufferedReader reader = new BufferedReader( new FileReader(file));
		String line;

		//for each line
		while( (line = reader.readLine()) != null){
			StringTokenizer st = new StringTokenizer(line);

			//while the line has more tokens
			while( st.hasMoreTokens()){
				//get the next 2 tokens
				String xs = st.nextToken();
				String ys = st.nextToken();

				try{
					//try to convert tokens into doubles
					double x = Double.parseDouble(xs);
					double y = Double.parseDouble(ys);
					g.drawOval( (int) x/c, (int) y/c, 2, 2);

				}catch(NumberFormatException nfe){
					//if it throws exception then its begining a new cluster
					//change the grafic color
					int R = (int) (Math.random()*256);
					int G = (int) (Math.random()*256);
					int B = (int) (Math.random()*256);
					g.setColor(new Color(R, G, B) );
				}
			}	
		}
	}
}