package app.bpartners.geojobs.repository.model;

import static app.bpartners.geojobs.repository.model.JobType.TILING;
import static app.bpartners.geojobs.repository.model.types.PostgresTypes.JSONB;

import app.bpartners.geojobs.repository.model.geo.Parcel;
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
public class ZoneTilingTask extends ZoneTask implements Serializable {
  @Type(type = JSONB)
  @Column(columnDefinition = JSONB)
  private Parcel parcel;

  @Override
  public JobType getJobType() {
    return TILING;
  }
}
