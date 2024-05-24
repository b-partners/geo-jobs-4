package app.bpartners.geojobs.service.event;

import static app.bpartners.geojobs.job.model.Status.HealthStatus.SUCCEEDED;
import static app.bpartners.geojobs.job.model.Status.ProgressionStatus.FINISHED;
import static app.bpartners.geojobs.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static app.bpartners.geojobs.repository.model.GeoJobType.TILING;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

import app.bpartners.geojobs.endpoint.event.gen.ImportedZoneTilingJobSaved;
import app.bpartners.geojobs.endpoint.rest.model.GeoServerParameter;
import app.bpartners.geojobs.endpoint.rest.model.TileCoordinates;
import app.bpartners.geojobs.file.BucketCustomizedComponent;
import app.bpartners.geojobs.job.model.TaskStatus;
import app.bpartners.geojobs.model.exception.ApiException;
import app.bpartners.geojobs.repository.TilingTaskRepository;
import app.bpartners.geojobs.repository.model.Parcel;
import app.bpartners.geojobs.repository.model.ParcelContent;
import app.bpartners.geojobs.repository.model.tiling.Tile;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import app.bpartners.geojobs.service.tiling.ZoneTilingJobService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@AllArgsConstructor
@Slf4j
public class ImportedZoneTilingJobSavedService implements Consumer<ImportedZoneTilingJobSaved> {
  private final BucketCustomizedComponent bucketCustomizedComponent;
  private final ZoneTilingJobService tilingJobService;
  private final TilingTaskRepository tilingTaskRepository;

  @Override
  @Transactional
  public void accept(ImportedZoneTilingJobSaved importedZoneTilingJobSaved) {
    var job = tilingJobService.findById(importedZoneTilingJobSaved.getJobId());
    var bucketName = importedZoneTilingJobSaved.getBucketName();
    var bucketPathPrefix = importedZoneTilingJobSaved.getBucketPathPrefix();
    var geoServerParameter = importedZoneTilingJobSaved.getGeoServerParameter();
    var geoServerUrlValue = importedZoneTilingJobSaved.getGeoServerUrl();
    List<S3Object> s3Objects =
        getS3Objects(importedZoneTilingJobSaved, bucketName, bucketPathPrefix);

    log.info("[DEBUG] S3 objects size {}", s3Objects.size());
    var tilingTasks =
        s3Objects.stream()
            .map(s3Object -> getFinishedTasks(s3Object, job, geoServerParameter, geoServerUrlValue))
            .toList();
    log.info(
        "[DEBUG] TilingTasks size {}, values {}",
        tilingTasks.size(),
        tilingTasks.stream().map(TilingTask::describe).toList());
    var savedTilingTasks = tilingTaskRepository.saveAll(tilingTasks);
    log.info(
        "[DEBUG] Saved TilingTasks size {}, values {}",
        savedTilingTasks.size(),
        savedTilingTasks.stream().map(TilingTask::describe).toList());
    var savedJob = tilingJobService.recomputeStatus(job);
    log.info("[DEBUG] Saved ZoneTilingJob {}", savedJob);
  }

  private List<S3Object> getS3Objects(
      ImportedZoneTilingJobSaved importedZoneTilingJobSaved,
      String bucketName,
      String bucketPathPrefix) {
    var defaultS3Objects = bucketCustomizedComponent.listObjects(bucketName, bucketPathPrefix);
    var startFromValue = importedZoneTilingJobSaved.getStartFrom();
    var endAtValue = importedZoneTilingJobSaved.getEndAt();
    long startFrom = startFromValue == null ? 0L : startFromValue;
    long endAt = endAtValue == null ? defaultS3Objects.size() : endAtValue;
    if (defaultS3Objects.size() > startFrom || endAt < defaultS3Objects.size()) {
      return new ArrayList<>(defaultS3Objects.subList((int) startFrom, (int) endAt));
    }
    return defaultS3Objects;
  }

  private TilingTask getFinishedTasks(
      S3Object s3Object,
      ZoneTilingJob job,
      GeoServerParameter geoServerParameter,
      String geoServerUrlValue) {
    try {
      String jobId = job.getId();
      URL geoServerUrl = new URL(geoServerUrlValue);
      String taskId = randomUUID().toString();
      String bucketPathKey = s3Object.key();
      return TilingTask.builder()
          .id(taskId)
          .jobId(jobId)
          .statusHistory(
              List.of(
                  TaskStatus.builder()
                      .id(randomUUID().toString())
                      .taskId(taskId)
                      .jobType(TILING)
                      .health(SUCCEEDED)
                      .progression(FINISHED)
                      .creationDatetime(now())
                      .build()))
          .submissionInstant(now())
          .parcels(
              List.of(
                  Parcel.builder()
                      .id(randomUUID().toString())
                      .parcelContent(
                          ParcelContent.builder()
                              .id(randomUUID().toString())
                              .feature(null) // TODO: distinct for each parcels
                              .creationDatetime(now())
                              .geoServerParameter(geoServerParameter)
                              .geoServerUrl(geoServerUrl)
                              .tiles(
                                  List.of(
                                      Tile.builder()
                                          .id(randomUUID().toString())
                                          .bucketPath(bucketPathKey)
                                          .coordinates(fromBucketPathKey(bucketPathKey))
                                          .creationDatetime(now())
                                          .build()))
                              .build())
                      .build()))
          .build();
    } catch (MalformedURLException e) {
      throw new ApiException(SERVER_EXCEPTION, e);
    }
  }

  public TileCoordinates fromBucketPathKey(String bucketPathKey) {
    String[] bucketPathValues = bucketPathKey.split("/");
    if (bucketPathValues.length != 4) {
      throw new ApiException(
          SERVER_EXCEPTION,
          "Unable to convert bucketPathKey " + bucketPathKey + " to TilesCoordinates");
    }
    String xValue = bucketPathValues[2];
    String yValue = bucketPathValues[3].split(".jpg")[0];
    String zValue = bucketPathValues[1];
    return new TileCoordinates()
        .x(Integer.valueOf(xValue))
        .y(Integer.valueOf(yValue))
        .z(Integer.valueOf(zValue));
  }
}
