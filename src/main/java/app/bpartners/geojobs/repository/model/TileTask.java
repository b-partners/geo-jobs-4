package app.bpartners.geojobs.repository.model;

import app.bpartners.geojobs.repository.model.tiling.Tile;
import lombok.*;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TileTask {
  private String taskId;
  private String jobId;
  private Tile tile;
}
