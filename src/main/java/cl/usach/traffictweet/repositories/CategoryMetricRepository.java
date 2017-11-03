package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.CategoryMetric;
import cl.usach.traffictweet.utils.Month;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface CategoryMetricRepository extends CrudRepository<CategoryMetric,Integer>{
    List<CategoryMetric> findAllByMetricDateOrderByCategoryAsc(Date metricDate);
    CategoryMetric findByMetricDateAndCategory(Date metricDate, Category category);
}
