import java.io.IOException;


public class Main {
	public static void main(String[] args) throws IOException{
		Kmeans km = new Kmeans(args[0]);
		km.createCentroids();
		km.initialClusterCreation();
		km.calcMean();
		km.finalClusterCreation();
		km.outputClusters(args[1]);
	}
}
