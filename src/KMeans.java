import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class KMeans {
    private final int numThreads;
    private final int iterations;
    private final double threshold;
    private boolean done;
    private Point[] points;
    private Centroid[] centroids;
    private Cluster[] clusters;

    public KMeans(String inputPath, int numThreads, int numCentroids, int iterations, double threshold) throws IOException {
        this.numThreads = numThreads;
        this.iterations = iterations;
        this.threshold = threshold;
        this.done = false;

        readInput(inputPath);
        initCentroids(numCentroids);
        clusters = new Cluster[centroids.length];
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

        points = new Point[list.size()];
        points = list.toArray(points);
    }

    private void initCentroids(int numCentroids) {
        Random random = new Random();
        centroids = new Centroid[numCentroids];
        for (int i = 0; i < numCentroids; i++) {
            centroids[i] = new Centroid(points[random.nextInt(points.length)]);
        }
    }

    public void compute() {
        ArrayList<LocalSumKmeansWorker> localSumKmeansWorkers = new ArrayList<>(numThreads);
        CyclicBarrier barrier = new CyclicBarrier(numThreads);

        for (int i = 0; i < numThreads; i++) {
            localSumKmeansWorkers.add(i, new LocalSumKmeansWorker(i, barrier));
        }

        localSumKmeansWorkers.forEach(LocalSumKmeansWorker::start);
        localSumKmeansWorkers.forEach(x -> {
            try {
                x.join();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        });
    }

    class LocalSumKmeansWorker extends Thread {
        private final int workerId;
        private final CyclicBarrier barrier;

        public LocalSumKmeansWorker(int workerId, CyclicBarrier barrier) {
            this.workerId = workerId;
            this.barrier = barrier;
        }

        public void run() {
            final int workPerThread = points.length / numThreads;
            final int startIndex = workerId * workPerThread;
            final int endIndex = startIndex + (workPerThread) - 1; // TODO wrong for last thread
            final Cluster[] localClusters = new Cluster[centroids.length];
            int iteration = 0;

            while (!done) {
                // Initialize local clusters
                for (int i = 0; i < localClusters.length; i++) {
                    localClusters[i] = new Cluster();
                }



                // Iterate over points for thread instance
                for (int i = startIndex; i <= endIndex; i++) {
                    Point curPoint = points[i];
                    int closestCentroidId = -1;
                    double min = Double.MAX_VALUE;

                    // Find closest centroid
                    for (int j = 0; j < centroids.length; j++) {
                        Centroid curCentroid = centroids[j];
                        double distance = curPoint.getEuclideanDistance(curCentroid);

                        if (distance < min) {
                            min = distance;
                            closestCentroidId = j;
                        }
                    }
                    localClusters[closestCentroidId].add(curPoint);
                }

                // Setup/reset global clusters
                if (workerId == 0) {
                    for (int i = 0; i < clusters.length; i++) {
                        clusters[i] = new Cluster();
                    }
                }

                // End init global clusters stage
                try {
                    barrier.await();
                } catch (Exception e) {
                    System.out.println(e);
                }

                // Accumulate local updates into global centroids
                // TODO i think this requires monitor support
                for (int i = 0; i < clusters.length; i++) {
                    synchronized (clusters[i]) {
                        clusters[i].add(localClusters[i]);
                    }
                }

                // End local summing stage
                try {
                    barrier.await();
                } catch (Exception e) {
                    System.out.println(e);
                }

                // Single thread adjust the centroids
                // TODO could parallelize this
                if (workerId == 0) {
                    double minDistance = Double.MAX_VALUE;
                    double tmp = 0;
                    for (int i = 0; i < centroids.length; i++) {
                        if (clusters[i].empty()) {
                            continue;
                        }
                        tmp = centroids[i].setCenter(clusters[i].getCenter());
                        minDistance = Math.min(tmp, minDistance);
                    }
                    if (minDistance < threshold || iteration >= iterations) {
                        done = true;
                    }
                }

                // End single thread global update stage
                try {
                    barrier.await();
                } catch (Exception e) {
                    System.out.println(e);
                }

                iteration++;
            }

            System.out.println(this.getName());
        }
    }

}
