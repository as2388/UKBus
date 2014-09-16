package as2388.uk_bus.stop;

public class Stop {
    private final String id;
    private final String name;
    private final String street;
    private final String lat;
    private final String lon;

    public Stop(String id, String name, String street, String lat, String lon) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}

