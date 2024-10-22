package _4if.pld_agile_4if.TestService;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.RoadSegment;
import _4if.pld_agile_4if.services.TourCalculatorService;
import _4if.pld_agile_4if.services.XMLParsingService;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

public class TourCalculatorServiceTest {

    @Test
    public void testDijkstra() {

        File deliveryFile = new File("src/test/ressources/demandePetit2.xml");
        File cityMapFile = new File("src/test/ressources/petitPlan.xml");

        XMLParsingService xmlParsingService = new XMLParsingService();

        if (!xmlParsingService.validateXML(cityMapFile)) {
            System.out.println("City map XML file is not valid!");
            return;
        }
        if (!xmlParsingService.validateXML(deliveryFile)) {
            System.out.println("Delivery list XML file is not valid!");
            return;
        }
        CityMap cityMap = null;
        TourCalculatorService tourCalculatorService = new TourCalculatorService();
        List<Delivery> deliveries = null;
        List<RoadSegment> optimalTour = null;


        try {
            cityMap = xmlParsingService.parseCityMap(cityMapFile);
            // System.out.println("Parsed CityMap: " + cityMap);  // Assuming CityMap has a meaningful toString() implementation

            long sourceIntersectionId = cityMap.getIntersections().get(4).getId();
            Map<Long, Double> shortestPaths = tourCalculatorService.dijkstra(cityMap, sourceIntersectionId);

            System.out.println("Shortest paths from intersection " + sourceIntersectionId + ":");
            for (Map.Entry<Long, Double> entry : shortestPaths.entrySet()) {
                System.out.println("To intersection " + entry.getKey() + ": " + entry.getValue() + " meters");
            }
        } catch (JAXBException e) {
            System.out.println("Error parsing CityMap: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            deliveries = xmlParsingService.parseDeliveryList(deliveryFile);
            System.out.println("\nParsed Deliveries: ");
            cityMap.setWarehouse(xmlParsingService.parseWarehouse(deliveryFile));
            deliveries.forEach(System.out::println);
            System.out.println(cityMap.getWarehouse());
            // Assuming Delivery has a meaningful toString() implementation
        } catch (JAXBException e) {
            System.out.println("Error parsing Delivery list: " + e.getMessage());
            e.printStackTrace();
        }

        try
        {
            optimalTour = tourCalculatorService.calculateOptimalTour(cityMap, deliveries, cityMap.getWarehouse().getAddress());
            System.out.println("\n-----------------------------------");
            System.out.println("Optimal tour: ");
            optimalTour.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Error calculating optimal tour: " + e.getMessage());
        }


    }

}
