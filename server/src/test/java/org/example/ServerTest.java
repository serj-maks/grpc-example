package org.example;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.example.serverproto.ServerGrpc;
import org.example.serverproto.ServerPropertyName;
import org.example.serverproto.ServerPropertyValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.grpc.Server;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private static final String SERVER_NAME = "server";

    private static Server inProcessServer;
    private static ManagedChannel inProcessChannel;
    private static ServerGrpc.ServerBlockingStub blockingStub;

    @BeforeAll
    public static void setUp() {
        inProcessServer = InProcessServerBuilder.forName(SERVER_NAME)
                .addService(new org.example.Server())
                .directExecutor()
                .build();
        try {
            inProcessServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        inProcessChannel = InProcessChannelBuilder.forName(SERVER_NAME)
                .directExecutor()
                .build();
        blockingStub = ServerGrpc.newBlockingStub(inProcessChannel);
    }

    @AfterAll
    public static void tearDown() {
        inProcessChannel.shutdown();
        inProcessServer.shutdown();
    }

    @Test
    @DisplayName("should return os.name value")
    void getProperty() {
       ServerPropertyName request = ServerPropertyName.newBuilder()
                .setPropertyName("os.name")
                .build();
        ServerPropertyValue response = blockingStub.getProperty(request);
        assertEquals(System.getProperty("os.name"), response.getPropertyValue());
    }
}
