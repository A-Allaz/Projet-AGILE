package _4if.pld_agile_4if.controllers;

import _4if.pld_agile_4if.models.*;
import _4if.pld_agile_4if.services.DeliveryManagementService;
import _4if.pld_agile_4if.services.TourCalculatorService;
import _4if.pld_agile_4if.services.XMLParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private XMLParsingService xmlParsingService;

    @Autowired
    private TourCalculatorService tourCalculatorService;

    @Autowired
    private DeliveryManagementService deliveryManagementService;

    private CityMap cityMap;

    @RequestMapping("/")
    public String index() {
        return "home";
    }

    // Initialiser les livreurs avec un nombre fixe au lancement
    @PostMapping("/initializeCouriers")
    @ResponseBody
    public Map<String, String> initializeCouriers(@RequestParam("count") int count) {
        Map<String, String> response = new HashMap<>();
        try {
            deliveryManagementService.initializeCouriers(count);
            response.put("status", "success");
            response.put("message", "Couriers initialized successfully with count: " + count);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error initializing couriers: " + e.getMessage());
        }
        return response;
    }


    // Endpoint pour charger le fichier XML de la carte
    @PostMapping("/uploadMap")
    @ResponseBody
    public String uploadMapFile(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("cityMap", ".xml");
            file.transferTo(tempFile);

            // Parser le fichier XML et stocker le CityMap
            cityMap = xmlParsingService.parseCityMap(tempFile);
            return "City map uploaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading the map file: " + e.getMessage();
        }
    }

    // Endpoint pour récupérer les données du CityMap sous forme de JSON
    @GetMapping("/mapData")
    @ResponseBody
    public Map<String, Object> getMapData() {
        Map<String, Object> data = new HashMap<>();
        if (cityMap != null) {
            data.put("node", cityMap.getIntersections());
            data.put("roadSegment", cityMap.getRoadSegments());
        } else {
            data.put("error", "No map data available");
        }
        return data;
    }

    // Endpoint pour charger le fichier XML des livraisons
    @PostMapping("/uploadTour")
    @ResponseBody
    public String uploadTourFile(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("deliveryTour", ".xml");
            file.transferTo(tempFile);

            List<Delivery> deliveries = xmlParsingService.parseDeliveryList(tempFile);
            Warehouse warehouse = xmlParsingService.parseWarehouse(tempFile);
            cityMap.setWarehouse(warehouse);

            // Ajouter les livraisons et recalculer les tournées
            for (Delivery delivery : deliveries) {
                deliveryManagementService.addDelivery(delivery);
            }

            return "Delivery tour uploaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading the tour file: " + e.getMessage();
        }
    }

    // Endpoint pour récupérer les données des tournées optimisées
    @GetMapping("/optimalTour")
    @ResponseBody
    public Map<String, Object> getOptimalTour() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Delivery> deliveries = deliveryManagementService.getAllDeliveries();

            // Appel de la méthode `calculateOptimalTourWithEstimates` pour obtenir le tour optimal et les estimations de temps
            Map<String, Object> result = tourCalculatorService.calculateOptimalTourWithEstimates(cityMap, deliveries, cityMap.getWarehouse().getAddress());
            List<RoadSegment> optimalTour = (List<RoadSegment>) result.get("optimalTour");
            List<Map<String, Object>> timeEstimates = (List<Map<String, Object>>) result.get("timeEstimates");

            response.put("optimalTour", optimalTour);
            response.put("timeEstimates", timeEstimates);
            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error calculating optimal tour: " + e.getMessage());
        }
        return response;
    }

    // Endpoint pour assigner une livraison à un livreur
    @PostMapping("/assignDeliveryToCourier")
    @ResponseBody
    public Map<String, String> assignDeliveryToCourier(
            @RequestParam("courierId") int courierId,
            @RequestParam("deliveryId") int deliveryId) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean success = deliveryManagementService.assignDeliveryToCourier(courierId, deliveryId);
            if (success) {
                response.put("status", "success");
                response.put("message", "Delivery assigned to courier successfully.");
            } else {
                response.put("status", "error");
                response.put("message", "Assignment failed. Delivery or courier not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error assigning delivery to courier: " + e.getMessage());
        }
        return response;
    }

    // Endpoint pour récupérer les informations d'un livreur (livraisons et route)
    @GetMapping("/courierInfo")
    @ResponseBody
    public Map<String, Object> getCourierInfo(@RequestParam("courierId") int courierId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Courier courier = deliveryManagementService.getCourierById(courierId);
            if (courier != null) {
                response.put("courier", courier);
                response.put("assignedDeliveries", courier.getAssignedDeliveries());
                response.put("currentRoute", courier.getCurrentRoute().getStopPoints());
                response.put("status", "success");
            } else {
                response.put("status", "error");
                response.put("message", "Courier not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error retrieving courier info: " + e.getMessage());
        }
        return response;
    }


    @GetMapping("/deliveries")
    @ResponseBody
    public List<Delivery> getDeliveries() {
        return deliveryManagementService.getAllDeliveries();
    }

    @GetMapping("/mapPoints")
    @ResponseBody
    public Map<String, Object> getMapPoints() {
        Map<String, Object> data = new HashMap<>();
        data.put("deliveries", deliveryManagementService.getAllDeliveries());
        data.put("warehouse", cityMap.getWarehouse());
        return data;
    }

    // Endpoint pour ajouter une nouvelle livraison
    @PostMapping("/addDelivery")
    @ResponseBody
    public Map<String, String> addDelivery(
            @RequestParam("pickupLocation") long pickupLocation,
            @RequestParam("deliveryLocation") long deliveryLocation,
            @RequestParam("pickupTime") int pickupTime,
            @RequestParam("deliveryTime") int deliveryTime) {
        Map<String, String> response = new HashMap<>();
        try {
            // Création d'une nouvelle instance de livraison
            Delivery newDelivery = new Delivery();
            newDelivery.setPickupLocation(pickupLocation);
            newDelivery.setDeliveryLocation(deliveryLocation);
            newDelivery.setPickupTime(pickupTime);
            newDelivery.setDeliveryTime(deliveryTime);

            // Ajout de la livraison via le service
            deliveryManagementService.addDelivery(newDelivery);

            response.put("status", "success");
            response.put("message", "Delivery added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error adding delivery: " + e.getMessage());
        }
        return response;
    }

    @PutMapping("/updateDelivery/{deliveryId}")
    @ResponseBody
    public Map<String, String> updateDelivery(
            @PathVariable("deliveryId") long deliveryId,
            @RequestBody Delivery updatedDelivery) {
        Map<String, String> response = new HashMap<>();
        try {
            deliveryManagementService.modifyDelivery(deliveryId, updatedDelivery);
            response.put("status", "success");
            response.put("message", "Delivery updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error updating delivery: " + e.getMessage());
        }
        return response;
    }

    // Endpoint pour supprimer une livraison existante
    @DeleteMapping("/deleteDelivery/{id}")
    @ResponseBody
    public Map<String, String> deleteDelivery(@PathVariable("id") long deliveryId) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean success = deliveryManagementService.removeDelivery(deliveryId);

            if (success) {
                response.put("status", "success");
                response.put("message", "Delivery deleted successfully!");
            } else {
                response.put("status", "error");
                response.put("message", "Delivery not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error deleting delivery: " + e.getMessage());
        }
        return response;
    }


}
