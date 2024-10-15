package Model;

import javax.xml.bind.annotation.XmlAttribute;

public class RoadSegment {
    private long destination;
    private double length;
    private String streetName;
    private long origin;

    // Getters and setters
    public long getDestination() {
        return destination;
    }

    @XmlAttribute
    public void setDestination(long destination) {
        this.destination = destination;
    }

    public double getLength() {
        return length;
    }

    @XmlAttribute(name = "longueur")
    public void setLength(double length) {
        this.length = length;
    }

    public String getStreetName() {
        return streetName;
    }

    @XmlAttribute(name = "nomRue")
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public long getOrigin() {
        return origin;
    }

    @XmlAttribute(name = "origine")
    public void setOrigin(long origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "RoadSegment{" +
                "destination=" + destination +
                ", length=" + length +
                ", streetName='" + streetName + '\'' +
                ", origin=" + origin +
                '}';
    }
}