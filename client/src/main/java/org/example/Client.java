package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.example.serverproto.*;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        ServerGrpc.ServerBlockingStub server = ServerGrpc.newBlockingStub(channel);
        ServerPropertyName request = ServerPropertyName.newBuilder()
                .setPropertyName(property)
                .build();
        ServerPropertyValue response = server.getProperty(request);
        channel.shutdown();
        return response.getPropertyValue();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Properties getUserProperties() {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(SYSTEM_HOST, SYSTEM_PORT)
                .usePlaintext().build();
        ServerGrpc.ServerStub client = ServerGrpc.newStub(channel);
        CountDownLatch countDown = new CountDownLatch(1);
        Properties properties = new Properties();

        StreamObserver<SystemPropertyName> stream = client.getClientStreamingProperties(
                new StreamObserver<SystemProperties>() {

                    @Override
                    public void onNext(SystemProperties value) {
                        logger.info("client streaming received a map that has "
                                + value.getPropertiesCount() + " properties");
                        properties.putAll(value.getPropertiesMap());
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        logger.info("client streaming completed");
                        countDown.countDown();
                    }
                });

        // collect the property names starting with user.
        List<String> keys = System.getProperties().stringPropertyNames().stream()
                .filter(k -> k.startsWith("user."))
                .collect(Collectors.toList());

        // send messages to the server
        keys.stream()
                .map(k -> SystemPropertyName.newBuilder().setPropertyName(k).build())
                .forEach(stream::onNext);
        stream.onCompleted();

        // wait until completed
        try {
            countDown.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        channel.shutdownNow();

        return properties;
    }
}
