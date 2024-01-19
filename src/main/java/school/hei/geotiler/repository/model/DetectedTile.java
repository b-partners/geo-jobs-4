package school.hei.geotiler.repository.model;

import static javax.persistence.CascadeType.ALL;
import static school.hei.geotiler.repository.model.types.PostgresTypes.JSONB;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@TypeDef(name = JSONB, typeClass = JsonBinaryType.class)
public class DetectedTile implements Serializable {
  @Id private String id;

  @Type(type = JSONB)
  @Column(columnDefinition = JSONB)
  private Tile tile;

  @CreationTimestamp private Instant creationDatetime;

  @OneToMany(cascade = ALL, mappedBy = "detectedTileId")
  private List<DetectedObject> detectedObjects;

  private String bucketPath;
}
