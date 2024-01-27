package app.bpartners.geojobs.api;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.file.BucketComponent;
import app.bpartners.geojobs.repository.model.geo.detection.DetectionTask;
import app.bpartners.geojobs.repository.model.geo.tiling.Tile;
import app.bpartners.geojobs.service.geo.detection.DetectionResponse;
import app.bpartners.geojobs.service.geo.detection.TilesDetectionApi;
import java.io.File;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class TilesDetectionApiIT extends FacadeIT {
  private static final String FILE_NAME =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "mockData"
          + File.separator
          + "image-to-detect.jpg";
  @MockBean BucketComponent bucketComponent;
  @Autowired TilesDetectionApi tilesDetectionApi;

  @Test
  public void process_detection_ok() {
    DetectionResponse actual = tilesDetectionApi.detect(detectionTask());

    assertNotNull(actual);
    assertNotNull(actual.getRstImageUrl());
    assertNotNull(actual.getSrcImageUrl());
    assertNotNull(actual.getRstRaw());
  }

  public DetectionTask detectionTask() {
    when(bucketComponent.download(any())).thenReturn(new File(FILE_NAME));

    return DetectionTask.builder()
        .id(String.valueOf(randomUUID()))
        .jobId(String.valueOf(randomUUID()))
        .submissionInstant(Instant.now())
        .tile(
            Tile.builder()
                .id(String.valueOf(randomUUID()))
                .bucketPath(String.valueOf(randomUUID()))
                .build())
        .build();
  }
}
