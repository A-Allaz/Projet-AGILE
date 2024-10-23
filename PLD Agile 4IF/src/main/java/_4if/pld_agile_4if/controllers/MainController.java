package _4if.pld_agile_4if.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.services.XMLParsingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @RequestMapping("/")
    public String index() {
        return "home";
    }

    @Autowired
    private XMLParsingService xmlParsingService;  // Service pour parser les fichiers XML

    private CityMap cityMap;

    // Endpoint pour charger le fichier XML de la carte
    @PostMapping("/uploadMap")
    @ResponseBody
    public String uploadMapFile(@RequestParam("file") MultipartFile file) {
        try {
            // Convertir le fichier Multipart en fichier temporaire
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

    // Endpoint pour récupérer les données du CityMap et les envoyer sous forme de JSON
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

    // Endpoint pour charger le fichier XML des livraisons (si nécessaire)
    @PostMapping("/uploadTour")
    @ResponseBody
    public String uploadTourFile(@RequestParam("file") MultipartFile file) {
        try {
            // Gérer le fichier des livraisons (similaire au fichier de carte)
            File tempFile = File.createTempFile("deliveryTour", ".xml");
            file.transferTo(tempFile);

            // Vous pouvez ajouter une logique similaire ici pour parser et stocker les livraisons
            // ...
            return "Delivery tour uploaded successfully!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading the tour file: " + e.getMessage();
        }
    }

}
