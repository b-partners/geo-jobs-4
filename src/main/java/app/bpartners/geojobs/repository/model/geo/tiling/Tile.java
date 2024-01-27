package app.bpartners.geojobs.repository.model.geo.tiling;

import app.bpartners.geojobs.endpoint.rest.model.TileCoordinates;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tile implements Serializable {
  private String id;
  private String creationDatetime;
  private TileCoordinates coordinates;
  private String bucketPath;
}
