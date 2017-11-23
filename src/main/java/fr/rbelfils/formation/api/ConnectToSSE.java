package fr.rbelfils.formation.api;

import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;
import java.util.concurrent.TimeUnit;

@Singleton
@Path("client-sse")
public class ConnectToSSE {

    private static final String HOST_SSE = "localhost";
    private static final String PORT_SSE = "8080";
    private static final String PATH = "/test/rest/events/subscribe";

    @Path("startthread")
    @GET
    public Response connectToSseServeurAndLogs() throws InterruptedException {


       // String url = "http://localhost:8080/test/rest/events/fetch";
        String url = "http://"+HOST_SSE+":"+PORT_SSE+PATH;
                System.out.println("URL  :" + url);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        try {
            SseEventSource source = SseEventSource.target(target).reconnectingEvery(5,TimeUnit.SECONDS).build();
            source.register((sseEvent)
                            -> {
                        System.out.println("EVENT : " + sseEvent.readData());
                    },
                    (e) -> e.printStackTrace());

            source.open();
            System.out.println("Source OPENED " + source.isOpen());
        } catch (Exception e) {
            // falls through
            e.printStackTrace();
        }
        return Response.ok("Event => OK").build();
    }

    @Path("connect")
    @GET
    public Response connect2() {

        Client client = ClientBuilder.newClient();
        WebTarget wt = client.target("http://" + HOST_SSE + ":" + PORT_SSE + PATH);
        SseEventSource source = SseEventSource.target(wt).reconnectingEvery(5, TimeUnit.SECONDS).build();

        source.register(inboundSseEvent -> {
                    System.out.println(inboundSseEvent.readData());
                }
        );
        source.open();
        System.out.println("Source OPENED " + source.isOpen());

        Runtime runtime = Runtime.getRuntime();
        JsonObject json = Json.createObjectBuilder()
                .add("processors", runtime.availableProcessors())
                .add("memory", runtime.freeMemory())
                .build();

        return Response.ok(json).build();
    }
}
