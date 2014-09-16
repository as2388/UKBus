package as2388.uk_bus.server;

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

}