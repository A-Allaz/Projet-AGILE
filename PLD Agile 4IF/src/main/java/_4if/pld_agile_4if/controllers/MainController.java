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
    /**
     * Main controller for the application
     */

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

    /**
     * Initialize couriers with a fixed number at startup
     * @param count The number of couriers to initialize
     * @return A map containing the status and message of the operation
     */
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


    /**
     * Upload the XML file containing the city map
     * @param file The uploaded file
     * @return A message indicating the status of the operation
     */
    @PostMapping("/uploadMap")
    @ResponseBody
    public String uploadMapFile(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("cityMap", ".xml");
            file.transferTo(tempFile);

            // Parser le fichier XML et stocker le CityMap
            cityMap = xmlParsingService.parseCityMap(tempFile);
            deliveryManagementService.setCityMap(cityMap);
            return "City map uploaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading the map file: " + e.getMessage();
        }
    }

    /**
     * Get the map data as JSON
     * @return A map containing the node and road segment data
     */
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

    /**
     * Upload the XML file containing the delivery tour
     * @param file The uploaded file
     * @return A message indicating the status of the operation
     */
    @PostMapping("/uploadTour")
    @ResponseBody
    public String uploadTourFile(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("deliveryTour", ".xml");
            file.transferTo(tempFile);

            List<Delivery> deliveries = xmlParsingService.parseDeliveryList(tempFile);
            Warehouse warehouse = xmlParsingService.parseWarehouse(tempFile);
            cityMap.setWarehouse(warehouse);

            // Ajouter les livraisons
            deliveryManagementService.addDeliveryProgram(deliveries);

            return "Delivery tour uploaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error uploading the tour file: " + e.getMessage();
        }
    }

    /**
     * Get the optimal tour for a specific courier
     * @param courierId The ID of the courier
     * @return A map containing the optimal tour and time estimates
     */
    @GetMapping("/optimalTour")
    @ResponseBody
    public Map<String, Object> getOptimalTour(
            @RequestParam("courierId") int courierId) {
        Map<String, Object> response = new HashMap<>();
        try {
            deliveryManagementService.calculateCourierRoute(deliveryManagementService.getCourierById(courierId));
            List<RoadSegment> optimalTour = deliveryManagementService.getCourierRoute(courierId);
            List<Map<String, Object>> timeEstimates = deliveryManagementService.getCourierRouteTimeEstimates(courierId);

            response.put("optimalTour", optimalTour);
            response.put("timeEstimates", timeEstimates);
            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error calculating optimal tour: " + e.getMessage());
        }
        return response;
    }

    /**
     * Assign a delivery to a courier
     * @param courierId The ID of the courier
     * @param deliveryId The ID of the delivery
     * @return A map containing the status and message of the operation
     */
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

    /**
     * Get the information of a courier
     * @param courierId The ID of the courier
     * @return A map containing the courier information, current route and status
     */
    @GetMapping("/courierInfo")
    @ResponseBody
    public Map<String, Object> getCourierInfo(@RequestParam("courierId") int courierId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Courier courier = deliveryManagementService.getCourierById(courierId);
            if (courier != null) {
                response.put("courier", courier);
                response.put("currentRoute", courier.getCurrentRoute() != null ? courier.getCurrentRoute().getStopPoints() : null);
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


    /**
     * Get the information of a delivery
     * @param
     * @return A map containing the delivery information, assigned courier and route
     */
    @GetMapping("/deliveries")
    @ResponseBody
    public List<Delivery> getDeliveries() {
        return deliveryManagementService.getAllDeliveries();
    }

    /**
     * Get the information of a delivery
     * @param
     * @return A map containing the delivery information, assigned courier and route
     */
    @GetMapping("/couriers")
    @ResponseBody
    public List<Courier> getAllCouriers() {
        List<Courier> couriers = deliveryManagementService.getAllCouriers();
        System.out.println("Couriers JSON output: " + couriers);
        return couriers;
    }

    /**
     * Get the information of a delivery
     * @param
     * @return A map containing the delivery information, assigned courier and route
     */
    @GetMapping("/mapPoints")
    @ResponseBody
    public Map<String, Object> getMapPoints() {
        Map<String, Object> data = new HashMap<>();
        data.put("deliveries", deliveryManagementService.getAllDeliveries());
        data.put("warehouse", cityMap.getWarehouse());
        return data;
    }

    /**
     * Add a new delivery to the system
     * @param pickupLocation The ID of the pickup location
     * @param deliveryLocation The ID of the delivery location
     * @param pickupTime The pickup time
     * @param deliveryTime The delivery time
     * @return A map containing the status and message of the operation
     */
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

    /**
     * Update an existing delivery
     * @param deliveryId The ID of the delivery to update
     * @param updatedDelivery The updated delivery object
     * @return A map containing the status and message of the operation
     */
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

    /**
     * Delete a delivery from the system
     * @param deliveryId The ID of the delivery to delete
     * @return A map containing the status and message of the operation
     */
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

    public void resetControllerData() {
        this.cityMap = null;  // Réinitialiser la carte au niveau du contrôleur
        deliveryManagementService.resetData();  // Réinitialiser les données de DeliveryManagementService
    }

    /**
     * Reset the controller data
     * @return A map containing the status and message of the operation
     */
    @PostMapping("/resetControllerData")
    @ResponseBody
    public Map<String, String> resetControllerDataRq() {
        Map<String, String> response = new HashMap<>();
        try {
            resetControllerData();  // Appel de la méthode de réinitialisation
            response.put("status", "success");
            response.put("message", "Controller data reset successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error resetting controller data: " + e.getMessage());
        }
        return response;
    }



}
