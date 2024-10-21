package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reseau")
public class CityMap {

    private ArrayList<Intersection> intersections;
    private ArrayList<RoadSegment> roadSegments;
    private Warehouse warehouse;

    // Constructor
    public CityMap() {}

    public CityMap(ArrayList<Intersection> intersections, ArrayList<RoadSegment> roadSegments, Warehouse warehouse) {
        this.intersections = intersections;
        this.roadSegments = roadSegments;
        this.warehouse = warehouse;
    }

    // Getters and Setters
    @XmlElement(name = "noeud")
    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(ArrayList<Intersection> intersections) {
        this.intersections = intersections;
    }

    @XmlElement(name = "troncon")
    public ArrayList<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public ArrayList<RoadSegment> getOutGoingRoadSegments(long source) {
        ArrayList<RoadSegment> outgoing = new ArrayList<>();
        for (RoadSegment roadSegment : this.getRoadSegments()) {
            if ( roadSegment.getOrigin() == source ) {
                outgoing.add(roadSegment);
            }
        }
        return outgoing;
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