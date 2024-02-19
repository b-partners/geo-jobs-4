package app.bpartners.geojobs.repository.model.tiling;

import static app.bpartners.geojobs.repository.model.GeoJobType.TILING;
import static org.hibernate.type.SqlTypes.JSON;

import app.bpartners.geojobs.job.model.Task;
import app.bpartners.geojobs.repository.model.GeoJobType;
import app.bpartners.geojobs.repository.model.Parcel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@JsonIgnoreProperties({"status"})
public class TilingTask extends Task implements Serializable {
  @JdbcTypeCode(JSON)
  private Parcel parcel;

  @Override
  public GeoJobType getJobType() {
    return TILING;
  }

  @Override
  public String toString() {
    return "TilingTask{" + "parcel=" + parcel + ", status=" + getStatus() + '}';
  }
}
