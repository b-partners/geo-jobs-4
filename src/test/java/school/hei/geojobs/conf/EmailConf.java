package school.hei.geojobs.conf;

import org.springframework.test.context.DynamicPropertyRegistry;
import school.hei.geojobs.PojaGenerated;

@PojaGenerated
public class EmailConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.ses.source", () -> "dummy-ses-source");
  }
}
