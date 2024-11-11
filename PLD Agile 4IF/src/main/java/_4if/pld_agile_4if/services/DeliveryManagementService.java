package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryManagementService {
    private List<Delivery> deliveries = new ArrayList<>();
    private final List<Courier> couriers = new ArrayList<>();
    private final Map<Integer, List<RoadSegment>> courierRoutes = new HashMap<>(); // Associe chaque livreur à son trajet optimal
    private final TourCalculatorService tourCalculatorService;
    private CityMap cityMap; // The current city map
    private long warehouseId; // The warehouse ID


    // Constructor that injects the TourCalculatorService and initializes with the city map and warehouse ID
    public DeliveryManagementService(TourCalculatorService tourCalculatorService) {
        this.tourCalculatorService = tourCalculatorService;
    }

    // Initialize couriers
    public void initializeCouriers(int numberOfCouriers) {
        for (int i = 1; i <= numberOfCouriers; i++) {
            couriers.add(new Courier(i, true, new ArrayList<>(), null)); // Disponibles par défaut
        }
    }

    // Assign delivery to a courier
    public boolean assignDeliveryToCourier(int courierId, long deliveryId) {
        Courier courier = getCourierById(courierId);
        Delivery delivery = findDeliveryById(deliveryId);

        if (courier != null && delivery != null) {
            courier.addDelivery(delivery);
            delivery.setCourier(courier);
            calculateCourierRoute(courier);
            return true;
        }
        System.out.println("Courier " + courier.toString());
        System.out.println("Delivery " + delivery.toString());
        return false;
    }

    // Find a delivery by ID
    private Delivery findDeliveryById(long deliveryId) {
        return deliveries.stream().filter(d -> d.getId() == deliveryId).findFirst().orElse(null);
    }

    // Calculate the optimal route for a specific courier based on their deliveries
    private void calculateCourierRoute(Courier courier) {
        if (cityMap != null && !courier.getAssignedDeliveries().isEmpty()) {
            Map<String, Object> result = tourCalculatorService.calculateOptimalTourWithEstimates(
                    cityMap, courier.getAssignedDeliveries(), warehouseId);
            List<RoadSegment> optimalTour = (List<RoadSegment>) result.get("optimalTour");
            courier.setCurrentRoute(new Route(optimalTour)); // Met à jour le trajet actuel du livreur
            courierRoutes.put(courier.getId(), optimalTour);
        }
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
        // recalculateTour();
    }

    // Collect and stock the deliveries assigned to a courier
    public void loadDeliveries(Courier selectedCourier){
        deliveries = selectedCourier.getAssignedDeliveries();
    }

    // Add a new delivery
    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    // Remove a delivery by its ID and reassigns affected couriers
    public boolean removeDelivery(long deliveryId) {
        for (Courier courier : couriers) {
            boolean removed = courier.getAssignedDeliveries().removeIf(d -> d.getId() == deliveryId);
            if (removed) {
                deliveries.removeIf(d -> d.getId() == deliveryId);
                calculateCourierRoute(courier); // Recalculer le trajet du livreur
                return true;
            }
        }

        boolean removed = deliveries.removeIf(d -> d.getId() == deliveryId);
        return removed;
    }

    // Modify an existing delivery by its ID
    // Modify an existing delivery and recalculate routes for assigned courier
    public void modifyDelivery(long deliveryId, Delivery updatedDelivery) {
        for (Courier courier : couriers) {
            for (Delivery delivery : courier.getAssignedDeliveries()) {
                if (delivery.getId() == deliveryId) {
                    delivery.setPickupLocation(updatedDelivery.getPickupLocation());
                    delivery.setDeliveryLocation(updatedDelivery.getDeliveryLocation());
                    delivery.setPickupTime(updatedDelivery.getPickupTime());
                    delivery.setDeliveryTime(updatedDelivery.getDeliveryTime());
                    calculateCourierRoute(courier); // Recalculer le trajet du livreur
                    break;
                }
            }
        }

        for (Delivery delivery : deliveries) {
            if (delivery.getId() == deliveryId) {
                delivery.setPickupLocation(updatedDelivery.getPickupLocation());
                delivery.setDeliveryLocation(updatedDelivery.getDeliveryLocation());
                delivery.setPickupTime(updatedDelivery.getPickupTime());
                delivery.setDeliveryTime(updatedDelivery.getDeliveryTime());
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

    // Get all couriers
    public List<Courier> getAllCouriers() {
        return couriers;
    }

    // Get the route for a specific courier
    public List<RoadSegment> getCourierRoute(int courierId) {
        return courierRoutes.getOrDefault(courierId, new ArrayList<>());
    }

    public Courier getCourierById(int courierId) {
        return couriers.stream().filter(c -> c.getId() == courierId).findFirst().orElse(null);
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
