package cl.usach.traffictweet.Repositories;

import cl.usach.traffictweet.Models.Commune;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommuneRepository extends CrudRepository<Commune,Integer>{
    List<Commune> findByName(String name);

}
