package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.Intersection;
import _4if.pld_agile_4if.models.RoadSegment;

import java.util.*;

public class TourCalculatorService {

    // Méthode principale pour calculer le meilleur tour
    public List<Long> calculateOptimalTour(CityMap cityMap, List<Delivery> deliveries, long warehouseId) {
        // Étape 1: Identifier tous les points d'intérêt (entrepôt, points de pickup et livraison)
        Set<Long> pointsOfInterest = new HashSet<>();
        pointsOfInterest.add(cityMap.getWarehouse().getId()); // Ajout de l'entrepôt

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

        return optimalTour;
    }

    // Méthode pour calculer les plus courts chemins à partir de chaque point d'intérêt
    private Map<Long, Map<Long,Double>> computeShortestPaths(CityMap cityMap, Set<Long> pointsOfInterest) {
        Map<Long, Map<Long,Double>> shortestPaths = new HashMap<>();

        for(Long point : pointsOfInterest) {
            shortestPaths.put(point, dijkstra(cityMap, point));
        }

        return shortestPaths;
    }

    // Algorithme de Dijkstra pour calculer les plus courts chemins à partir d'une intersection source
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

            System.out.println("----------------------------------------------------");
            System.out.println(cityMap.getOutGoingRoadSegments(currentIntersectionId));
            System.out.println("----------------------------------------------------");

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
    private List<Long> findBestTour(Map<Long, Map<Long, Double>> completeSubGraph, List<Delivery> deliveries, long wareHouseId) {
        List<Long> tour = new ArrayList<>();
        Set<Long> visitedPickups = new HashSet<>();
        Set<Long> visitedDeliveries = new HashSet<>();
        long currentLocation = wareHouseId;

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
    private long findNextLocation(Map<Long, Map<Long, Double>> subGraph, long currentLocation, List<Delivery> deliveries, Set<Long> visitedPickups, Set<Long> visitedDeliveries) {
        long nextLocation = -1; // Initialisation de la prochaine destination
        Double shortestDistance = Double.MAX_VALUE; // La distance la plus courte sera mise à jour au fur et à mesure

        // Parcourir les livraisons pour trouver la prochaine destination valide (pickup ou livraison)
        for (Delivery delivery : deliveries) {
            long pickupLocation = delivery.getPickupLocation();
            long deliveryLocation = delivery.getDeliveryLocation();

            // Si le pickup n'a pas encore été visité, on considère cette option
            if (!visitedPickups.contains(pickupLocation)) {
                Double distanceToPickup = subGraph.get(currentLocation).get(pickupLocation);
                if (distanceToPickup != null && distanceToPickup < shortestDistance) {
                    nextLocation = pickupLocation; // On choisit ce pickup
                    shortestDistance = distanceToPickup; // On met à jour la distance minimale
                }
            }
            // Si le pickup a été visité mais pas encore la livraison, on considère cette livraison
            else if (!visitedDeliveries.contains(deliveryLocation)) {
                Double distanceToDelivery = subGraph.get(currentLocation).get(deliveryLocation);
                if (distanceToDelivery != null && distanceToDelivery < shortestDistance) {
                    nextLocation = deliveryLocation; // On choisit cette livraison
                    shortestDistance = distanceToDelivery; // On met à jour la distance minimale
                }
            }
        }

        // Retourner la prochaine destination trouvée (ou -1 si quelque chose se passe mal, mais cela ne devrait jamais arriver)
        return nextLocation;
    }

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


    // Classe pour gérer la priorité dans la file d'attente
    private static class IntersectionDistance {
        private long intersectionId; // Utilisation d'un ID d'intersection au lieu d'un objet Intersection
        private double distance;

        public IntersectionDistance(long intersectionId, double distance) {
            this.intersectionId = intersectionId;
            this.distance = distance;
        }

        // Getter pour l'ID de l'intersection
        public long getIntersectionId() {
            return intersectionId;
        }

        // Getter pour la distance
        public double getDistance() {
            return distance;
        }
    }
}
