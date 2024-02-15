package app.bpartners.geojobs.repository.model.detection;

import static app.bpartners.geojobs.repository.model.GeoJobType.DETECTION;
import static org.hibernate.type.SqlTypes.JSON;

import app.bpartners.geojobs.job.model.Task;
import app.bpartners.geojobs.repository.model.GeoJobType;
import app.bpartners.geojobs.repository.model.tiling.Tile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"status"})
public class DetectionTask extends Task implements Serializable {
  @JdbcTypeCode(JSON)
  private Tile tile;

  @Override
  public GeoJobType getJobType() {
    return DETECTION;
  }
}
