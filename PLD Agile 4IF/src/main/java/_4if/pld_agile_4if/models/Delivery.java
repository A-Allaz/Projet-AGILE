package _4if.pld_agile_4if.models;

import java.sql.Time;

public class Delivery {

    private Location pickupLocation;
    private Location deliveryLocation;
    private Time pickupTime;
    private Time deliveryTime;
    private Courier courier;
    private Route route;

    // Constructor
    public Delivery(Location pickupLocation, Location deliveryLocation, Time pickupTime, Time deliveryTime, Courier courier, Route route) {
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
        this.courier = courier;
        this.route = route;
    }

    // Getters and Setters
    public Location getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Location pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(Location deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public Time getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Time pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Time getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Time deliveryTime) {
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