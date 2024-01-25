package app.bpartners.geojobs.service.event;

import app.bpartners.geojobs.endpoint.event.gen.ZoneTilingJobCreated;
import app.bpartners.geojobs.service.ZoneTilingJobService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZoneTilingJobCreatedService implements Consumer<ZoneTilingJobCreated> {
  private final ZoneTilingJobService zoneTilingJobService;

  @Override
  public void accept(ZoneTilingJobCreated zoneTilingJobCreated) {
    zoneTilingJobService.process(zoneTilingJobCreated.getZoneTilingJob());
  }
}
