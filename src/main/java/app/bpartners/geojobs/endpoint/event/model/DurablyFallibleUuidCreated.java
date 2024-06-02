package app.bpartners.geojobs.endpoint.event.model;

import static java.lang.Math.random;

import app.bpartners.geojobs.PojaGenerated;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@PojaGenerated
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class DurablyFallibleUuidCreated extends PojaEvent {
  private UuidCreated uuidCreated;
  private int waitDurationBeforeConsumingInSeconds;
  private double failureRate;

  public boolean shouldFail() {
    return random() < failureRate;
  }

  @Override
  public Duration maxDuration() {
    return Duration.ofSeconds(
        waitDurationBeforeConsumingInSeconds + uuidCreated.maxDuration().toSeconds());
  }

  @Override
  public Duration maxBackoffBetweenRetries() {
    return uuidCreated.maxBackoffBetweenRetries();
  }
}