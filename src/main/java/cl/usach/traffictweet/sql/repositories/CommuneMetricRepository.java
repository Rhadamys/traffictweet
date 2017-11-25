package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Commune;
import cl.usach.traffictweet.sql.models.CommuneMetric;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface CommuneMetricRepository extends CrudRepository<CommuneMetric,Integer>{
    List<CommuneMetric> findAllByMetricDateOrderByCommuneAsc(Date metricDate);
    CommuneMetric findByMetricDateAndCommune(Date metricDate, Commune commune);
    CommuneMetric findByCommune_Name(String communeName);
    CommuneMetric findByMetricDateAndCommune_Name(Date metricDate, String communeName);
    List<CommuneMetric> findByMetricDateBetweenOrderByCommuneAsc(Date from, Date to);
}
