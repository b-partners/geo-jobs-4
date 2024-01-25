package app.bpartners.geojobs.endpoint.rest.controller.mapper;

import static java.time.Instant.now;

import app.bpartners.geojobs.endpoint.rest.model.Feature;
import app.bpartners.geojobs.endpoint.rest.model.GeoServerParameter;
import app.bpartners.geojobs.repository.model.ZoneTilingTask;
import app.bpartners.geojobs.repository.model.geo.Parcel;
import java.net.URL;
import org.springframework.stereotype.Component;

@Component
public class FeatureMapper {
  public Parcel toDomain(Feature rest, URL geoServerUrl, GeoServerParameter GeoServerParameter) {
    return Parcel.builder()
        .id(rest.getId())
        .feature(rest)
        .geoServerUrl(geoServerUrl)
        .geoServerParameter(GeoServerParameter)
        .creationDatetime(String.valueOf(now()))
        .build();
  }

  public Feature fromZoneTilingTask(ZoneTilingTask domainTask) {
    return domainTask.getParcel().getFeature();
  }
}
