package app.bpartners.geojobs.service;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.UNKNOWN;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.PENDING;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.bpartners.geojobs.endpoint.event.gen.ImportedZoneTilingJobSaved;
import app.bpartners.geojobs.endpoint.rest.model.GeoServerParameter;
import app.bpartners.geojobs.endpoint.rest.model.TileCoordinates;
import app.bpartners.geojobs.file.BucketCustomizedComponent;
import app.bpartners.geojobs.job.model.JobStatus;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.TilingTaskRepository;
import app.bpartners.geojobs.repository.model.tiling.Tile;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.event.ImportedZoneTilingJobSavedService;
import app.bpartners.geojobs.service.tiling.ZoneTilingJobService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.s3.model.S3Object;

public class ImportedZoneTilingJobSavedServiceTest {
  BucketCustomizedComponent bucketCustomizedComponentMock = mock();
  ZoneTilingJobService tilingJobServiceMock = mock();
  TilingTaskRepository tilingTaskRepositoryMock = mock();
  ImportedZoneTilingJobSavedService subject =
      new ImportedZoneTilingJobSavedService(
          bucketCustomizedComponentMock, tilingJobServiceMock, tilingTaskRepositoryMock);

  @Test
  void accept_all_ok() {
    Long startFrom = 0L;
    Long endAt = null;
    String jobId = "jobId";
    String dummyBucketName = "dummyBucketName";
    String dummyBucketPrefix = "dummyBucketPrefix";
    GeoServerParameter geoServerParameter = new GeoServerParameter();
    String dummyGeoServerUrl = "https://dummyGeoServerUrl.com";
    List<S3Object> s3Objects =
        List.of(
            S3Object.builder().key("s3ObjectKeyLayer1/20/100/200").build(),
            S3Object.builder().key("s3ObjectKeyLayer2/20/100/200").build());
    when(tilingJobServiceMock.findById(jobId))
        .thenReturn(
            ZoneTilingJob.builder()
                .id(jobId)
                .zoneName("dummyZoneName")
                .emailReceiver("dummyEmailReceiver")
                .statusHistory(
                    List.of(
                        JobStatus.builder()
                            .progression(PENDING)
                            .health(UNKNOWN)
                            .creationDatetime(now())
                            .build()))
                .build());

    when(bucketCustomizedComponentMock.listObjects(dummyBucketName, dummyBucketPrefix))
        .thenReturn(s3Objects);

    subject.accept(
        new ImportedZoneTilingJobSaved(
            startFrom,
            endAt,
            jobId,
            dummyBucketName,
            dummyBucketPrefix,
            geoServerParameter,
            dummyGeoServerUrl));

    var listCaptor = ArgumentCaptor.forClass(List.class);
    verify(tilingTaskRepositoryMock, times(1)).saveAll(listCaptor.capture());
    List<TilingTask> savedTilingTasks = (List<TilingTask>) listCaptor.getValue();
    var firstTask = savedTilingTasks.getFirst();
    var lastTask = savedTilingTasks.getLast();
    assertEquals(2, savedTilingTasks.size());
    Tile firstTile = firstTask.getParcelContent().getFirstTile();
    Tile lastTile = lastTask.getParcelContent().getFirstTile();
    assertEquals(s3Objects.size(), savedTilingTasks.size());
    assertTrue(savedTilingTasks.stream().allMatch(TilingTask::isSucceeded));
    assertEquals(
        Tile.builder()
            .id(firstTile.getId())
            .creationDatetime(firstTile.getCreationDatetime())
            .bucketPath("s3ObjectKeyLayer1/20/100/200")
            .coordinates(new TileCoordinates().x(100).y(200).z(20))
            .build(),
        firstTile);
    assertEquals(
        Tile.builder()
            .id(lastTile.getId())
            .creationDatetime(lastTile.getCreationDatetime())
            .bucketPath("s3ObjectKeyLayer2/20/100/200")
            .coordinates(new TileCoordinates().x(100).y(200).z(20))
            .build(),
        lastTile);
  }

  @Test
  void accept_truncated_ok() {
    Long startFrom = 0L;
    Long endAt = 1L;
    String jobId = "jobId";
    String dummyBucketName = "dummyBucketName";
    String dummyBucketPrefix = "dummyBucketPrefix";
    GeoServerParameter geoServerParameter = new GeoServerParameter();
    String dummyGeoServerUrl = "https://dummyGeoServerUrl.com";
    List<S3Object> s3Objects =
        List.of(
            S3Object.builder().key("s3ObjectKeyLayer1/20/100/200").build(),
            S3Object.builder().key("s3ObjectKeyLayer2/20/100/200").build());
    when(tilingJobServiceMock.findById(jobId))
        .thenReturn(
            ZoneTilingJob.builder()
                .id(jobId)
                .zoneName("dummyZoneName")
                .emailReceiver("dummyEmailReceiver")
                .statusHistory(
                    List.of(
                        JobStatus.builder()
                            .progression(PENDING)
                            .health(UNKNOWN)
                            .creationDatetime(now())
                            .build()))
                .build());

    when(bucketCustomizedComponentMock.listObjects(dummyBucketName, dummyBucketPrefix))
        .thenReturn(s3Objects);

    subject.accept(
        new ImportedZoneTilingJobSaved(
            startFrom,
            endAt,
            jobId,
            dummyBucketName,
            dummyBucketPrefix,
            geoServerParameter,
            dummyGeoServerUrl));

    var listCaptor = ArgumentCaptor.forClass(List.class);
    verify(tilingTaskRepositoryMock, times(1)).saveAll(listCaptor.capture());
    List<TilingTask> savedTilingTasks = (List<TilingTask>) listCaptor.getValue();
    var firstTask = savedTilingTasks.getFirst();
    assertEquals(1, savedTilingTasks.size());
    Tile firstTile = firstTask.getParcelContent().getFirstTile();
    assertEquals(1, savedTilingTasks.size());
    assertTrue(savedTilingTasks.stream().allMatch(TilingTask::isSucceeded));
    assertEquals(
        Tile.builder()
            .id(firstTile.getId())
            .creationDatetime(firstTile.getCreationDatetime())
            .bucketPath("s3ObjectKeyLayer1/20/100/200")
            .coordinates(new TileCoordinates().x(100).y(200).z(20))
            .build(),
        firstTile);
  }

  @Test
  void convert_tiles_from_bucket_path_ko() {
    assertThrows(ApiException.class, () -> subject.fromBucketPathKey("dummyBucket"));
  }
}
