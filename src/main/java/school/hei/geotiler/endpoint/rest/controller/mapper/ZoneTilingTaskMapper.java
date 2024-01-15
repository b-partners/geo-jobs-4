package school.hei.geotiler.endpoint.rest.controller.mapper;

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static school.hei.geotiler.endpoint.rest.controller.mapper.StatusMapper.toHealthStatus;
import static school.hei.geotiler.endpoint.rest.controller.mapper.StatusMapper.toProgressionEnum;
import static school.hei.geotiler.repository.model.Status.HealthStatus.UNKNOWN;
import static school.hei.geotiler.repository.model.Status.ProgressionStatus.PENDING;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.geotiler.endpoint.rest.model.Feature;
import school.hei.geotiler.endpoint.rest.model.GeoServerParameter;
import school.hei.geotiler.endpoint.rest.model.Parcel;
import school.hei.geotiler.endpoint.rest.model.Status;
import school.hei.geotiler.endpoint.rest.model.Tile;
import school.hei.geotiler.repository.ZoneTilingJobRepository;
import school.hei.geotiler.repository.model.TaskStatus;
import school.hei.geotiler.repository.model.ZoneTilingJob;
import school.hei.geotiler.repository.model.ZoneTilingTask;

@Component
@AllArgsConstructor
public class ZoneTilingTaskMapper {
  private final FeatureMapper featureMapper;
  private final ZoneTilingJobRepository zoneTilingJobRepository;

  public ZoneTilingTask from(
      Feature createFeature, URL geoServerUrl, GeoServerParameter geoServerParameter, UUID jobId) {
    String generatedId = randomUUID().toString();
    return ZoneTilingTask.builder()
        .id(generatedId)
        .jobId(jobId.toString())
        .statusHistory(
            List.of(
                TaskStatus.builder()
                    .health(UNKNOWN)
                    .progression(PENDING)
                    .creationDatetime(now())
                    .taskId(generatedId)
                    .build()))
        .submissionInstant(now())
        .parcel(featureMapper.toDomain(createFeature, geoServerUrl, geoServerParameter))
        .build();
  }

  public Parcel toRest(school.hei.geotiler.repository.model.geo.Parcel model, String jobId) {
    ZoneTilingJob zoneTilingJob = zoneTilingJobRepository.findById(jobId).get();
    return new Parcel()
        .id(UUID.randomUUID().toString())
        .tiles(
            ofNullable(model.getTiles())
                .map(tiles -> tiles.stream().map(this::toRest).toList())
                .orElse(null))
        .tilingStatus(
            ofNullable(zoneTilingJob.getStatus())
                .map(
                    status ->
                        new Status()
                            .health(toHealthStatus(status.getHealth()))
                            .progression(toProgressionEnum(status.getProgression()))
                            .creationDatetime(status.getCreationDatetime()))
                .orElse(null))
        .feature(model.getFeature());
  }

  public Tile toRest(school.hei.geotiler.repository.model.Tile model) {
    return new Tile()
        .id(model.getId())
        .coordinates(model.getCoordinates())
        .creationDatetime(Instant.parse(model.getCreationDatetime()))
        .bucketPath(model.getBucketPath());
  }
}
