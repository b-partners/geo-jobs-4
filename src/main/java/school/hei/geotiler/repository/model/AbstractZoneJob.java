package school.hei.geotiler.repository.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.SELECT;
import static school.hei.geotiler.repository.model.types.PostgresEnumType.PGSQL_ENUM_NAME;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.TypeDef;
import school.hei.geotiler.repository.model.types.PostgresEnumType;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@MappedSuperclass
@TypeDef(name = PGSQL_ENUM_NAME, typeClass = PostgresEnumType.class)
public class AbstractZoneJob<S extends Status, T> implements Serializable {
  @Id private String id;
  private String zoneName;
  private String emailReceiver;
  @CreationTimestamp private Instant submissionInstant;

  // note(LazyInitializationException): thrown when fetch type is LAZY, hence using EAGER
  @OneToMany(cascade = ALL, mappedBy = "jobId", fetch = EAGER)
  @Fetch(SELECT)
  private List<S> statusHistory;

  // note(LazyInitializationException)
  @OneToMany(mappedBy = "jobId", cascade = ALL, fetch = EAGER)
  @Fetch(SELECT)
  private List<T> tasks = new ArrayList<>();

  public S getStatus() {
    return null;
  }

  public void addStatus(S status) {}
}
