public class Warehouse {
    private Location location;
    private CityMap cityMap;

    public Warehouse(Location location, CityMap cityMap) {
        this.location = location;
        this.cityMap = cityMap;

    }

    public CityMap getCityMap() {
        return cityMap;
    }

}
