package app.bpartners.geojobs.service.geo.detection;

import app.bpartners.geojobs.file.BucketComponent;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.model.geo.detection.DetectionTask;
import app.bpartners.geojobs.repository.model.geo.tiling.Tile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Base64;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class TilesDetectionApi {
  private final String tileDetectionBaseUrl;
  private final ObjectMapper om;
  private final BucketComponent bucketComponent;

  public TilesDetectionApi(
      @Value("${tile.detection.api.url}") String apiUrl,
      ObjectMapper objectMapper,
      BucketComponent bucket) {
    tileDetectionBaseUrl = apiUrl;
    om = objectMapper;
    bucketComponent = bucket;
  }

  @SneakyThrows
  public DetectionResponse detect(DetectionTask task) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Tile tile = task.getTile();
    File file = bucketComponent.download(tile.getBucketPath());
    String base64ImgData = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));

    var payload =
        DetectionPayload.builder()
            .projectName(task.getJobId())
            .fileName(file.getName())
            .base64ImgData(base64ImgData)
            .build();
    String requestBody = om.writeValueAsString(payload);

    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(tileDetectionBaseUrl + "/detection");
    ResponseEntity<DetectionResponse> responseEntity =
        restTemplate.postForEntity(builder.toUriString(), request, DetectionResponse.class);

    if (responseEntity.getStatusCode().value() == 200) {
      log.info("Response data {}", responseEntity.getBody());
      return responseEntity.getBody();
    }
    throw new ApiException(ApiException.ExceptionType.SERVER_EXCEPTION, "Server error");
  }
}
