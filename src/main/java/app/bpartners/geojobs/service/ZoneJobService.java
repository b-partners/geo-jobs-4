package app.bpartners.geojobs.service;

import app.bpartners.geojobs.endpoint.event.EventProducer;
import app.bpartners.geojobs.model.BoundedPageSize;
import app.bpartners.geojobs.model.PageFromOne;
import app.bpartners.geojobs.model.exception.NotFoundException;
import app.bpartners.geojobs.repository.model.Job;
import app.bpartners.geojobs.repository.model.Task;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@AllArgsConstructor
public class ZoneJobService<T extends Task, J extends Job<T>> {
  protected final JpaRepository<J, String> repository;
  protected final EventProducer eventProducer;

  public List<J> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    return repository.findAll(pageable).toList();
  }

  public J findById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("ZoneJob.Id " + id + " not found"));
  }

  public J refreshStatus(J job) {
    job.refreshStatusHistory();
    return repository.save(job);
  }

  public J create(J job) {
    if (!job.isPending()) {
      throw new IllegalArgumentException(
          "Only PENDING job can be created. " + "You sure that tasks are all PENDING?");
    }

    return repository.save(job);
  }
}
