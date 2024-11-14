package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "troncon")
public class RoadSegment {
    /**
     * RoadSegment class
     */

    private long origin;
    private long destination;
    private String name;
    private double length;

    // Constructor
    /**
     * Default constructor
     */
    public RoadSegment() {}

    /**
     * Constructor
     * @param origin Origin
     * @param destination Destination
     * @param name Name
     * @param length Length
     */
    public RoadSegment(long origin, long destination, String name, double length) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.length = length;
    }

    // Getters and Setters
    /**
     * Get the origin
     * @return Origin
     */
    @XmlAttribute(name = "origine")
    public long getOrigin() {
        return origin;
    }

    /**
     * Set the origin
     * @param origin Origin
     */
    public void setOrigin(long origin) {
        this.origin = origin;
    }

    /**
     * Get the destination
     * @return Destination
     */
    @XmlAttribute(name = "destination")
    public long getDestination() {
        return destination;
    }

    /**
     * Set the destination
     * @param destination Destination
     */
    public void setDestination(long destination) {
        this.destination = destination;
    }

    /**
     * Get the name
     * @return Name
     */
    @XmlAttribute(name = "nomRue")
    public String getName() {
        return name;
    }

    /**
     * Set the name
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the length
     * @return Length
     */
    @XmlAttribute(name = "longueur")
    public double getLength() {
        return length;
    }

    /**
     * Set the length
     * @param length Length
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Get the string representation of the road segment
     * @return String representation of the road segment
     */
    @Override
    public String toString() {
        return "RoadSegment{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}