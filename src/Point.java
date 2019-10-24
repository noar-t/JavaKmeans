import java.util.Arrays;

public class Point {
    private final int pointId;
    private double[] coordinates;

    public Point (int id, double[] coordinates) {
        this.pointId = id;
        this.coordinates = coordinates;
    }

    public Point (Point p) {
        pointId = p.pointId;
        coordinates = p.coordinates.clone();
    }

    public double getEuclideanDistance(Point p) {
        int dimensionality = this.getDimensionality();
        assert p.getDimensionality() == dimensionality;

        double distanceSum = 0;
        for (int i = 0; i < dimensionality; i++) {
            distanceSum += Math.pow(coordinates[i] - p.coordinates[i], 2);
        }

        return Math.sqrt(distanceSum);
    }

    public int getPointId() {
        return pointId;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public int getDimensionality() {
        return coordinates.length;
    }

    @Override
    public String toString() {
        return "Point{" +
                "pointId=" + pointId +
                ", coordinates=" + Arrays.toString(coordinates) +
                '}';
    }
}
