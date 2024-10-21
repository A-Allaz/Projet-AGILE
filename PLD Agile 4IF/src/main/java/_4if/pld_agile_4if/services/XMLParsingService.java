package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import _4if.pld_agile_4if.models.Warehouse;
import org.w3c.dom.Element;
import org.springframework.stereotype.Service;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLParsingService {

    public XMLParsingService() {}

    public CityMap parseCityMap(File xmlFile) throws JAXBException
    {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CityMap.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (CityMap) jaxbUnmarshaller.unmarshal(xmlFile);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour parser l'entrepôt à partir d'un fichier XML
    public Warehouse parseWarehouse(File xmlFile) {
        Warehouse warehouse = null;

        try {
            // Création du document XML à partir du fichier
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // Accéder à l'élément <entrepot>
            Element warehouseElement = (Element) document.getElementsByTagName("entrepot").item(0);

            // Extraction de l'attribut 'adresse' (id d'intersection)
            long address = Long.parseLong(warehouseElement.getAttribute("adresse"));

            // Extraction de l'attribut 'heureDepart'
            String heureDepartStr = warehouseElement.getAttribute("heureDepart");
            LocalTime departureTime = parseTime(heureDepartStr);

            // Création de l'objet Warehouse
            warehouse = new Warehouse(address, departureTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return warehouse;
    }

    // Méthode pour convertir la chaîne d'heure au format "8:0:0" en LocalTime
    private LocalTime parseTime(String timeStr) {
        String[] timeParts = timeStr.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);
        return LocalTime.of(hours, minutes, seconds);
    }

    public List<Delivery> parseDeliveryList(File xmlFile) throws JAXBException
    {
        List<Delivery> deliveries = new ArrayList<>();
        try {
            // Créer un contexte JAXB pour Delivery
            JAXBContext jaxbContext = JAXBContext.newInstance(Delivery.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // Créer un XMLStreamReader pour parcourir les éléments XML
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new java.io.FileInputStream(xmlFile));

            // Parcourir chaque élément du fichier XML
            while (reader.hasNext()) {
                int event = reader.next();
                // Lorsque l'on trouve un élément <livraison>, on le désérialise
                if (event == XMLEvent.START_ELEMENT && "livraison".equals(reader.getLocalName())) {
                    Delivery delivery = (Delivery) jaxbUnmarshaller.unmarshal(reader, Delivery.class).getValue();
                    deliveries.add(delivery);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    public boolean validateXML(File xmlFile)
    {
        return xmlFile.exists() && xmlFile.isFile();
    }
}
