package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.CategoryMetric;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CategoryMetricRepository extends CrudRepository<CategoryMetric,Integer>{
    List<CategoryMetric> findAllByMetricDateOrderByCategoryAsc(Date metricDate);
    CategoryMetric findByMetricDateAndCategory(Date metricDate, Category category);


    List<CategoryMetric> findByMetricDateBetweenOrderByCategoryAsc(@Param("from") Date from, @Param("to") Date to);
}
