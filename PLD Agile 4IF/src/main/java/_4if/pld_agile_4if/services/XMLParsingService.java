package _4if.pld_agile_4if.services;

import _4if.pld_agile_4if.models.CityMap;
import _4if.pld_agile_4if.models.Delivery;
import org.springframework.stereotype.Service;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


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
