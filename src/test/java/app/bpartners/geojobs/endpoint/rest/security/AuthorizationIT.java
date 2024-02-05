package app.bpartners.geojobs.endpoint.rest.security;

import static java.net.http.HttpClient.newHttpClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.endpoint.rest.api.DetectionApi;
import app.bpartners.geojobs.endpoint.rest.api.TilingApi;
import app.bpartners.geojobs.endpoint.rest.client.ApiClient;
import app.bpartners.geojobs.endpoint.rest.client.ApiException;
import app.bpartners.geojobs.endpoint.rest.model.CreateZoneTilingJob;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

public class AuthorizationIT extends FacadeIT {

  @LocalServerPort private int port;

  @Autowired ObjectMapper om;

  TilingApi tilingApi;
  DetectionApi detectionApi;

  @BeforeEach
  void setUp() {
    var client = new ApiClient();
    client.setScheme("http");
    client.setPort(port);
    client.setObjectMapper(om);

    tilingApi = new TilingApi(client);
    detectionApi = new DetectionApi(client);
  }

  @Test
  void anonymous_can_ping() throws URISyntaxException, IOException, InterruptedException {
    var pongResponse =
        newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("http://localhost:" + port + "/ping"))
                    .build(),
                BodyHandlers.ofString());
    assertEquals("pong", pongResponse.body());
  }

  @Test
  void anonymous_cannot_tile() {
    var e = assertThrows(ApiException.class, () -> tilingApi.getTilingJobs(1, 2));
    assertEquals("getTilingJobs call failed with: 401 - [no body]", e.getMessage());

    e = assertThrows(ApiException.class, () -> tilingApi.getZTJParcels("dummy"));
    assertEquals("getZTJParcels call failed with: 401 - [no body]", e.getMessage());

    e = assertThrows(ApiException.class, () -> tilingApi.tileZone(mock(CreateZoneTilingJob.class)));
    assertEquals("tileZone call failed with: 401 - [no body]", e.getMessage());
  }

  @Test
  void anonymous_cannot_detect() {
    var e = assertThrows(ApiException.class, () -> detectionApi.getDetectionJobs(1, 2));
    assertEquals("getDetectionJobs call failed with: 401 - [no body]", e.getMessage());

    e = assertThrows(ApiException.class, () -> detectionApi.getZDJGeojsonsUrl("dummy"));
    assertEquals("getZDJGeojsonsUrl call failed with: 401 - [no body]", e.getMessage());
  }
}
