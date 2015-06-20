import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws NumberFormatException, IOException{
		AreaGridPartition agp = new AreaGridPartition( args[0] );
		agp.outputAreas(args[1], agp.mapPartition( Integer.parseInt(args[2]) , Integer.parseInt(args[3]) ));
	}

}
