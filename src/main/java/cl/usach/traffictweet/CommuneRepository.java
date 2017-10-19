package cl.usach.traffictweet;

import cl.usach.traffictweet.models.Commune;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommuneRepository extends PagingAndSortingRepository<Commune, Integer> {

}
