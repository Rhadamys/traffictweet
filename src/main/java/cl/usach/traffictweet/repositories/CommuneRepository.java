package cl.usach.traffictweet.repositories;

import cl.usach.traffictweet.models.Commune;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommuneRepository extends CrudRepository<Commune,Integer>{
    Commune findByName(String name);
}
