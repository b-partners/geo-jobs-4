package app.bpartners.geojobs.repository.model.geo.tiling;

import static app.bpartners.geojobs.repository.model.geo.JobType.TILING;

import app.bpartners.geojobs.repository.model.Job;
import app.bpartners.geojobs.repository.model.geo.JobType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@JsonIgnoreProperties({"status", "done"})
public class ZoneTilingJob extends Job<TilingTask> implements Serializable {
  @Override
  public JobType getJobType() {
    return TILING;
  }
}
