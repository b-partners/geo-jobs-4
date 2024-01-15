package school.hei.geotiler.service.event;

import static java.util.UUID.randomUUID;
import static school.hei.geotiler.file.FileUnzipper.stripExtension;
import static school.hei.geotiler.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.geotiler.endpoint.event.gen.ZoneTilingTaskCreated;
import school.hei.geotiler.endpoint.rest.model.TileCoordinates;
import school.hei.geotiler.file.BucketComponent;
import school.hei.geotiler.model.exception.ApiException;
import school.hei.geotiler.repository.model.Tile;
import school.hei.geotiler.repository.model.ZoneTilingTask;
import school.hei.geotiler.repository.model.geo.Parcel;
import school.hei.geotiler.service.ZoneTilingTaskStatusService;
import school.hei.geotiler.service.geo.TilesDownloader;

@Service
@AllArgsConstructor
public class ZoneTilingTaskCreatedService implements Consumer<ZoneTilingTaskCreated> {
  private final TilesDownloader tilesDownloader;
  private final BucketComponent bucketComponent;
  private final ZoneTilingTaskStatusService zoneTilingTaskStatusService;

  @Override
  public void accept(ZoneTilingTaskCreated zoneTilingTaskCreated) {
    ZoneTilingTask task = zoneTilingTaskCreated.getTask();
    zoneTilingTaskStatusService.process(task);

    try {
      File downloadedTiles = tilesDownloader.apply(zoneTilingTaskCreated.getTask().getParcel());
      String bucketKey = downloadedTiles.getName();
      bucketComponent.upload(downloadedTiles, bucketKey);
      setParcelTiles(downloadedTiles, task.getParcel(), bucketKey);
    } catch (Exception e) {
      zoneTilingTaskStatusService.fail(task);
      throw new ApiException(SERVER_EXCEPTION, e);
    }

    zoneTilingTaskStatusService.succeed(task);
  }

  private void setParcelTiles(File tilesDir, Parcel parcel, String bucketKey) {
    parcel.setTiles(getParcelTiles(new ArrayList<>(), tilesDir, parcel, bucketKey));
  }

  private List<Tile> getParcelTiles(
      List<Tile> accumulator, File tilesFile, Parcel parcel, String bucketKey) {
    if (!tilesFile.isDirectory()) {
      var enrichedAccumulator = new ArrayList<>(accumulator);

      String entryParentPath = tilesFile.getPath();
      String[] dir = entryParentPath.split("/");
      var x = Integer.valueOf(dir[dir.length - 2]);
      var z = Integer.valueOf(dir[dir.length - 3]);
      var y = Integer.valueOf(stripExtension(tilesFile.getName()));
      String bucketName = bucketComponent.getBucketName();
      enrichedAccumulator.add(
          Tile.builder()
              .id(randomUUID().toString())
              .creationDatetime(Instant.now().toString())
              .coordinates(new TileCoordinates().x(x).y(y).z(z))
              .bucketPath(bucketName + "/" + bucketKey + tilesFile.getPath())
              .build());

      return enrichedAccumulator;
    }

    return Arrays.stream(tilesFile.listFiles())
        .flatMap(subFile -> getParcelTiles(accumulator, subFile, parcel, bucketKey).stream())
        .collect(Collectors.toList());
  }
}
