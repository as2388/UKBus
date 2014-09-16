package as2388.uk_bus.server;

import as2388.uk_bus.stop.Stop;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/stops")
public class BusServlet {

    @GET
    @Path("/test")
    public Response printMessage() {
        return Response.ok().entity("Mission Control reads you loud and clear").build();
    }

    @GET
    @Path("/")
    public Response test() {
        Stop stop = new Stop("Inv", "main", 56.123, -0.2545);
        return Response.ok().entity(stop).build();
    }

}