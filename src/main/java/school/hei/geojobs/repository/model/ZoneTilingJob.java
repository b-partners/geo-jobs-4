package school.hei.geojobs.repository.model;

import static java.util.stream.Collectors.toList;

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
public class ZoneTilingJob extends AbstractZoneJob<TilingJobStatus, ZoneTilingTask>
    implements Serializable {

  public TilingJobStatus getStatus() {
    return TilingJobStatus.from(
        this.getId(),
        Status.reduce(
            this.getStatusHistory().stream().map(status -> (Status) status).collect(toList())));
  }

  public void addStatus(TilingJobStatus status) {
    this.getStatusHistory().add(status);
  }
}
