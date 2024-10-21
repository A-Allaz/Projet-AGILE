package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Time;

@XmlRootElement(name = "demandeDeLivraisons")
public class Delivery {

    private long pickupLocation;
    private long deliveryLocation;
    private int pickupTime;
    private int deliveryTime;
    private Courier courier;
    private Route route;

    // Constructor
    public Delivery() {}

    public Delivery(long pickupLocation, long deliveryLocation, int pickupTime, int deliveryTime, Courier courier, Route route) {
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
        this.courier = courier;
        this.route = route;
    }

    // Getters and Setters
    @XmlAttribute(name = "adresseEnlevement")
    public long getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(long pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    @XmlAttribute(name = "adresseLivraison")
    public long getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(long deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    @XmlAttribute(name = "dureeEnlevement")
    public int getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(int pickupTime) {
        this.pickupTime = pickupTime;
    }

    @XmlAttribute(name = "dureeLivraison")
    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "pickupLocation=" + pickupLocation +
                ", deliveryLocation=" + deliveryLocation +
                ", pickupTime=" + pickupTime +
                ", deliveryTime=" + deliveryTime +
                ", courier=" + courier +
                ", route=" + route +
                '}';
    }
}