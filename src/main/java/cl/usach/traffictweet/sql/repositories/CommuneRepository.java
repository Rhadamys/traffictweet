package cl.usach.traffictweet.sql.repositories;

import cl.usach.traffictweet.sql.models.Commune;
import org.springframework.data.repository.CrudRepository;

public interface CommuneRepository extends CrudRepository<Commune,Integer>{
    Commune findByName(String name);
}
