package app.bpartners.geojobs.repository.model;

import static java.util.UUID.randomUUID;
import static org.hibernate.type.SqlTypes.JSON;

import app.bpartners.geojobs.endpoint.rest.model.Feature;
import app.bpartners.geojobs.endpoint.rest.model.GeoServerParameter;
import app.bpartners.geojobs.job.model.Status;
import app.bpartners.geojobs.repository.model.tiling.Tile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;

@Slf4j
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties({"tilingStatus", "firstTile"})
public class ParcelContent implements Serializable {
  private String id;
  private Feature feature;
  private URL geoServerUrl;
  private GeoServerParameter geoServerParameter;

  @Builder.Default
  @JdbcTypeCode(JSON)
  private List<Tile> tiles = new ArrayList<>();

  // TODO !!! VERY BAD ! Statuses must be separated from Parcel
  private Status tilingStatus; // computed value only
  private Status detectionStatus; // computed value only

  private Instant creationDatetime;

  public void setTiles(List<Tile> tiles) {
    if (tiles == null) {
      this.tiles = new ArrayList<>();
      return;
    }
    this.tiles = tiles;
  }

  public Tile getFirstTile() {
    if (tiles.isEmpty()) return null;
    var chosenTile = tiles.get(0);
    if (tiles.size() > 1) {
      log.info(
          "ParcelContent(id={}) contains multiple tiles but only one Tile(id={}) is handle for"
              + " now",
          getId(),
          chosenTile.getId());
    }
    return chosenTile;
  }

  public ParcelContent duplicate(String parcelContentId, boolean hasSameTile) {
    return ParcelContent.builder()
        .id(parcelContentId)
        .tiles(
            tiles.stream()
                .map(tile -> tile.duplicate(hasSameTile ? tile.getId() : randomUUID().toString()))
                .toList())
        .feature(feature)
        .creationDatetime(this.creationDatetime)
        .geoServerParameter(geoServerParameter)
        .geoServerUrl(geoServerUrl)
        .tilingStatus(null)
        .detectionStatus(null)
        .build();
  }

  public ParcelContent duplicate(String parcelContentId, List<Tile> newTiles) {
    return ParcelContent.builder()
        .id(parcelContentId)
        .tiles(newTiles)
        .feature(feature)
        .creationDatetime(this.creationDatetime)
        .geoServerParameter(geoServerParameter)
        .geoServerUrl(geoServerUrl)
        .tilingStatus(null)
        .detectionStatus(null)
        .build();
  }
}
