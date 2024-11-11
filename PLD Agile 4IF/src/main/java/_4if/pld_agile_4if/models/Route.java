package _4if.pld_agile_4if.models;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private List<RoadSegment> stopPoints;

    // Constructor
    public Route(List<RoadSegment> stopPoints) {
        this.stopPoints = stopPoints;
    }

    // Getters and Setters
    public List<RoadSegment> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(ArrayList<RoadSegment> stopPoints) {
        this.stopPoints = stopPoints;
    }

    @Override
    public String toString() {
        return "Route{" +
                "stopPoints=" + stopPoints +
                '}';
    }
}
