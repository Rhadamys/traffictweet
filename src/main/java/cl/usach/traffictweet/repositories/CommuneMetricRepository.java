package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Commune;
import cl.usach.traffictweet.models.CommuneMetric;
import cl.usach.traffictweet.utils.Month;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface CommuneMetricRepository extends CrudRepository<CommuneMetric,Integer>{
    List<CommuneMetric> findAllByMetricDateOrderByCommuneAsc(Date metricDate);
    CommuneMetric findByMetricDateAndCommune(Date metricDate, Commune commune);
    CommuneMetric findByCommune_Name(String communeName);
    CommuneMetric findByMetricDateAndCommune_Name(Date metricDate, String communeName);
    List<CommuneMetric> findByCommune_NameAndMetricDateBetweenOrderByMetricDateDesc(String communeName, Date from, Date to);
}
