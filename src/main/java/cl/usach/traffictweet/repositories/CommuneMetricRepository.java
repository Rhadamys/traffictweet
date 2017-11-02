package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Commune;
import cl.usach.traffictweet.models.CommuneMetric;
import cl.usach.traffictweet.utils.Month;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommuneMetricRepository extends CrudRepository<CommuneMetric,Integer>{
    List<CommuneMetric> findAllByDayAndMonthAndYearOrderByCommuneAsc(int day, Month month, int year);
    CommuneMetric findByDayAndMonthAndYearAndCommune(int day, Month month, int year, Commune commune);
}
