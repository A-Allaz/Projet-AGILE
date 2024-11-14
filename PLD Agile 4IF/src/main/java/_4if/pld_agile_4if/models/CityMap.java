package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "reseau")
public class CityMap {
    /**
     * List of intersections
     */

    private ArrayList<Intersection> intersections;
    private ArrayList<RoadSegment> roadSegments;
    private Warehouse warehouse;

    // Constructor
    /**
     * Default constructor
     */
    public CityMap() {}

    /**
     * Constructor
     * @param intersections List of intersections
     * @param roadSegments List of road segments
     * @param warehouse Warehouse
     */
    public CityMap(ArrayList<Intersection> intersections, ArrayList<RoadSegment> roadSegments, Warehouse warehouse) {
        this.intersections = intersections;
        this.roadSegments = roadSegments;
        this.warehouse = warehouse;
    }


    // Getters and Setters
    /**
     * Get the list of intersections
     * @return List of intersections
     */
    @XmlElement(name = "noeud")
    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Set the list of intersections
     * @param intersections List of intersections
     */
    public void setIntersections(ArrayList<Intersection> intersections) {
        this.intersections = intersections;
    }

    /**
     * Get the list of road segments
     * @return List of road segments
     */
    @XmlElement(name = "troncon")
    public ArrayList<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    /**
     * Get the road segment between two intersections
     * @param source Source intersection
     * @return Road segment
     */
    public ArrayList<RoadSegment> getOutGoingRoadSegments(long source) {
        ArrayList<RoadSegment> outgoing = new ArrayList<>();
        for (RoadSegment roadSegment : this.getRoadSegments()) {
            if ( roadSegment.getOrigin() == source ) {
                outgoing.add(roadSegment);
            }
        }
        return outgoing;
    }

    /**
     * Set the list of road segments
     * @param roadSegments List of road segments
     */
    public void setRoadSegments(ArrayList<RoadSegment> roadSegments) {
        this.roadSegments = roadSegments;
    }

    /**
     * Get the warehouse
     * @return Warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Set the warehouse
     * @param warehouse Warehouse
     */
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    /**
     * Get the String representation of the CityMap
     * @return String representation of the CityMap
     */
    @Override
    public String toString() {
        return "CityMap{" +
                "intersections=" + intersections +
                ", roadSegments=" + roadSegments +
                ", warehouse=" + warehouse +
                '}';
    }
}