package org.example;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private static final String PORT = System.getProperty("http.port", "9081");
    private static final String URL = "http://localhost:" + PORT + "/";

    private static Client client;

    @BeforeAll
    public static void setUp() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void tearDown() {
        client.close();
    }

    @Test
    @DisplayName("should return code 200")
    void getPropertiesString_shouldReturn200() {
        WebTarget target = client.target(URL + "client/properties/os.name");
        Response response = target.request().get();
        assertEquals(200, response.getStatus(), "incorrect response code from " + target.getUri().getPath()); response.close();
        response.close();
    }

    @Test
    @DisplayName("should return false, if we check entity.isEmpty()")
    void getPropertiesString_shouldNotBeEmpty() {
        WebTarget target = client.target(URL + "client/properties/os.name");
        Response response = target.request().get();
        assertFalse(response.readEntity(String.class).isEmpty(), "response should not be empty");
        response.close();
    }
}
