package Model;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.List;

@XmlRootElement(name = "reseau")
public class CityMap {
    private List<Intersection> intersections;
    private List<RoadSegment> roadSegments;
    private Warehouse warehouse;

    @XmlElement(name = "noeud")
    public List<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<Intersection> intersections) {
        this.intersections = intersections;
    }

    @XmlElement(name = "troncon")
    public List<RoadSegment> getRoadSegments() {
        return roadSegments;
    }

    public void setRoadSegments(List<RoadSegment> roadSegments) {
        this.roadSegments = roadSegments;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void loadMap(String xmlFile) {
        try {
            JAXBContext context = JAXBContext.newInstance(CityMap.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            CityMap cityMap = (CityMap) unmarshaller.unmarshal(new File(xmlFile));
            this.intersections = cityMap.getIntersections();
            this.roadSegments = cityMap.getRoadSegments();
            this.warehouse = cityMap.getWarehouse();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
