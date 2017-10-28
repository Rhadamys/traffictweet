package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Street;
import org.springframework.data.repository.CrudRepository;

public interface StreetRepository extends CrudRepository<Street,Integer>{
    Street findByName(String name);
}
