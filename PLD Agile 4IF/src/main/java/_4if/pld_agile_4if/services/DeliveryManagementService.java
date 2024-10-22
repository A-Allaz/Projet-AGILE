package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryManagementService {

    private List<Delivery> deliveries = new ArrayList<>();
    private final TourCalculatorService tourCalculatorService;
    private CityMap cityMap; // The current city map
    private long warehouseId; // The warehouse ID

    // Constructor that injects the TourCalculatorService and initializes with the city map and warehouse ID
    public DeliveryManagementService(TourCalculatorService tourCalculatorService) {
        this.tourCalculatorService = tourCalculatorService;
    }

    // Initialize the city map and warehouse ID when loading a new city map
    public void initializeCityMap(CityMap cityMap, long warehouseId) {
        this.cityMap = cityMap;
        this.warehouseId = warehouseId;
    }

    // Add a new delivery
    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
        recalculateTour();
    }

    // Remove a delivery by its ID
    public void removeDelivery(long deliveryId) {
        deliveries.removeIf(delivery -> delivery.getId() == deliveryId);
        recalculateTour();
    }

    // Modify an existing delivery by its ID
    public void modifyDelivery(long deliveryId, Delivery updatedDelivery) {
        for (Delivery delivery : deliveries) {
            if (delivery.getId() == deliveryId) {
                delivery.setPickupLocation(updatedDelivery.getPickupLocation());
                delivery.setDeliveryLocation(updatedDelivery.getDeliveryLocation());
                delivery.setPickupTime(updatedDelivery.getPickupTime());
                delivery.setDeliveryTime(updatedDelivery.getDeliveryTime());
                recalculateTour();
                break;
            }
        }
    }

    // Recalculate the optimal tour based on the updated delivery list
    private void recalculateTour() {
        if (cityMap != null && !deliveries.isEmpty()) {
            List<RoadSegment> optimalTour = tourCalculatorService.calculateOptimalTour(cityMap, deliveries, warehouseId);
            // Here you can handle the result, for example, updating the UI or storing the tour
            System.out.println("Optimal tour recalculated: " + optimalTour);
        }
    }

    // Get the current list of deliveries
    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }

    // Get the city map (if needed for external use)
    public CityMap getCityMap() {
        return cityMap;
    }

    // Set the city map manually if needed
    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }
}
