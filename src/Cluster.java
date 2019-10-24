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

    }
}
