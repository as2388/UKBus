package as2388.uk_bus.stop;

public class Stop {
    private final String name;
    private final String street;
    private final double lat;
    private final double lon;

    public Stop(String name, String street, double lat, double lon) {
        this.name = name;
        this.street = street;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}

