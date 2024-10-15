package _4if.pld_agile_4if.models;

import java.util.ArrayList;

public class Route {

    private ArrayList<RoadSegment> stopPoints;

    // Constructor
    public Route(ArrayList<RoadSegment> stopPoints) {
        this.stopPoints = stopPoints;
    }

    // Getters and Setters
    public ArrayList<RoadSegment> getStopPoints() {
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
