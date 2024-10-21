package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;

import java.time.LocalTime;

public class Warehouse {

    // Attributes
    private long address; // Correspond à l'id de l'intersection
    private LocalTime departureTime;


    // Constructeur
    public Warehouse(long address, LocalTime departureTime) {
        this.address = address;
        this.departureTime = departureTime;
    }

    // Default constructor (optional, for use by frameworks like JAXB)
    public Warehouse() {
    }

    // Getters
    public long getAddress() {
        return address;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    // Setters
    public void setAddress(long address) {
        this.address = address;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    // toString method (optional, for logging or debugging)
    @Override
    public String toString() {
        return "Warehouse{" +
                "adress=" + address +
                ", departureTime=" + departureTime +
                '}';
    }
}
