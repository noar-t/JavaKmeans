import java.util.LinkedList;
import java.util.List;

public class Cluster {
    final private List<Point> points;

    public Cluster () {
        points = new LinkedList<>();
    }

    public void add(Point p) {
        points.add(p);
    }

    public void add(Cluster c) {
        points.addAll(c.points);
    }

    public boolean empty() {
        return points.size() == 0;
    }

    /**
     * Compute the average of all points in the cluster
     * @return
     */
    public double[] getCenter() {
        assert points.get(0) != null;

        int dimensionality = points.get(0).getDimensionality();
        double[] sum = new double[dimensionality];

        for (Point p : points) {
            double[] coordinates = p.getCoordinates();

            for (int i = 0; i < dimensionality; i++) {
                sum[i] += coordinates[i];
            }
        }

        for (int i = 0; i < sum.length; i++) {
            sum[i] /= points.size();
        }
        //TODO

        return sum;
    }
}
