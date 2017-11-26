package cl.usach.traffictweet.mongo.services;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.mongo.repositories.OccurrenceRepository;
import cl.usach.traffictweet.twitter.Lucene;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Month;
import cl.usach.traffictweet.utils.Util;
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
        Date today = Util.getDateStart(new Date());
        List<Occurrence> occurrences = occurrenceRepository.findAllByDateAfterOrderByDateDesc(today);
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
     * Get all occurrences by filter.
     * @return All occurrences that match the filter.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = { "search", "commune", "category" })
    @ResponseBody
    public List<HashMap<String, Object>> filter(
            @RequestParam("search") String search,
            @RequestParam("commune") String commune,
            @RequestParam("category") String category) {
        List<Occurrence> occurrences = Lucene.search(search, commune, category);
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
        to = Util.getDateEnd(to);
        List<Occurrence> occurrences = occurrenceRepository.findAllByDateBetweenOrderByDateDesc(from, to);
        return getCalendar(occurrences);
    }

    private List<HashMap<String, Object>> getCalendar(List<Occurrence> occurrences) {
        if(occurrences.isEmpty()) return null;

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

        occurrencesDate.put("date", currentDate);
        occurrencesDate.put("occurrences", occurrencesFromDate);
        occurrencesCalendar.add(occurrencesDate);
        return occurrencesCalendar;
    }
}
