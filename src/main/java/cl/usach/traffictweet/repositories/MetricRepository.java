package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.Commune;
import cl.usach.traffictweet.models.Metric;
import cl.usach.traffictweet.utils.Month;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface MetricRepository extends CrudRepository<Metric,Integer>{
    List<Metric> findAllByMetricDateOrderByCategoryAsc(Date metricDate);
    Metric findByMetricDateAndCategoryAndCommune(Date metricDate, Category category, Commune commune);
    Metric findByCategory_KeyAndCommune_Name(String category, String commune);
}
