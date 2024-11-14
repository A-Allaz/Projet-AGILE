package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.Intersection;
import _4if.pld_agile_4if.models.RoadSegment;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

/**
 * @desc Service class to calculate the optimal tour with time estimates
 */

@Service
public class TourCalculatorService {

    private int currentCapacity;
    private static final double COURIER_SPEED_KMH = 15.0;  // Vitesse en km/h

    // Méthode principale pour calculer le meilleur tour
    /**
     * Calculate the optimal tour with time estimates
     * @param cityMap City map
     * @param deliveries List of deliveries
     * @param warehouseId Warehouse ID
     * @return Map containing the optimal tour and time estimates
     */
    public  Map<String, Object> calculateOptimalTourWithEstimates(CityMap cityMap, List<Delivery> deliveries, long warehouseId) {
        // Étape 1: Identifier tous les points d'intérêt (entrepôt, points de pickup et livraison)
        Set<Long> pointsOfInterest = new HashSet<>();
        pointsOfInterest.add(cityMap.getWarehouse().getAddress()); // Ajout de l'entrepôt

        // Ajout des points pickup et delivery
        for (Delivery delivery : deliveries) {
            pointsOfInterest.add(delivery.getPickupLocation());
            pointsOfInterest.add(delivery.getDeliveryLocation());
        }

        // Étape 2: Calculer les plus courts chemins entre chaque paire de points d'intérêt
        Map<Long, Map<Long, Double>> shortestPaths = computeShortestPaths(cityMap, pointsOfInterest);

        // Étape 3: Construire un sous-graphe complet entre les points d'intérêt
        Map<Long, Map<Long, Double>> completeSubGraph = buildCompleteSubGraph(shortestPaths, pointsOfInterest);

        // Étape 4: Résoudre le problème en suivant la contrainte de visite des pickups avant les livraisons
        List<Long> optimalTour = findBestTour(completeSubGraph, deliveries, warehouseId);

        // Étape 5: Retrouver le chemin complet entre chaque points du programme de livraison
        List<RoadSegment> completeDeliveryPath = getCompletePath(cityMap, optimalTour);

        // Étape 6: Calculer les estimations de temps pour chaque arrêt
        List<Map<String, Object>> timeEstimates = calculateTimeEstimates(completeDeliveryPath, deliveries, cityMap.getWarehouse().getDepartureTime());

        // Etape 7: Vérifier que le temps total ne dépasse pas 7 heures
        checkTotalTime(timeEstimates, deliveries);

        // Retourner les deux résultats dans une Map
        Map<String, Object> result = new HashMap<>();
        result.put("optimalTour", completeDeliveryPath); // Chemin complet des segments de route
        result.put("timeEstimates", timeEstimates);      // Estimations de temps pour chaque arrêt

        return result;

    }

    // Méthode pour calculer les plus courts chemins à partir de chaque point d'intérêt
    /**
     * Compute the shortest paths from each point of interest
     * @param cityMap City map
     * @param pointsOfInterest Set of points of interest
     * @return Map containing the shortest paths from each point of interest
     */
    private Map<Long, Map<Long,Double>> computeShortestPaths(CityMap cityMap, Set<Long> pointsOfInterest) {
        Map<Long, Map<Long,Double>> shortestPaths = new HashMap<>();

        for(Long point : pointsOfInterest) {
            shortestPaths.put(point, dijkstra(cityMap, point));
        }

        return shortestPaths;
    }

    // Algorithme de Dijkstra pour calculer les plus courts chemins à partir d'une intersection source
    /**
     * Dijkstra's algorithm to compute the shortest paths from a source intersection
     * @param cityMap City map
     * @param source Source intersection
     * @return Map containing the shortest paths from the source intersection
     */
    public Map<Long, Double> dijkstra(CityMap cityMap, long source) {
        Map<Long, Double> distances = new HashMap<>();

        PriorityQueue<IntersectionDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(IntersectionDistance::getDistance));

        // Initialisation : on met toutes les distances à l'infini sauf la source
        for(Intersection intersection : cityMap.getIntersections()) {
            distances.put(intersection.getId(), Double.MAX_VALUE);
        }
        distances.put(source, 0.0);
        pq.add(new IntersectionDistance(source, 0));

