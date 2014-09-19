package as2388.uk_bus.server;

import as2388.uk_bus.stop.Departure;
import as2388.uk_bus.stop.Stop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jmx.snmp.internal.SnmpSubSystem;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/stops")
public class BusServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok().entity("Mission Control reads you loud and clear").build();
    }

    @GET
    @Path("/search")
    public Response listByLatLon(@QueryParam("lat") String qlat, @QueryParam("lon") String qlon) throws IOException {
        System.out.println("http://www.nextbuses.mobi/WebView/BusStopSearch/BusStopSearchResults/ll_" + qlat + "," + qlon + "~?currentPage=0");
        Document doc = Jsoup.connect("http://www.nextbuses.mobi/WebView/BusStopSearch/BusStopSearchResults/ll_" + qlat + "," + qlon + "~?currentPage=0").get();
        Elements rawStops = doc.getElementsByClass("BusStops").get(0).getElementsByTag("tr");
        String mapSrc = doc.getElementById("sMap").attr("src");

        List<Stop> stops = new LinkedList<>();
        int counter = 1;
        for (Element rawStop : rawStops) {
            Element stopEl = rawStop.getElementsByTag("td").get(1);

            Element stopBusDataEl = stopEl.getElementsByTag("a").get(0);
            String stopNameMain = stopBusDataEl.html().split(" \\(")[0];
            String stopNameBracket = stopBusDataEl.html().split(" \\(")[1].replaceAll("\\)", "");
            String stopName = stopNameBracket + " " + stopNameMain;
            String stopId = findString("BusStopSearchResults/(.*)\\?", stopBusDataEl.attr("href"));
            String stopStreet = findString("br /> (.*) </p>" , stopEl.html()).replace("on ", "");

            Element stopLocationEl = stopEl.getElementsByTag("a").get(1);
            String[] stopLL = findString("label:" + counter + "\\|((\\d|\\.|,|\\-)+)", mapSrc).split(",");
            String lat = stopLL[0];
            String lon = stopLL[1];

            stops.add(new Stop(stopId, stopName, stopStreet, lat, lon));
            counter++;
        }

       return Response.ok().entity(mapper.writeValueAsString(stops)).build();
    }

    @GET
    @Path("/departures")
    public Response listDepartures(@QueryParam("stopId") String stopId) throws IOException {
        Document doc = Jsoup.connect("http://www.nextbuses.mobi/WebView/BusStopSearch/BusStopSearchResults/" + stopId + "?currentPage=0").timeout(20000).get();
        if (doc.getElementsByClass("BusStops").size() == 0) {
            return Response.noContent().build();
        }
        Elements rawDepartures = doc.getElementsByClass("BusStops").get(0).getElementsByTag("tr");

        List<Departure> departures = new LinkedList<>();
        for (Element rawDeparture : rawDepartures) {
            Element routeEl = rawDeparture.getElementsByTag("td").get(0);
            String route = rawDeparture.getElementsByClass("Stops").get(0).getElementsByTag("a").get(0).html();

            String[] departureText = rawDeparture.getElementsByTag("td").get(1).getElementsByClass("Stops").get(0).html().split("&nbsp;");
            String destination = departureText[0];
            String time = departureText[1].replaceAll("at |in ", "").split(" \\(")[0]; //TODO: this removes the "(DAY)" part of the time. In case e.g. the service doesn't run on weekends, a check should be done to make sure this is ok to do

            //rawDepartures are sorted by time. If the time of this element is more than one hour ahead, return
            if (time.contains("min") || time.contains("DUE") || withinAnHour(time)) {
                departures.add(new Departure(route, destination, time));
            } else {
                return Response.ok().entity(mapper.writeValueAsString(departures)).build();
            }
        }

        return Response.ok().entity(mapper.writeValueAsString(departures)).build();
    }

    private boolean withinAnHour(String time) {
        System.out.println(time);
        System.out.println(time.split(":")[0]);
        System.out.println(time.split(":")[1]);

        LocalDateTime timeNow = new LocalDateTime();
        LocalDateTime busTime = new LocalDateTime(timeNow.getYear(), timeNow.getMonthOfYear(), timeNow.getDayOfMonth(),
                Integer.valueOf(time.split(":")[0]), Integer.valueOf(time.split(":")[1]), 0);

        //TODO: deal with change of day/month/year etc.
        return Minutes.minutesBetween(timeNow, busTime).getMinutes() < 60;
    }

    private String findString(String p, String input) {
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }
}