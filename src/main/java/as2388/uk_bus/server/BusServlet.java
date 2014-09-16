package as2388.uk_bus.server;

import as2388.uk_bus.stop.Stop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jmx.snmp.internal.SnmpSubSystem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/stops")
public class BusServlet {
    private final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/test")
    public Response printMessage() {
        return Response.ok().entity("Mission Control reads you loud and clear").build();
    }

    @GET
    @Path("/")
    public Response test() throws IOException {
        Document doc = Jsoup.connect("http://www.nextbuses.mobi/WebView/BusStopSearch/BusStopSearchResults/ll_56.457373399999994,-3.0588688~?currentPage=2").get();
        Elements rawStops = doc.getElementsByClass("BusStops").get(0).getElementsByTag("tr");

        List<Stop> stops = new LinkedList<>();
        for (Element rawStop : rawStops) {
            Element stopEl = rawStop.getElementsByTag("td").get(1);

            Element stopBusDataEl = stopEl.getElementsByTag("a").get(0);
            String stopName = stopBusDataEl.html();
            String stopId = findString("BusStopSearchResults/(.*)\\?", stopBusDataEl.attr("href"));
            String stopStreet = findString("br /> (.*) </p>" , stopEl.html());

            Element stopLocationEl = stopEl.getElementsByTag("a").get(1);
            String[] stopLL = findString("ll_(.*)\\~\\?", stopLocationEl.attr("href")).split(",");
            String lat = stopLL[0];
            String lon = stopLL[1];

            stops.add(new Stop(stopId, stopName, stopStreet, lat, lon));
        }

       return Response.ok().entity(mapper.writeValueAsString(stops)).build();
    }

    private  String findString(String p, String input) {
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

}