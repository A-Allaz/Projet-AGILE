package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.Intersection;
import _4if.pld_agile_4if.models.RoadSegment;

import java.util.*;

class TourCalculatorService {

    public Map<Integer, Map<Integer, Double>> computeShortestPaths(CityMap cityMap, List<Delivery> deliveries) {
        // On extrait les points d'intérêt (pickup et delivery points) en utilisant les IDs d'intersections
        Set<Integer> pointsOfInterest = new HashSet<>();

        // Ajouter les points de pickup et delivery dans le set des points d'intérêt
        for (Delivery delivery : deliveries) {
            pointsOfInterest.add(delivery.getPickupLocation());   // ID de l'intersection de pickup
            pointsOfInterest.add(delivery.getDeliveryLocation()); // ID de l'intersection de delivery
        }

        // On va construire un sous-graphe complet où chaque sommet est un pickup ou un delivery
        Map<Integer, Map<Integer, Double>> completeSubGraph = new HashMap<>();

        // Pour chaque point d'intérêt (ID d'intersection), on exécute Dijkstra pour trouver les plus courts chemins
        for (int sourceId : pointsOfInterest) {
            // Calculer les plus courts chemins à partir de l'ID source
            Map<Integer, Double> shortestPathsFromSource = dijkstra(cityMap, sourceId);

            // On filtre les résultats pour ne conserver que les chemins vers les autres points d'intérêt
            Map<Integer, Double> filteredPaths = new HashMap<>();
            for (int destinationId : pointsOfInterest) {
                // Ne pas inclure le chemin vers soi-même
                if (sourceId != destinationId) {
                    filteredPaths.put(destinationId, shortestPathsFromSource.get(destinationId));
                }
            }

            // Ajouter les chemins les plus courts à partir du point source au sous-graphe complet
            completeSubGraph.put(sourceId, filteredPaths);
        }

        return completeSubGraph;
    }


    private Map<Integer, Double> dijkstra(CityMap cityMap, int sourceId) {
        // Utilisation d'une Map pour stocker les distances minimales à partir du point source
        Map<Integer, Double> distances = new HashMap<>();
        // Map pour stocker le nœud précédent dans le chemin le plus court (facultatif si vous n'avez pas besoin des chemins)
        Map<Integer, Integer> previous = new HashMap<>();
        // PriorityQueue pour choisir l'intersection avec la plus petite distance lors de l'exploration
        PriorityQueue<IntersectionDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(IntersectionDistance::getDistance));

        // Initialisation : on met toutes les distances à l'infini sauf celle du point source
        for (Intersection intersection : cityMap.getIntersections()) {
            distances.put(intersection.getId(), Double.MAX_VALUE);
        }
        distances.put(sourceId, 0.0);
        pq.add(new IntersectionDistance(sourceId, 0.0));

        // Algorithme de Dijkstra
        while (!pq.isEmpty()) {
            // On prend l'intersection avec la plus petite distance actuelle
            IntersectionDistance current = pq.poll();
            int currentId = current.getIntersectionId();  // Nous travaillons maintenant avec les IDs d'intersection

            /*
            // Parcourir tous les segments de route partant de cette intersection
            for (RoadSegment segment : cityMap.getOutgoingRoadSegments(currentId)) {
                int neighborId = segment.getDestination(); // ID de l'intersection de destination

                // Calcul de la nouvelle distance pour atteindre le voisin via ce segment
                double newDist = distances.get(currentId) + segment.getLength();
                if (newDist < distances.get(neighborId)) {
                    // Mise à jour de la distance minimale si on a trouvé un chemin plus court
                    distances.put(neighborId, newDist);
                    pq.add(new IntersectionDistance(neighborId, newDist));
                    previous.put(neighborId, currentId); // Optionnel : utile pour reconstruire le chemin si nécessaire
                }
            }
            */
        }

        // Retourner les distances minimales depuis le point source vers toutes les autres intersections
        return distances;
    }


    // Classe pour gérer la priorité dans la file d'attente
    private static class IntersectionDistance {
        private int intersectionId; // Utilisation d'un ID d'intersection au lieu d'un objet Intersection
        private double distance;

        public IntersectionDistance(int intersectionId, double distance) {
            this.intersectionId = intersectionId;
            this.distance = distance;
        }

        // Getter pour l'ID de l'intersection
        public int getIntersectionId() {
            return intersectionId;
        }

        // Getter pour la distance
        public double getDistance() {
            return distance;
        }
    }
}
