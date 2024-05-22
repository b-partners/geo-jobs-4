package app.bpartners.geojobs.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.bpartners.geojobs.conf.FacadeIT;
import app.bpartners.geojobs.file.BucketCustomizedComponent;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.model.S3Object;

@Disabled(
    "TODO: must be launched only locally by configuration AWS credentials and EventConf.region")
public class BucketCustomizedComponentIT extends FacadeIT {
  @Autowired private BucketCustomizedComponent subject;

  @Test
  void list_objects_ok() {
    List<S3Object> actual = subject.listObjects("cannes-draft");

    assertEquals(6, actual.size());
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_layer/20/544785/383260.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_layer/20/544785/383261.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_layer/20/544785/383262.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_copy/20/544785/383260.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_copy/20/544785/383261.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_copy/20/544785/383262.jpg")));
  }

  @Test
  void list_objects_with_prefix_ok() {
    List<S3Object> actual = subject.listObjects("cannes-draft", "draft_layer");
    List<S3Object> actual2 = subject.listObjects("cannes-draft", "draft_copy");

    assertEquals(3, actual.size());
    assertEquals(3, actual2.size());
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_layer/20/544785/383260.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_layer/20/544785/383261.jpg")));
    assertTrue(
        actual.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_layer/20/544785/383262.jpg")));
    assertTrue(
        actual2.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_copy/20/544785/383260.jpg")));
    assertTrue(
        actual2.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_copy/20/544785/383261.jpg")));
    assertTrue(
        actual2.stream()
            .anyMatch(s3Object -> s3Object.key().equals("draft_copy/20/544785/383262.jpg")));
  }
}
