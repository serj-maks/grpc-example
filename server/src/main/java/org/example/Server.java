package org.example;

import io.grpc.stub.StreamObserver;
import org.example.serverproto.ServerGrpc;
import org.example.serverproto.ServerPropertyName;
import org.example.serverproto.ServerPropertyValue;

import java.util.logging.Logger;

public class Server extends ServerGrpc.ServerImplBase {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public Server() {

    }

    @Override
    public void getProperty(ServerPropertyName request, StreamObserver<ServerPropertyValue> streamObserver) {
        String propertyName = request.getPropertyName();
        String propertyValue = System.getProperty(propertyName);
        ServerPropertyValue value = ServerPropertyValue.newBuilder()
                .setPropertyValue(propertyValue)
                .build();
        streamObserver.onNext(value);
        streamObserver.onCompleted();
    }
}
