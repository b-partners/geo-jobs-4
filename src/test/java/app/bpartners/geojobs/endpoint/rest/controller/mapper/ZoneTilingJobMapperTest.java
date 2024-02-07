package app.bpartners.geojobs.endpoint.rest.controller.mapper;

import static app.bpartners.geojobs.endpoint.rest.model.CreateZoneTilingJob.ZoomLevelEnum.HOUSES_0;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import app.bpartners.geojobs.endpoint.rest.model.CreateZoneTilingJob;
import app.bpartners.geojobs.endpoint.rest.model.Feature;
import app.bpartners.geojobs.repository.model.geo.ArcgisImageZoom;
import java.util.List;
import org.junit.jupiter.api.Test;

class ZoneTilingJobMapperTest {

  FeatureMapper featureMapper = new FeatureMapper();
  ZoneTilingJobMapper subject =
      new ZoneTilingJobMapper(new TilingTaskMapper(featureMapper, mock()), featureMapper, mock());

  @Test
  void restJobZoomLevel_overrides_restJobFeatureZoom() {
    var rest = new CreateZoneTilingJob();
    rest.setZoomLevel(HOUSES_0);
    var feature = new Feature();
    feature.setZoom(17);
    rest.setFeatures(List.of(feature));
    rest.setGeoServerUrl("https://geoserver.url");

    var actual = subject.toDomain(rest);

    assertEquals(
        ArcgisImageZoom.valueOf(HOUSES_0.name()).getZoomLevel(),
        actual.getTasks().get(0).getParcel().getFeature().getZoom());
  }
}
