package egor.enrollment.repository;

import egor.enrollment.model.Statistics;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends CrudRepository<Statistics, Integer> {
    List<Statistics> findAllByDateBetween(LocalDateTime dateStart, LocalDateTime dateEnd);

}
