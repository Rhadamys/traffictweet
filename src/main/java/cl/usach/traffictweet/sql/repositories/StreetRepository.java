package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Street;
import org.springframework.data.repository.CrudRepository;

public interface StreetRepository extends CrudRepository<Street,Integer>{
    Street findByName(String name);
}
