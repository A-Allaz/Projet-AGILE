package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;

import java.time.LocalTime;

public class Warehouse {
    /**
     * Warehouse class
     */

    // Attributes
    private long address; // Correspond à l'id de l'intersection
    private LocalTime departureTime;


    // Constructeur
    /**
     * Constructor
     * @param address Address
     * @param departureTime Departure time
     */
    public Warehouse(long address, LocalTime departureTime) {
        this.address = address;
        this.departureTime = departureTime;
    }

    // Default constructor
    /**
     * Default constructor
     */
    public Warehouse() {
    }

    // Getters
    /**
     * Get the address
     * @return Address
     */
    public long getAddress() {
        return address;
    }

    /**
     * Get the departure time
     * @return Departure time
     */
    public LocalTime getDepartureTime() {
        return departureTime;
    }

    // Setters
    /**
     * Set the address
     * @param address Address
     */
    public void setAddress(long address) {
        this.address = address;
    }

    /**
     * Set the departure time
     * @param departureTime Departure time
     */
    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    // toString method
    /**
     * Convert the object to a string
     * @return String representation of the object
     */
    @Override
    public String toString() {
        return "Warehouse{" +
                "adress=" + address +
                ", departureTime=" + departureTime +
                '}';
    }
}
