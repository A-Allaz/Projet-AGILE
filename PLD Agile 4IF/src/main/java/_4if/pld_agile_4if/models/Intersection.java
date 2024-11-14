package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "noeud")
public class Intersection {
    /**
     * Intersection class
     */

    private long id;
    private double latitude;
    private double longitude;

    // Constructor
    /**
     * Default constructor
     */
    public Intersection() {}

    /**
     * Constructor
     * @param id Intersection ID
     * @param latitude Latitude
     * @param longitude Longitude
     */
    public Intersection(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Get the intersection ID
     * @return Intersection ID
     */
    // Getters and Setters
    @XmlAttribute(name = "id")
    public long getId() {
        return id;
    }

    /**
     * Set the intersection ID
     * @param id Intersection ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the latitude
     * @return Latitude
     */
    @XmlAttribute(name = "latitude")
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude
     * @param latitude Latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the longitude
     * @return Longitude
     */
    @XmlAttribute(name = "longitude")
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude
     * @param longitude Longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the intersection String representation
     * @return Intersection String representation
     */
    @Override
    public String toString() {
        return "Intersection{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
