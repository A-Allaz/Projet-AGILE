package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryManagementService {
    private List<Delivery> deliveries;
    private List<RoadSegment> optimalTour;
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

    // Stock a list of deliveries from a XML file
    public void addDeliveryProgram(List<Delivery> deliveryProgram){
        if (deliveries.isEmpty())
        {
            deliveries = deliveryProgram;
        }
        else {
            deliveries.addAll(deliveryProgram);
        }
        recalculateTour();
    }

    // Collect and stock the deliveries assigned to a courier
    public void loadDeliveries(Courier selectedCourier){
        deliveries = selectedCourier.getAssignedDeliveries();
    }

   public void assignCourier(Courier courier, Delivery delivery){
        courier.addDelivery(delivery);
        loadDeliveries(courier);
        recalculateTour();
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

    // Recalcule le tour optimal et les estimations de temps en fonction de la liste de livraisons mise à jour
    private void recalculateTour() {
        if (cityMap != null && !deliveries.isEmpty()) {
            // Appel de la méthode `calculateOptimalTourWithEstimates` pour obtenir le tour optimal et les estimations de temps
            Map<String, Object> result = tourCalculatorService.calculateOptimalTourWithEstimates(cityMap, deliveries, warehouseId);
            List<RoadSegment> optimalTour = (List<RoadSegment>) result.get("optimalTour");
            List<Map<String, Object>> timeEstimates = (List<Map<String, Object>>) result.get("timeEstimates");

            // Affichage ou manipulation des résultats pour l'interface ou le stockage
            System.out.println("Optimal tour recalculated: " + optimalTour);
            System.out.println("Time estimates: " + timeEstimates);
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
