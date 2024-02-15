package app.bpartners.geojobs.service.detection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class DetectionPayload {
  @JsonProperty("projectname")
  private String projectName;

  @JsonProperty("filename")
  private String fileName;

  @JsonProperty("base64_img_data")
  private String base64ImgData;
}
