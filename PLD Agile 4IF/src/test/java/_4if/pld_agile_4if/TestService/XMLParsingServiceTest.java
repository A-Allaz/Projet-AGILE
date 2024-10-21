package _4if.pld_agile_4if.TestService;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.Warehouse;
import _4if.pld_agile_4if.services.XMLParsingService;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XMLParsingServiceTest
{
    @Test
    public void testParseCityMapXML() throws JAXBException
    {
        XMLParsingService xmlParsingService = new XMLParsingService();
        File file = new File("src/test/ressources/petitPlan.xml");
        CityMap cityMap = xmlParsingService.parseCityMap(file);
        assertNotNull(cityMap);

        System.out.println(cityMap);
    }

    @Test
    public void testParseDeliveryProgram() throws JAXBException
    {
        XMLParsingService xmlParsingService = new XMLParsingService();
        File file = new File("src/test/ressources/demandePetit1.xml");
        List<Delivery> deliveries = xmlParsingService.parseDeliveryList(file);
        assertNotNull(deliveries);

        for(Delivery delivery : deliveries)
        {
            System.out.println(delivery);
        }
    }

    @Test
    public void testParseWarehouse() throws Exception {
        XMLParsingService xmlParsingService = new XMLParsingService();
        File file = new File("src/test/ressources/demandePetit1.xml"); // Fichier XML contenant les infos de l'entrepôt

        Warehouse warehouse = xmlParsingService.parseWarehouse(file);
        assertNotNull(warehouse); // Vérifie que l'entrepôt a été correctement parsé

        // Vérification des attributs parsés
        //assertEquals(342873658, warehouse.getAddress()); // Vérifie que l'adresse est correcte
        //assertEquals("08:00", warehouse.getDepartureTime().toString()); // Vérifie que l'heure de départ est correcte

        System.out.println(warehouse); // Affiche l'entrepôt (peut être retiré)
    }
}
