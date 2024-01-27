package app.bpartners.geojobs.repository.model;

import static javax.persistence.CascadeType.ALL;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ZoneDetectionJob extends ZoneJob<ZoneDetectionTask> implements Serializable {
  @OneToOne(cascade = ALL)
  private ZoneTilingJob zoneTilingJob;
}
