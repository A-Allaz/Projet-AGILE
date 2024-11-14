package _4if.pld_agile_4if.models;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Time;
import java.util.concurrent.atomic.AtomicLong;

@XmlRootElement(name = "demandeDeLivraisons")
public class Delivery {
    /**
     * Delivery class
     */

    private static final AtomicLong idGenerator = new AtomicLong(0);
    private int id;  // Nouvel attribut ID généré automatiquement
    private long pickupLocation;
    private long deliveryLocation;
    private int pickupTime;
    private int deliveryTime;
    private Courier courier;
    private Route route;

    // Constructor
    /**
     * Default constructor
     */
    public Delivery() {
        this.id = (int) idGenerator.incrementAndGet();  // Générer un ID unique
    }

    /**
     * Constructor
     * @param pickupLocation Pickup location
     * @param deliveryLocation Delivery location
     * @param pickupTime Pickup time
     * @param deliveryTime Delivery time
     */
    public Delivery(long pickupLocation, long deliveryLocation, int pickupTime, int deliveryTime) {
        this.id = (int) idGenerator.incrementAndGet();  // Générer un ID unique
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
    }

    /**
     * Constructor
     * @param pickupLocation Pickup location
     * @param deliveryLocation Delivery location
     * @param pickupTime Pickup time
     * @param deliveryTime Delivery time
     * @param courier Courier
     * @param route Route
     */
    public Delivery(long pickupLocation, long deliveryLocation, int pickupTime, int deliveryTime, Courier courier, Route route) {
        this.id = (int) idGenerator.incrementAndGet();  // Générer un ID unique
        this.pickupLocation = pickupLocation;
        this.deliveryLocation = deliveryLocation;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
        this.courier = courier;
        this.route = route;
    }

    // Getters and Setters
    /**
     * Get the delivery ID
     * @return Delivery ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the delivery ID
     * @param id Delivery ID
     */
    @XmlAttribute(name = "adresseEnlevement")
    public long getPickupLocation() {
        return pickupLocation;
    }

    /**
     * Set the pickup location
     * @param pickupLocation Pickup location
     */
    public void setPickupLocation(long pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    /**
     * Get the delivery location
     * @return Delivery location
     */
    @XmlAttribute(name = "adresseLivraison")
    public long getDeliveryLocation() {
        return deliveryLocation;
    }

    /**
     * Set the delivery location
     * @param deliveryLocation Delivery location
     */
    public void setDeliveryLocation(long deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    /**
     * Get the pickup time
     * @return Pickup time
     */
    @XmlAttribute(name = "dureeEnlevement")
    public int getPickupTime() {
        return pickupTime;
    }

    /**
     * Set the pickup time
     * @param pickupTime Pickup time
     */
    public void setPickupTime(int pickupTime) {
        this.pickupTime = pickupTime;
    }

    /**
     * Get the delivery time
     * @return Delivery time
     */
    @XmlAttribute(name = "dureeLivraison")
    public int getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Set the delivery time
     * @param deliveryTime Delivery time
     */
    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * Get the courier
     * @return Courier
     */
    public Courier getCourier() {
        return courier;
    }

    /**
     * Set the courier
     * @param courier Courier
     */
    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    /**
     * Get the route
     * @return Route
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Set the route
     * @param route Route
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Reset the ID generator
     */
    public static void resetIdGenerator() {
        idGenerator.set(0);
    }

    /**
     * Get the String representation of the delivery
     * @return String representation of the delivery
     */
    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", pickupLocation=" + pickupLocation +
                ", deliveryLocation=" + deliveryLocation +
                ", pickupTime=" + pickupTime +
                ", deliveryTime=" + deliveryTime +
                ", courier=" + courier +
                ", route=" + route +
                '}';
    }
}