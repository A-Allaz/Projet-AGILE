package _4if.pld_agile_4if.models;

import java.util.ArrayList;

public class Courier {
    /**
     * Courier class
     */

    private int id;
    private boolean availability;
    private Route currentRoute;

    // Constructor
    /**
     * Constructor
     * @param id Courier ID
     * @param availability Courier availability
     * @param currentRoute Current route
     */
    public Courier(int id, boolean availability, Route currentRoute) {
        this.id = id;
        this.availability = availability;
        this.currentRoute = currentRoute;
    }

    // Getters and Setters
    /**
     * Get the courier ID
     * @return Courier ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the courier ID
     * @param id Courier ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the courier availability
     * @return Courier availability
     */
    public boolean isAvailability() {
        return availability;
    }

    /**
     * Set the courier availability
     * @param availability Courier availability
     */
    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    /**
     * Get the current route
     * @return Current route
     */
    public Route getCurrentRoute() {
        return currentRoute;
    }

    /**
     * Set the current route
     * @param currentRoute Current route
     */
    public void setCurrentRoute(Route currentRoute) {
        this.currentRoute = currentRoute;
    }

    /**
     * Get the string representation of the courier
     * @return String representation of the courier
     */
    @Override
    public String toString() {
        return "Courier{" +
                "id=" + id +
                ", availability=" + availability +
                ", currentRoute=" + currentRoute +
                '}';
    }
}