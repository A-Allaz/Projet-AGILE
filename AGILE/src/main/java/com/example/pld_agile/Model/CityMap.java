import java.util.List;

public class CityMap {
    private List<Intersection> intersections;
    private List<RoadSegment> roadSegments;
    private Warehouse warehouse;

    public void loadMap(String xmlFile) {
        // Implementation for loading the map from XML
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }
}
