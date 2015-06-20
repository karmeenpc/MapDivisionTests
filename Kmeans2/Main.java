import java.io.IOException;


public class Main {
	public static void main(String[] args) throws IOException{
		Kmeans2 km = new Kmeans2(args[0]);
		km.createCentroids();
		km.initialClusterCreation();
		km.calcMean();
		km.finalClusterCreation();
		km.outputClusters(args[1]);
	}
}
