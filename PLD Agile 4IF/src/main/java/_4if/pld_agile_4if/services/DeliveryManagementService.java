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
    private final Map<Integer, Map<List<RoadSegment>, List<Map<String, Object>>>> courierRoutesAndTimeEstimates = new HashMap<>(); // Associe chaque livreur à son trajet optimal
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
    public boolean assignDeliveryToCourier(int courierId, int deliveryId) {
        Courier courier = getCourierById(courierId);
        Delivery delivery = findDeliveryById(deliveryId);

        if (courier != null && delivery != null) {
            courier.addDelivery(delivery);
            delivery.setCourier(courier);

            // Mettre à jour la liste globale des livraisons
            updateDeliveryInList(delivery);

            // Mettre à jour la liste globale des livreurs
            updateCourierInList(courier);

            calculateCourierRoute(courier);
            return true;
        }
        System.out.println("Courier " + courier.toString());
        System.out.println(deliveries);
        System.out.println("Delivery " + delivery.toString());
        return false;
    }

    // Méthode pour mettre à jour une livraison dans la liste globale des livraisons
    private void updateDeliveryInList(Delivery updatedDelivery) {
        for (int i = 0; i < deliveries.size(); i++) {
            if (deliveries.get(i).getId() == updatedDelivery.getId()) {
                deliveries.set(i, updatedDelivery);
                return;
            }
        }
        // Ajouter si la livraison n'est pas trouvée
        deliveries.add(updatedDelivery);
    }

    // Méthode pour mettre à jour un livreur dans la liste globale des livreurs
    private void updateCourierInList(Courier updatedCourier) {
        for (int i = 0; i < couriers.size(); i++) {
            if (couriers.get(i).getId() == updatedCourier.getId()) {
                couriers.set(i, updatedCourier);
                return;
            }
        }
        // Ajouter si le livreur n'est pas trouvé
        couriers.add(updatedCourier);
    }

    // Find a delivery by ID
    private Delivery findDeliveryById(int deliveryId) {
        return deliveries.stream().filter(d -> d.getId() == deliveryId).findFirst().orElse(null);
    }

    // Calculate the optimal route for a specific courier based on their deliveries
    public void calculateCourierRoute(Courier courier) {
        if (cityMap != null && !courier.getAssignedDeliveries().isEmpty()) {
            Map<String, Object> result = tourCalculatorService.calculateOptimalTourWithEstimates(
                    cityMap, courier.getAssignedDeliveries(), warehouseId);
            List<RoadSegment> optimalTour = (List<RoadSegment>) result.get("optimalTour");
            List<Map<String, Object>> timeEstimates = (List<Map<String, Object>>) result.get("timeEstimates");
            courier.setCurrentRoute(new Route(optimalTour)); // Met à jour le trajet actuel du livreur
            courierRoutesAndTimeEstimates.put(courier.getId(), Map.of(optimalTour, timeEstimates));
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

    // Get all couriers
    public List<Courier> getAllCouriers() {
        return couriers;
    }

    // Get the route for a specific courier
    public List<RoadSegment> getCourierRoute(int courierId) {
        Map<List<RoadSegment>, List<Map<String, Object>>> routeAndEstimates = courierRoutesAndTimeEstimates.get(courierId);
        if (routeAndEstimates != null && !routeAndEstimates.isEmpty()) {
            return routeAndEstimates.keySet().iterator().next();
        }
        return new ArrayList<>();
    }

    public Courier getCourierById(int courierId) {
        return couriers.stream().filter(c -> c.getId() == courierId).findFirst().orElse(null);
    }

    // Get the current list of deliveries
    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }


    // Get the delivery of a specific courier
    public List<Delivery> getCourierDeliveries(int courierId) {
        return getCourierById(courierId).getAssignedDeliveries();
    }

    public List<Map<String, Object>> getCourierRouteTimeEstimates(int courierId) {
        Map<List<RoadSegment>, List<Map<String, Object>>> routeAndEstimates = courierRoutesAndTimeEstimates.get(courierId);
        if (routeAndEstimates != null && !routeAndEstimates.isEmpty()) {
            return routeAndEstimates.values().iterator().next();
        }
        return new ArrayList<>();
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
