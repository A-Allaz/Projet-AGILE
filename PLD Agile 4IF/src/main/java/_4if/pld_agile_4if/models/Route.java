package _4if.pld_agile_4if.models;

import java.util.ArrayList;

public class Route {

    private ArrayList<Location> stopPoints;

    // Constructor
    public Route(ArrayList<Location> stopPoints) {
        this.stopPoints = stopPoints;
    }

    // Getters and Setters
    public ArrayList<Location> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(ArrayList<Location> stopPoints) {
        this.stopPoints = stopPoints;
    }

    @Override
    public String toString() {
        return "Route{" +
                "stopPoints=" + stopPoints +
                '}';
    }
}
