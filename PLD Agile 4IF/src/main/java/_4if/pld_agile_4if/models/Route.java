package _4if.pld_agile_4if.models;

import java.util.ArrayList;
import java.util.List;

public class Route {
    /**
     * Route class
     */

    private List<RoadSegment> stopPoints;

    // Constructor
    /**
     * Constructor
     * @param stopPoints List of road segments
     */
    public Route(List<RoadSegment> stopPoints) {
        this.stopPoints = stopPoints;
    }

    // Getters and Setters
    /**
     * Get the list of road segments
     * @return List of road segments
     */
    public List<RoadSegment> getStopPoints() {
        return stopPoints;
    }

    /**
     * Set the list of road segments
     * @param stopPoints List of road segments
     */
    public void setStopPoints(ArrayList<RoadSegment> stopPoints) {
        this.stopPoints = stopPoints;
    }

    /**
     * Get the String representation of the Route
     * @return String representation of the Route
     */
    @Override
    public String toString() {
        return "Route{" +
                "stopPoints=" + stopPoints +
                '}';
    }
}
