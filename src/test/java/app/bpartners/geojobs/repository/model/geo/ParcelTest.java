package app.bpartners.geojobs.repository.model.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ParcelTest {

  @Test
  void tiles_are_initialized_on_construction() {
    assertEquals(List.of(), new Parcel().getTiles());
  }

  @Test
  void tiles_cannot_be_set_to_null() {
    var parcel = new Parcel();
    parcel.setTiles(null);
    assertEquals(List.of(), parcel.getTiles());
  }
}
