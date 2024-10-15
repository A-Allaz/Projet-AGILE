package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "troncon")
public class RoadSegment {

    private int origin;
    private int destination;
    private String name;
    private double length;

    // Constructor
    public RoadSegment() {}

    public RoadSegment(int origin, int destination, String name, double length) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.length = length;
    }

    // Getters and Setters
    @XmlAttribute(name = "origine")
    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    @XmlAttribute(name = "destination")
    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    @XmlAttribute(name = "nomRue")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "longueur")
    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

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