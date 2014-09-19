package as2388.uk_bus.stop;

public class Departure {
    private final String route;
    private final String destination;
    private final String time;

    public Departure(String route, String destination, String time) {
        this.route = route;
        this.destination = destination;
        this.time = time;
    }

    public String getRoute() {
        return route;
    }

    public String getDestination() {
        return destination;
    }

    public String getTime() {
        return time;
    }
}