        //Dijkstra
        while(!pq.isEmpty()) {
            IntersectionDistance current = pq.poll();
            long currentIntersectionId = current.getIntersectionId();

            for(RoadSegment segment : cityMap.getOutGoingRoadSegments(currentIntersectionId)) {
                long neighbour = segment.getDestination();
                double newDistance = distances.get(currentIntersectionId) + segment.getLength();
                if(newDistance < distances.get(neighbour)) {
                    distances.put(neighbour, newDistance);
                    pq.add(new IntersectionDistance(neighbour, newDistance));
                }
            }
        }
        return distances;
    }

    // Méthode pour construire un sous-graphe complet entre les points d'intérêt
    /**
     * Build a complete subgraph between the points of interest
     * @param shortestPaths Map containing the shortest paths between each pair of points of interest
     * @param pointsOfInterest Set of points of interest
     * @return Map containing the complete subgraph between the points of interest
     */
    private Map<Long, Map<Long, Double>> buildCompleteSubGraph(Map<Long, Map<Long,Double>> shortestPaths, Set<Long> pointsOfInterest) {
        Map<Long, Map<Long, Double>> subGraph = new HashMap<>();

        // Pour chaque point d'intérêt 'source', on va filtrer les chemins qui mènent aux autres points d'intérêt
        for(Long source : pointsOfInterest) {
            Map<Long, Double> filteredPaths = new HashMap<>();

            // On parcourt à nouveau tous les points d'intérêt, cette fois-ci pour les destinations
            for(Long destination : pointsOfInterest) {
                if(!source.equals(destination)) {
                    filteredPaths.put(destination, shortestPaths.get(source).get(destination));
                }
            }
            subGraph.put(source, filteredPaths);
        }
        // Le sous-graphe complet est retourné : il contient les distances entre chaque paire de points d'intérêt
        return subGraph;
        //GABODI GABODA
    }

    // Méthode pour trouver le meilleur tour tout en respectant les contraintes de pickup avant livraison
    /**
     * Find the best tour while respecting the constraints of pickup before delivery
     * @param completeSubGraph Map containing the complete subgraph between the points of interest
     * @param deliveries List of deliveries
     * @param wareHouseId Warehouse ID
     * @return List containing the best tour
     */
    private List<Long> findBestTour(Map<Long, Map<Long, Double>> completeSubGraph, List<Delivery> deliveries, long wareHouseId) {
        List<Long> tour = new ArrayList<>();
        Set<Long> visitedPickups = new HashSet<>();
        Set<Long> visitedDeliveries = new HashSet<>();
        long currentLocation = wareHouseId;
        this.currentCapacity = 0;

        tour.add(currentLocation);

        while(visitedDeliveries.size() < deliveries.size()) {
            //Chercher le prochain point à visiter en respectant les contraintes
            long nextLocation = findNextLocation(completeSubGraph, currentLocation, deliveries, visitedPickups, visitedDeliveries);
            tour.add(nextLocation);

            //Marquer le point comme visité (pickup ou delivery)
            markVisited(nextLocation, deliveries, visitedPickups, visitedDeliveries);
            currentLocation = nextLocation;
        }

        //Retourner à l'entrepôt
        tour.add(wareHouseId);
        return tour;
    }


    // Chercher le prochain point à visiter en respectant les contraintes de pickup et de livraison
    /**
     * Find the next location to visit while respecting the constraints of pickup and delivery
     * @param subGraph Map containing the complete subgraph between the points of interest
     * @param currentLocation Current location
     * @param deliveries List of deliveries
     * @param visitedPickups Set of visited pickups
     * @param visitedDeliveries Set of visited deliveries
     * @return Next location to visit
     */
    private long findNextLocation(Map<Long, Map<Long, Double>> subGraph, long currentLocation, List<Delivery> deliveries, Set<Long> visitedPickups, Set<Long> visitedDeliveries) {
        long nextLocation = -1; // Initialisation de la prochaine destination
        Double shortestDistance = Double.MAX_VALUE; // La distance la plus courte sera mise à jour au fur et à mesure
        Double shortestDistanceToDelivery = Double.MAX_VALUE; // La distance la plus courte vers une livraison (pour gérer la capacité)
        int type = -1; // 0 si Pickup 1 si Delivery

        // Parcourir les livraisons pour trouver la prochaine destination valide (pickup ou livraison)
        for (Delivery delivery : deliveries) {
            long pickupLocation = delivery.getPickupLocation();
            long deliveryLocation = delivery.getDeliveryLocation();

            // Si le pickup n'a pas encore été visité, on considère cette option
            if (!visitedPickups.contains(pickupLocation) && currentCapacity < 5 ) {
                Double distanceToPickup = subGraph.get(currentLocation).get(pickupLocation);
                if (distanceToPickup != null && distanceToPickup < shortestDistance) {
                    nextLocation = pickupLocation; // On choisit ce pickup
                    type = 0;
                    shortestDistance = distanceToPickup; // On met à jour la distance minimale
                }
            }
            // Si le pickup a été visité mais pas encore la livraison, on considère cette livraison
            else if (!visitedDeliveries.contains(deliveryLocation)) {
                Double distanceToDelivery = subGraph.get(currentLocation).get(deliveryLocation);
                if (distanceToDelivery != null && distanceToDelivery < shortestDistance) {
                    nextLocation = deliveryLocation; // On choisit cette livraison
                    type = 1;
                    shortestDistance = distanceToDelivery; // On met à jour la distance minimale
                    shortestDistanceToDelivery = distanceToDelivery;
                }

            }
        }

        if (type == 0)
        {
            this.currentCapacity += 1;
        }
        if (type == 1)
        {
            this.currentCapacity -= 1;
        }
        /*else
        {
            System.out.println("currentCapacity = -1");
        }*/
        // Retourner la prochaine destination trouvée (ou -1 si quelque chose se passe mal, mais cela ne devrait jamais arriver)
        return nextLocation;
    }

    /**
     * Mark the visited pickup and delivery locations
     * @param location Location to mark as visited
     * @param deliveries List of deliveries
     * @param visitedPickups Set of visited pickups
     * @param visitedDeliveries Set of visited deliveries
     */
    private void markVisited(long location, List<Delivery> deliveries, Set<Long> visitedPickups, Set<Long> visitedDeliveries) {
        for(Delivery delivery : deliveries) {
            if(delivery.getPickupLocation() == location) {
                visitedPickups.add(location);
            }
            else if(delivery.getDeliveryLocation() == location) {
                visitedDeliveries.add(location);
            }
        }
    }

    /**
     * Get the complete path between each pair of points in the tour
     * @param cityMap City map
     * @param tour List of points in the tour
     * @return List containing the complete path of road segments
     */
    public List<RoadSegment> getCompletePath(CityMap cityMap, List<Long> tour) {
        List<RoadSegment> completePath = new ArrayList<>();

        // Parcourir les paires consécutives de points d'intérêt dans le tour
        for (int i = 0; i < tour.size() - 1; i++) {
            long start = tour.get(i);
            long destination = tour.get(i + 1);

            // Utiliser Dijkstra pour obtenir les segments de route entre start et destination
            List<RoadSegment> pathBetween = dijkstraPath(cityMap, start, destination);

            // Ajouter tous les segments du chemin au chemin complet
            completePath.addAll(pathBetween);
        }

        return completePath; // Retourner la liste complète des segments de route
    }

    /**
     * Get the complete path between two points using Dijkstra's algorithm
     * @param cityMap City map
     * @param source Source intersection
     * @param destination Destination intersection
     * @return List containing the complete path of road segments
     */
    private List<RoadSegment> dijkstraPath(CityMap cityMap, long source, long destination) {
        // Map pour garder la distance minimum depuis la source
        Map<Long, Double> distances = new HashMap<>();
        // Map pour garder l'ID de la précédente intersection
        Map<Long, Long> previous = new HashMap<>();
        // Map pour associer chaque intersection au segment de route emprunté
        Map<Long, RoadSegment> roadMap = new HashMap<>();

        PriorityQueue<IntersectionDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(IntersectionDistance::getDistance));

        // Initialisation : on met toutes les distances à l'infini sauf la source
        for (Intersection intersection : cityMap.getIntersections()) {
            distances.put(intersection.getId(), Double.MAX_VALUE);
        }
        distances.put(source, 0.0);
        pq.add(new IntersectionDistance(source, 0));

        // Dijkstra
        while (!pq.isEmpty()) {
            IntersectionDistance current = pq.poll();
            long currentIntersectionId = current.getIntersectionId();

            // Si on atteint la destination, on peut arrêter
            if (currentIntersectionId == destination) {
                break;
            }

            // Parcourir tous les segments partant de l'intersection actuelle
            for (RoadSegment segment : cityMap.getOutGoingRoadSegments(currentIntersectionId)) {
                long neighbour = segment.getDestination();
                double newDistance = distances.get(currentIntersectionId) + segment.getLength();

                // Mise à jour si un chemin plus court est trouvé
                if (newDistance < distances.get(neighbour)) {
                    distances.put(neighbour, newDistance);
                    pq.add(new IntersectionDistance(neighbour, newDistance));
                    previous.put(neighbour, currentIntersectionId);
                    roadMap.put(neighbour, segment);  // Garder le segment de route utilisé
                }
            }
        }

        // Reconstruire le chemin à partir des intersections visitées (backtracking)
        List<RoadSegment> path = new ArrayList<>();
        Long currentNode = destination;
        while (previous.containsKey(currentNode)) {
            RoadSegment segment = roadMap.get(currentNode);
            path.add(0, segment);  // Ajouter le segment dans l'ordre inverse
            currentNode = previous.get(currentNode);  // Remonter au précédent
        }

        // Si on n'a pas trouvé de chemin, on renvoie une exception
        if (path.isEmpty()) {
            throw new IllegalStateException("Aucun chemin trouvé entre " + source + " et " + destination);
        }

        return path;
    }

    // Méthode pour calculer les estimations de temps pour chaque arrêt
    /**
     * Calculate the time estimates for each stop
     * @param completeDeliveryPath List of road segments in the complete delivery path
     * @param deliveries List of deliveries
     * @param startTime Start time
     * @return List containing the time estimates for each stop
     */
    private List<Map<String, Object>> calculateTimeEstimates(List<RoadSegment> completeDeliveryPath, List<Delivery> deliveries, LocalTime startTime) {
        List<Map<String, Object>> timeEstimates = new ArrayList<>();
        LocalTime currentTime = startTime;

        for (RoadSegment segment : completeDeliveryPath) {
            double distanceKm = segment.getLength() / 1000.0; // Conversion de mètres en kilomètres
            long travelTimeSeconds = (long) ((distanceKm / COURIER_SPEED_KMH) * 3600);

            Map<String, Object> stopInfo = new HashMap<>();
            stopInfo.put("segment", segment);
            stopInfo.put("departureTime", currentTime);
            currentTime = currentTime.plusSeconds(travelTimeSeconds);
            stopInfo.put("arrivalTime", currentTime);

            timeEstimates.add(stopInfo);

            // Check if this stop is a pickup or delivery
            for (Delivery delivery : deliveries) {
                if (delivery.getPickupLocation() == segment.getDestination()) {
                    currentTime = currentTime.plusSeconds(delivery.getPickupTime());
                    break;
                } else if (delivery.getDeliveryLocation() == segment.getDestination()) {
                    currentTime = currentTime.plusSeconds(delivery.getDeliveryTime());
                    break;
                }
            }
        }

        return timeEstimates;
    }

    // Méthode pour vérifier que le temps total ne dépasse pas 7 heures
    /**
     * Check that the total time does not exceed 7 hours
     * @param timeEstimates List of time estimates for each stop
     * @param deliveries List of deliveries
     */
    public void checkTotalTime(List<Map<String, Object>> timeEstimates, List<Delivery> deliveries)
    {
        double totalTimeMinutes = 0;

        for (Map<String, Object> estimate : timeEstimates) {
            LocalTime departureTime = (LocalTime) estimate.get("departureTime");
            LocalTime arrivalTime = (LocalTime) estimate.get("arrivalTime");

            long travelDurationSeconds = java.time.Duration.between(departureTime, arrivalTime).getSeconds();
            totalTimeMinutes += travelDurationSeconds / 60.0;

            if (estimate.containsKey("additionalWait")) {
                long additionalWaitSeconds = (long) estimate.get("additionalWait");
                totalTimeMinutes += additionalWaitSeconds / 60.0;
            }
        }

        for (Delivery delivery : deliveries) {
            long pickupTimeSeconds = delivery.getPickupTime();
            long deliveryTimeSeconds = delivery.getDeliveryTime();

            totalTimeMinutes += pickupTimeSeconds / 60.0;
            totalTimeMinutes += deliveryTimeSeconds / 60.0;
        }


        if (totalTimeMinutes > 420) {
            throw new IllegalArgumentException("The total time exceeds 7 hours: " + totalTimeMinutes + " minutes");
        }
    }



    // Classe pour gérer la priorité dans la file d'attente
    private static class IntersectionDistance {
        /**
         * IntersectionDistance class
         */
        private long intersectionId; // Utilisation d'un ID d'intersection au lieu d'un objet Intersection
        private double distance;

        /**
         * Constructor
         * @param intersectionId Intersection ID
         * @param distance Distance
         */
        public IntersectionDistance(long intersectionId, double distance) {
            this.intersectionId = intersectionId;
            this.distance = distance;
        }

        // Getter pour l'ID de l'intersection
        /**
         * Get the intersection ID
         * @return Intersection ID
         */
        public long getIntersectionId() {
            return intersectionId;
        }

        // Getter pour la distance
        /**
         * Get the distance
         * @return Distance
         */
        public double getDistance() {
            return distance;
        }
    }
}
