public class Centroid extends Point {

    public Centroid(Point point) {
        super(point);
    }

    public double setCenter(double[] newCoordinates) {
        assert newCoordinates.length == coordinates.length;

        double distance= 0;
        for (int i = 0; i < coordinates.length; i++) {
            distance += Math.pow(coordinates[i] - newCoordinates[i], 2);
        }
        distance = Math.sqrt(distance);

        this.coordinates = newCoordinates;
        return distance;
    }
}
