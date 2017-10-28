package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Occurrence;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OccurrenceRepository extends CrudRepository<Occurrence,Integer>{
    List<Occurrence> findByDateBetween(Date start, Date end);
}
