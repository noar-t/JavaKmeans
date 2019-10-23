import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KMeans {
    int numThreads;
    Point[] points;
    Centroid[] centroids;

    public KMeans(String inputPath, int numThreads, int numCentroids) throws IOException {
        this.numThreads = numThreads;

        readInput(inputPath);
        initCentroids(numCentroids);
    }

    /* Parse input file of points */
    private void readInput(String inputPath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputPath));
        bufferedReader.readLine(); // First line is dimensionality which is unneeded

        List<Point> list = bufferedReader.lines()
                .map(x -> {
                    String[] tokens = x.split(" ");
                    int id = Integer.parseInt(tokens[0]);
                    double[] coordinates = new double[tokens.length - 1];
                    for (int i = 1; i < tokens.length; i++) {
                        coordinates[i-1] = Double.parseDouble(tokens[i]);
                    }
                    return new Point(id, coordinates);
                })
                .collect(Collectors.toList());

        points = list.toArray(points);
    }

    private void initCentroids(int numCentroids) {
        Random random = new Random();
        centroids = new Centroid[numCentroids];
        for (int i = 0; i < numCentroids; i++) {
            centroids[i] = new Centroid(points[random.nextInt(points.length)]);
        }
    }

    public void runNIterations(int iterations) {

        for (int i = 0; i < iterations; i++) {
            iterate();
        }
    }

    public void iterate() {
        ;
    }

}
