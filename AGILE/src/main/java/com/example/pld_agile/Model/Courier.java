import java.util.List;

public class Courier {
    private int id;
    private boolean availability;
    private List<Delivery> assignedDeliveries;
    private Route currentRoute;

    public int getId() {
        return id;

    }

    public boolean getAvailability() {
        return availability;
    }

    public void assignDelivery(Delivery delivery) {
    }

    public Route computeOptimalRoute(List<Delivery> deliveries) {
        // Compute the optimal route for the deliveries
        return null;
    }
}

