package Model;

import java.sql.Time;

public class Delivery {
    private Location pickupLocation;
    private Location deliveryLocation;
    private Time pickupTime;
    private Time deliveryTime;
    private Courier courier;

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation;
    }

    public Time getPickupTime() {
        return pickupTime;
    }

    public Time getDeliveryTime() {
        return deliveryTime;
    }

    public Courier getCourier() {
        return courier;
    }
}

