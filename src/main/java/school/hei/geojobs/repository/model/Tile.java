package school.hei.geojobs.repository.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import school.hei.geojobs.endpoint.rest.model.TileCoordinates;

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
