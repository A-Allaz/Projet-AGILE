package _4if.pld_agile_4if.controllers;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.RoadSegment;
import _4if.pld_agile_4if.models.Warehouse;
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
            List<RoadSegment> optimalTour = tourCalculatorService.calculateOptimalTour(cityMap, deliveries, cityMap.getWarehouse().getAddress());

            response.put("optimalTour", optimalTour);
            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error calculating optimal tour: " + e.getMessage());
        }
        return response;
    }
}
