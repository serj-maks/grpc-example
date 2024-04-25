package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.serverproto.ServerGrpc;
import org.example.serverproto.ServerPropertyName;
import org.example.serverproto.ServerPropertyValue;

import java.util.logging.Logger;

@ApplicationScoped
@Path("/properties")
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    @Inject
    @ConfigProperty(name = "system.host", defaultValue = "localhost")
    String SYSTEM_HOST;

    @Inject
    @ConfigProperty(name = "system.port", defaultValue = "9080")
    int SYSTEM_PORT;

    @GET
    @Path("/{property}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPropertiesString(@PathParam("property") String property) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(SYSTEM_HOST, SYSTEM_PORT)
                .usePlaintext()
                .build();
        ServerGrpc.ServerBlockingStub client = ServerGrpc.newBlockingStub(channel);
        ServerPropertyName request = ServerPropertyName.newBuilder()
                .setPropertyName(property)
                .build();
        ServerPropertyValue response = client.getProperty(request);
        channel.shutdown();
        return response.getPropertyValue();
    }
}
