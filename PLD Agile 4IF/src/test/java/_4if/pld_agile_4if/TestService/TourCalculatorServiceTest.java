package _4if.pld_agile_4if.TestService;

import _4if.pld_agile_4if.models.*;
import _4if.pld_agile_4if.services.DeliveryManagementService;
import _4if.pld_agile_4if.services.TourCalculatorService;
import _4if.pld_agile_4if.services.XMLParsingService;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.List;
import java.util.Map;

public class TourCalculatorServiceTest {

    private XMLParsingService xmlParsingService;
    private TourCalculatorService tourCalculatorService;
    private DeliveryManagementService deliveryManagementService;
    private CityMap cityMap;
    private List<Delivery> deliveries;

    @BeforeEach
    public void setUp() throws JAXBException {
        xmlParsingService = new XMLParsingService();
        tourCalculatorService = new TourCalculatorService();
        deliveryManagementService = new DeliveryManagementService(tourCalculatorService);

        File cityMapFile = new File("src/test/ressources/petitPlan.xml");
        File deliveryFile = new File("src/test/ressources/demandePetit2.xml");

        // Validate and parse CityMap and Deliveries
        Assertions.assertTrue(xmlParsingService.validateXML(deliveryFile), "Delivery list XML file is invalid!");
        Assertions.assertTrue(xmlParsingService.validateXML(cityMapFile), "City map XML file is invalid!");

        cityMap = xmlParsingService.parseCityMap(cityMapFile);
        deliveries = xmlParsingService.parseDeliveryList(deliveryFile);

        // Setting up warehouse and initializing cityMap in DeliveryManagementService
        cityMap.setWarehouse(xmlParsingService.parseWarehouse(deliveryFile));
        deliveryManagementService.initializeCityMap(cityMap, cityMap.getWarehouse().getAddress());
        deliveryManagementService.addDeliveryProgram(deliveries);
    }

    @Test
    public void testCalculateShortestPaths() {
        // Test Dijkstra's algorithm
        long sourceIntersectionId = cityMap.getIntersections().get(0).getId();
        Map<Long, Double> shortestPaths = tourCalculatorService.dijkstra(cityMap, sourceIntersectionId);

        Assertions.assertNotNull(shortestPaths, "Shortest paths result is null.");
        Assertions.assertTrue(shortestPaths.size() > 0, "No paths found from source intersection.");

        System.out.println("Shortest paths from intersection " + sourceIntersectionId + ":");
        shortestPaths.forEach((dest, distance) ->
                System.out.println("To intersection " + dest + ": " + distance + " meters"));
    }

    @Test
    public void testCalculateOptimalTour() {
        // Test optimal tour calculation with initial deliveries
        Map<String, Object> result = tourCalculatorService.calculateOptimalTourWithEstimates(cityMap, deliveries, cityMap.getWarehouse().getAddress());
        List<RoadSegment> optimalTour = (List<RoadSegment>) result.get("optimalTour");

        Assertions.assertNotNull(optimalTour, "Optimal tour is null.");
        Assertions.assertTrue(optimalTour.size() > 0, "Optimal tour has no segments.");

        System.out.println("Optimal Tour:");
        optimalTour.forEach(System.out::println);
    }

    @Test
    public void testAddModifyRemoveDelivery() {
        // Add a new delivery
        Delivery newDelivery = new Delivery(12345L, 67890L, 300, 400);
        deliveryManagementService.addDelivery(newDelivery);
        Assertions.assertTrue(deliveryManagementService.getAllDeliveries().contains(newDelivery), "New delivery not added.");

        // Modify the new delivery
        newDelivery.setPickupTime(600);
        deliveryManagementService.modifyDelivery(newDelivery.getId(), newDelivery);
        Assertions.assertEquals(600, deliveryManagementService.getAllDeliveries().stream()
                .filter(d -> d.getId() == newDelivery.getId())
                .findFirst()
                .get()
                .getPickupTime(), "Delivery pickup time not updated.");

        // Remove the delivery
        boolean removed = deliveryManagementService.removeDelivery(newDelivery.getId());
        Assertions.assertTrue(removed, "Delivery not removed.");
        Assertions.assertFalse(deliveryManagementService.getAllDeliveries().contains(newDelivery), "Removed delivery still present.");
    }

    @Test
    public void testAssignCourier() {
        // Initialize couriers
        deliveryManagementService.initializeCouriers(2);
        List<Courier> couriers = deliveryManagementService.getAllCouriers();
        Assertions.assertEquals(2, couriers.size(), "Couriers initialization failed.");

        // Assign a delivery to courier 1
        Delivery deliveryToAssign = deliveries.get(0);
        Courier courier = couriers.get(0);

        deliveryManagementService.assignDeliveryToCourier(courier.getId(), deliveryToAssign.getId());

        // Check that the delivery is assigned
        Assertions.assertTrue(courier.getAssignedDeliveries().contains(deliveryToAssign), "Delivery not assigned to courier.");

        // Verify courier route recalculated without calling recalculateTour
        Assertions.assertNotNull(courier.getCurrentRoute(), "Courier route is null after assignment.");
        Assertions.assertTrue(!courier.getCurrentRoute().getStopPoints().isEmpty(), "Courier route is empty after assignment.");

        System.out.println("Courier Route:");
        courier.getCurrentRoute().getStopPoints().forEach(System.out::println);
    }
}
