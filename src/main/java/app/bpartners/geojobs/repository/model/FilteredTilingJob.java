package app.bpartners.geojobs.repository.model;

import app.bpartners.geojobs.repository.model.tiling.ZoneTilingJob;
import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class FilteredTilingJob {
  private String initialJobId;
  private ZoneTilingJob succeededJob;
  private ZoneTilingJob notSucceededJob;
}
