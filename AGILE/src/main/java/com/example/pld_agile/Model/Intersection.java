package Model;

import javax.xml.bind.annotation.XmlAttribute;

public class Intersection {
    private long id;
    private double latitude;
    private double longitude;

    // Getters and setters
    public long getId() {
        return id;
    }

    @XmlAttribute
    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    @XmlAttribute
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @XmlAttribute
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}