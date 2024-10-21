package _4if.pld_agile_4if.models;

public class Warehouse {

    // Attributes
    private long id;
    private double latitude;
    private double longitude;

    // Constructor with parameters
    public Warehouse(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Default constructor (optional, for use by frameworks like JAXB)
    public Warehouse() {
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // toString method (optional, for logging or debugging)
    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
