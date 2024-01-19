package school.hei.geojobs.service.event;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geojobs.endpoint.event.gen.ZoneTilingJobCreated;
import school.hei.geojobs.service.ZoneTilingJobService;

@Service
@AllArgsConstructor
public class ZoneTilingJobCreatedService implements Consumer<ZoneTilingJobCreated> {
  private final ZoneTilingJobService zoneTilingJobService;

  @Override
  public void accept(ZoneTilingJobCreated zoneTilingJobCreated) {
    zoneTilingJobService.process(zoneTilingJobCreated.getZoneTilingJob());
  }
}
