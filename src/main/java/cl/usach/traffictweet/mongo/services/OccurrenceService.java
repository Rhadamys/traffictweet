package cl.usach.traffictweet.mongo.services;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.mongo.repositories.OccurrenceRepository;
import cl.usach.traffictweet.twitter.Lucene;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Month;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/occurrences")
public class OccurrenceService {
    @Autowired
    private OccurrenceRepository occurrenceRepository;

    /**
     * Return all occurrences.
     * @return All occurrences.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String, Object>> getAll() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        List<Occurrence> occurrences = occurrenceRepository.findAllByDateAfterOrderByDateDesc(calendar.getTime());
        System.out.println(calendar.getTime());
        return getCalendar(occurrences);
    }

    /**
     * Get an occurrence by tweet ID.
     * @param tweetId Tweet ID.
     * @return An occurrence matching given tweet ID.
     */
    @RequestMapping(
            value = "/{tweetId}",
            method = RequestMethod.GET)
    @ResponseBody
    public Occurrence getTweetById(@PathVariable("tweetId") String tweetId) {
        return occurrenceRepository.findByTweetId(tweetId);
    }

    /**
     * Get all occurrences by category.
     * @param categoryKey Category.
     * @return All occurrences that match the category.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = "category")
    @ResponseBody
    public List<Occurrence> findByCategory(@RequestParam("category") String categoryKey) {
        return occurrenceRepository.findAllByCategoriesContainsOrderByDateDesc(categoryKey);
    }

    /**
     * Get all occurrence by commune.
     * @param communeName CommuneNode.
     * @return All occurrences that match the commune.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = "commune")
    @ResponseBody
    public List<Occurrence> findByCommune(@RequestParam("commune") String communeName) {
        return occurrenceRepository.findAllByCommuneOrderByDateDesc(communeName);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = "search")
    @ResponseBody
    public List<HashMap<String, Object>> search(@RequestParam("search") String search) {
        List<org.apache.lucene.document.Document> hits = Lucene.search(search);

        List<Occurrence> occurrences =  new ArrayList<>();
        for (org.apache.lucene.document.Document hit: hits) {
            Occurrence occurrence = occurrenceRepository.findByTweetId(hit.get(Constant.TWEET_FIELD));
            occurrences.add(occurrence);
        }

        occurrences.sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        return getCalendar(occurrences);
    }

    /**
     * Get an occurrence by category.
     * @param from Date
     * @param to Date.
     * @return All occurrences that match the category.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = { "from", "to" })
    @ResponseBody
    public List<HashMap<String, Object>> findBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        List<Occurrence> occurrences = occurrenceRepository.findAllByDateBetweenOrderByDateDesc(
                from, calendar.getTime());
        return getCalendar(occurrences);
    }

    private List<HashMap<String, Object>> getCalendar(List<Occurrence> occurrences) {
        Calendar calendar = Calendar.getInstance();
        List<HashMap<String, Object>> occurrencesCalendar = new ArrayList<>();
        HashMap<String, Object> occurrencesDate = null;
        List<Occurrence> occurrencesFromDate = null;
        String currentDate = null;
        int currentDay = 0;
        for(Occurrence occurrence: occurrences) {
            calendar.setTime(occurrence.getDate());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if(day != currentDay) {
                if(occurrencesDate != null) {
                    occurrencesDate.put("date", currentDate);
                    occurrencesDate.put("occurrences", occurrencesFromDate);
                    occurrencesCalendar.add(occurrencesDate);
                }

                occurrencesDate = new HashMap<>();
                occurrencesFromDate = new ArrayList<>();

                String month = Month.values()[calendar.get(Calendar.MONTH)].toString().toLowerCase();
                currentDate = day + " de " + month +" de " + calendar.get(Calendar.YEAR);
                currentDay = day;
            }

            occurrencesFromDate.add(occurrence);
        }

        return occurrencesCalendar;
    }
}
