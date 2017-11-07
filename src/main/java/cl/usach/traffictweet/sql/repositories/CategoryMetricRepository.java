package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.CategoryMetric;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface CategoryMetricRepository extends CrudRepository<CategoryMetric,Integer>{
    List<CategoryMetric> findAllByMetricDateOrderByCategoryAsc(Date metricDate);
    CategoryMetric findByMetricDateAndCategory(Date metricDate, Category category);
}
