package school.hei.geotiler.repository.model;

import static school.hei.geotiler.repository.model.types.PostgresEnumType.PGSQL_ENUM_NAME;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import school.hei.geotiler.repository.model.types.PostgresEnumType;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@TypeDef(name = PGSQL_ENUM_NAME, typeClass = PostgresEnumType.class)
public class DetectableObjectType implements Serializable {
  @Id private String id;

  @JoinColumn(referencedColumnName = "id")
  private String objectId;

  @Enumerated(EnumType.STRING)
  @Type(type = PGSQL_ENUM_NAME)
  private DetectableType detectableType;

  public enum DetectableType {
    ROOF,
    SOLAR_PANEL,
    POOL,
    PATHWAY,
    TREE
  }
}
