package org.example;

import io.grpc.stub.StreamObserver;
import org.example.serverproto.*;

import java.util.HashMap;
import java.util.Map;
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

    @Override
    public StreamObserver<SystemPropertyName> getClientStreamingProperties(
            StreamObserver<SystemProperties> observer) {

        return new StreamObserver<SystemPropertyName>() {

            private Map<String, String> properties = new HashMap<String, String>();

            @Override
            public void onNext(SystemPropertyName spn) {
                String pName = spn.getPropertyName();
                String pValue = System.getProperty(pName);
                logger.info("client streaming received property: " + pName);
                properties.put(pName, pValue);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                SystemProperties value = SystemProperties.newBuilder()
                        .putAllProperties(properties)
                        .build();
                observer.onNext(value);
                observer.onCompleted();
                logger.info("client streaming was completed!");
            }
        };
    }
}
