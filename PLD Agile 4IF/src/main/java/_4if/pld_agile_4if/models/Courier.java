package _4if.pld_agile_4if.models;

import java.util.ArrayList;

public class Courier {

    private int id;
    private boolean availability;
    private Route currentRoute;

    // Constructor
    public Courier(int id, boolean availability, Route currentRoute) {
        this.id = id;
        this.availability = availability;
        this.currentRoute = currentRoute;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(Route currentRoute) {
        this.currentRoute = currentRoute;
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id=" + id +
                ", availability=" + availability +
                ", currentRoute=" + currentRoute +
                '}';
    }
}