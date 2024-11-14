package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryManagementService {
    /**
     * Delivery management service
     */

    private List<Delivery> deliveries = new ArrayList<>();
    private final List<Courier> couriers = new ArrayList<>();
    private final Map<Integer, Map<List<RoadSegment>, List<Map<String, Object>>>> courierRoutesAndTimeEstimates = new HashMap<>(); // Associe chaque livreur à son trajet optimal
    private final TourCalculatorService tourCalculatorService;
    private CityMap cityMap; // The current city map
    private long warehouseId; // The warehouse ID


    /**
     * Constructor
     * @param tourCalculatorService Tour calculator service
     */
    public DeliveryManagementService(TourCalculatorService tourCalculatorService) {
        this.tourCalculatorService = tourCalculatorService;
    }

    /**
     * Initialize couriers
     * @param numberOfCouriers Number of couriers
     */
    public void initializeCouriers(int numberOfCouriers) {
        for (int i = 1; i <= numberOfCouriers; i++) {
            couriers.add(new Courier(i, true, null)); // Disponibles par défaut
        }
    }

    // Assign delivery to a courier
    /**
     * Assign a delivery to a courier
     * @param courierId Courier ID
     * @param deliveryId Delivery ID
     * @return True if the delivery was successfully assigned to the courier, false otherwise
     */
    public boolean assignDeliveryToCourier(int courierId, int deliveryId) {
        Courier courier = getCourierById(courierId);
        Delivery delivery = findDeliveryById(deliveryId);
        int formerCourierId = delivery.getCourier() != null ? delivery.getCourier().getId() : 0;
        System.out.println("Assigning delivery " + deliveryId + " to courier " + courierId);
        System.out.println("Delivery: " + delivery);

        if (courier != null && delivery != null) {
            delivery.setCourier(courier);

            System.out.println("Delivery: " + delivery);

            // Mettre à jour la liste globale des livraisons
            updateDeliveryInList(delivery);

            // Mettre à jour la liste globale des livreurs
            updateCourierInList(courier);

            // Si la delivery avait déjà un livreur, recalculer le trajet du livreur qui a perdu la livraison
            if(formerCourierId != 0) {
                Courier formerCourier = getCourierById(formerCourierId);
                calculateCourierRoute(formerCourier);
            }

            // Recalculer le trajet du livreur
            calculateCourierRoute(courier);
            return true;
        }
        return false;
    }

    // Méthode pour mettre à jour une livraison dans la liste globale des livraisons
    /**
     * Update a delivery in the global list of deliveries
     * @param updatedDelivery Updated delivery
     */
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
    /**
     * Update a courier in the global list of couriers
     * @param updatedCourier Updated courier
     */
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
    /**
     * Find a delivery by its ID
     * @param deliveryId Delivery ID
     * @return Delivery
     */
    private Delivery findDeliveryById(int deliveryId) {
        return deliveries.stream().filter(d -> d.getId() == deliveryId).findFirst().orElse(null);
    }

    /**
     * Get the warehouse ID
     * @return Warehouse ID
     */
    public List<Delivery> getDeliveriesCourier(Courier courier) {
        List<Delivery> courierDeliveries = new ArrayList<>();
        for(Delivery delivery : deliveries) {
            if(delivery.getCourier() == courier) {
                courierDeliveries.add(delivery);
            }
        }
        return courierDeliveries;
    }

    // Calculate the optimal route for a specific courier based on their deliveries
    /**
     * Calculate the optimal route for a specific courier based on their deliveries
     * @param courier Courier
     */
    public void calculateCourierRoute(Courier courier) {
        List<Delivery> courierDeliveries = getDeliveriesCourier(courier);
        System.out.println("Calculating route for courier " + courier.getId() + " with " + courierDeliveries.size() + " deliveries");
        if (cityMap != null && !courierDeliveries.isEmpty()) {
            Map<String, Object> result = tourCalculatorService.calculateOptimalTourWithEstimates(
                    cityMap, courierDeliveries, cityMap.getWarehouse().getAddress());
            List<RoadSegment> optimalTour = (List<RoadSegment>) result.get("optimalTour");
            List<Map<String, Object>> timeEstimates = (List<Map<String, Object>>) result.get("timeEstimates");
            courier.setCurrentRoute(new Route(optimalTour)); // Met à jour le trajet actuel du livreur
            courierRoutesAndTimeEstimates.put(courier.getId(), Map.of(optimalTour, timeEstimates));
        }
    }

    // Initialize the city map and warehouse ID when loading a new city map
    /**
     * Initialize the city map and warehouse ID when loading a new city map
     * @param cityMap City map
     * @param warehouseId Warehouse ID
     */
    public void initializeCityMap(CityMap cityMap, long warehouseId) {
        this.cityMap = cityMap;
        this.warehouseId = warehouseId;
    }

    // Stock a list of deliveries from a XML file
    /**
     * Stock a list of deliveries from an XML file
     * @param deliveryProgram List of deliveries
     */
    public void addDeliveryProgram(List<Delivery> deliveryProgram){
        if (deliveries.isEmpty())
        {
            deliveries = deliveryProgram;
        }
        else {
            deliveries.addAll(deliveryProgram);
        }
    }

    // Add a new delivery
    /**
     * Add a new delivery
     * @param delivery Delivery
     */
    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    // Remove a delivery by its ID and reassigns affected couriers
    /**
     * Remove a delivery by its ID and reassigns affected couriers
     * @param deliveryId Delivery ID
     * @return True if the delivery was successfully removed, false otherwise
     */
    public boolean removeDelivery(long deliveryId) {;
        for (Courier courier : couriers) {
            boolean removed = getDeliveriesCourier(courier).removeIf(d -> d.getId() == deliveryId);
            if (removed) {
                deliveries.removeIf(d -> d.getId() == deliveryId);
                calculateCourierRoute(courier); // Recalculer le trajet du livreur
                return true;
            }
        }

        boolean removed = deliveries.removeIf(d -> d.getId() == deliveryId);
        return removed;
    }


    // Modify an existing delivery and recalculate routes for assigned courier
    /**
     * Modify an existing delivery and recalculate routes for assigned courier
     * @param deliveryId Delivery ID
     * @param updatedDelivery Updated delivery
     */
    public void modifyDelivery(long deliveryId, Delivery updatedDelivery) {
        Delivery deliveryToUpdate = null;
        Courier assignedCourier = null;

        for (Courier courier : couriers) {
            for (Delivery delivery : getDeliveriesCourier(courier)) {
                if (delivery.getId() == deliveryId) {
                    deliveryToUpdate = delivery;
                    assignedCourier = courier;
                    break;
                }
            }
            if (deliveryToUpdate != null) {
                break;
            }
        }
        if (deliveryToUpdate != null) {
            deliveryToUpdate.setPickupLocation(updatedDelivery.getPickupLocation());
            deliveryToUpdate.setDeliveryLocation(updatedDelivery.getDeliveryLocation());
            deliveryToUpdate.setPickupTime(updatedDelivery.getPickupTime());
            deliveryToUpdate.setDeliveryTime(updatedDelivery.getDeliveryTime());
            calculateCourierRoute(assignedCourier); // Recalculer le trajet du livreur
        }
    }

    // Get all couriers
    /**
     * Get all couriers
     * @return List of couriers
     */
    public List<Courier> getAllCouriers() {
        return couriers;
    }

    // Get the route for a specific courier
    /**
     * Get the route for a specific courier
     * @param courierId Courier ID
     * @return List of road segments
     */
    public List<RoadSegment> getCourierRoute(int courierId) {
        Map<List<RoadSegment>, List<Map<String, Object>>> routeAndEstimates = courierRoutesAndTimeEstimates.get(courierId);
        if (routeAndEstimates != null && !routeAndEstimates.isEmpty()) {
            return routeAndEstimates.keySet().iterator().next();
        }
        return new ArrayList<>();
    }

    /**
     * Get the courier by its ID
     * @param courierId Courier ID
     * @return Courier
     */
    public Courier getCourierById(int courierId) {
        return couriers.stream().filter(c -> c.getId() == courierId).findFirst().orElse(null);
    }

    /**
     * Get all deliveries
     * @return List of deliveries
     */
    // Get the current list of deliveries
    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }

    /**
     * Get the time estimates for a specific courier
     * @param courierId Courier ID
     * @return List of time estimates
     */
    public List<Map<String, Object>> getCourierRouteTimeEstimates(int courierId) {
        Map<List<RoadSegment>, List<Map<String, Object>>> routeAndEstimates = courierRoutesAndTimeEstimates.get(courierId);
        if (routeAndEstimates != null && !routeAndEstimates.isEmpty()) {
            return routeAndEstimates.values().iterator().next();
        }
        return new ArrayList<>();
    }

    // Get the city map (if needed for external use)
    /**
     * Get the city map
     * @return City map
     */
    public CityMap getCityMap() {
        return cityMap;
    }

    // Set the city map manually if needed
    /**
     * Set the city map
     * @param cityMap City map
     */
    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    /**
     * Reset the data
     */
    public void resetData() {
        deliveries.clear();  // Vider la liste des livraisons
        couriers.clear();  // Vider la liste des livreurs
        courierRoutesAndTimeEstimates.clear();  // Vider les estimations et trajets par livreur
        cityMap = null;  // Réinitialiser la carte
        warehouseId = 0;  // Réinitialiser l'ID de l'entrepôt
        Delivery.resetIdGenerator(); // Réinitialiser l'ID des livraisons

        System.out.println("Data reset");
        System.out.println("Deliveries: " + deliveries);
        System.out.println("Couriers: " + couriers);
        System.out.println("Courier routes and time estimates: " + courierRoutesAndTimeEstimates);
        System.out.println("City map: " + cityMap);
        System.out.println("Warehouse ID: " + warehouseId);
    }

}
