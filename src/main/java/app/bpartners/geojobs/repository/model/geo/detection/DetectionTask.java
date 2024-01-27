package app.bpartners.geojobs.repository.model.geo.detection;

import static app.bpartners.geojobs.repository.model.geo.JobType.DETECTION;
import static app.bpartners.geojobs.repository.model.types.PostgresTypes.JSONB;

import app.bpartners.geojobs.repository.model.Task;
import app.bpartners.geojobs.repository.model.geo.JobType;
import app.bpartners.geojobs.repository.model.geo.tiling.Tile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@TypeDef(name = JSONB, typeClass = JsonBinaryType.class)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"status"})
public class DetectionTask extends Task implements Serializable {
  @Type(type = JSONB)
  @Column(columnDefinition = JSONB)
  private Tile tile;

  @Override
  public JobType getJobType() {
    return DETECTION;
  }
}
