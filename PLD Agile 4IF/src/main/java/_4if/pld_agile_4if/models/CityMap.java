package _4if.pld_agile_4if.models;

import java.util.ArrayList;

public class CityMap {

    private ArrayList<Intersection> intersections;
    private ArrayList<RoadSegment> roadSegments;
    private Warehouse warehouse;

    // Constructor
    public CityMap(ArrayList<Intersection> intersections, ArrayList<RoadSegment> roadSegments, Warehouse warehouse) {
        this.intersections = intersections;
        this.roadSegments = roadSegments;
        this.warehouse = warehouse;
    }

    // Getters and Setters
    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(ArrayList<Intersection> intersections) {
        this.intersections = intersections;
    }

    public ArrayList<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public void setRoadSegments(ArrayList<RoadSegment> roadSegments) {
        this.roadSegments = roadSegments;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public String toString() {
        return "CityMap{" +
                "intersections=" + intersections +
                ", roadSegments=" + roadSegments +
                ", warehouse=" + warehouse +
                '}';
    }
}