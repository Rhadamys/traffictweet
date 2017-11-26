package cl.usach.traffictweet.mongo.repositories;

import cl.usach.traffictweet.mongo.models.Occurrence;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface OccurrenceRepository extends MongoRepository<Occurrence, String> {
    List<Occurrence> findAllByOrderByDateAsc();
    Occurrence findByTweetId(String tweetId);
    List<Occurrence> findAllByDateAfterOrderByDateDesc(Date date);
    List<Occurrence> findAllByDateBetweenOrderByDateDesc(Date from, Date to);
}