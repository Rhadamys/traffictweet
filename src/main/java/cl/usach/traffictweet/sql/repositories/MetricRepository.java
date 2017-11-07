package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.Commune;
import cl.usach.traffictweet.sql.models.Metric;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface MetricRepository extends CrudRepository<Metric,Integer>{
    List<Metric> findAllByMetricDateOrderByCategoryAsc(Date metricDate);
    Metric findByMetricDateAndCategoryAndCommune(Date metricDate, Category category, Commune commune);
    Metric findByCategory_KeyAndCommune_Name(String category, String commune);
}
