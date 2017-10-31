package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.CategoryMetric;
import cl.usach.traffictweet.utils.Month;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryMetricRepository extends CrudRepository<CategoryMetric,Integer>{
    List<CategoryMetric> findAllByDayAndMonthAndYearOrderByCategoryAsc(int day, Month month, int year);
    CategoryMetric findByDayAndMonthAndYearAndCategory(int day, Month month, int year, Category category);
}
