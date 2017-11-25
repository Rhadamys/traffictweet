package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.Commune;
import cl.usach.traffictweet.sql.models.Metric;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface MetricRepository extends CrudRepository<Metric,Integer>{
    Metric findByMetricDateAndCategoryAndCommune(Date metricDate, Category category, Commune commune);
    List<Metric> findAllByCommune_NameAndMetricDateBetweenOrderByCategory(String commune, Date from, Date to);
}
