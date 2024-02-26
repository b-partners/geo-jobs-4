package app.bpartners.geojobs.service.event;

import static org.mockito.Mockito.mock;

import app.bpartners.geojobs.repository.model.Parcel;
import app.bpartners.geojobs.repository.model.ParcelContent;
import app.bpartners.geojobs.repository.model.tiling.TilingTask;
import app.bpartners.geojobs.service.tiling.downloader.MockedTilesDownloader;
import java.util.List;
import org.junit.jupiter.api.Test;

class TilingTaskConsumerWithMockedDownloaderTest {

  TilingTaskConsumer subject = new TilingTaskConsumer(new MockedTilesDownloader(), mock());

  @Test
  void can_consume() {
    subject.accept(
        new TilingTask()
            .toBuilder()
                .parcels(
                    List.of(new Parcel().toBuilder().parcelContent(new ParcelContent()).build()))
                .build());
  }
}
