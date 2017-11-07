package cl.usach.traffictweet.sql.services;

import cl.usach.traffictweet.sql.models.CategoryMetric;
import cl.usach.traffictweet.sql.models.CommuneMetric;
import cl.usach.traffictweet.sql.models.Metric;
import cl.usach.traffictweet.sql.repositories.CategoryMetricRepository;
import cl.usach.traffictweet.sql.repositories.CommuneMetricRepository;
import cl.usach.traffictweet.sql.repositories.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@CrossOrigin
@RequestMapping("/metrics")
public class MetricService {
    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private CategoryMetricRepository categoryMetricRepository;

    @Autowired
    private CommuneMetricRepository communeMetricRepository;

    @RequestMapping(
            value = "/categories/today",
            method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryMetric> getAllCategoryMetricsOfToday() {
        return categoryMetricRepository.findAllByMetricDateOrderByCategoryAsc(new Date());
    }

    @RequestMapping(
            value = "/categories",
            method = RequestMethod.GET,
            params = "date")
    @ResponseBody
    public List<CategoryMetric> getAllCategoryMetricsByDate(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        return categoryMetricRepository.findAllByMetricDateOrderByCategoryAsc(date);
    }

    @RequestMapping(
            value = "/communes/today",
            method = RequestMethod.GET)
    @ResponseBody
    public List<CommuneMetric> getAllCommuneMetricsOfToday() {
        return communeMetricRepository.findAllByMetricDateOrderByCommuneAsc(new Date());
    }

    @RequestMapping(
            value = "/communes",
            method = RequestMethod.GET,
            params = "date")
    @ResponseBody
    public List<CommuneMetric> getAllCommuneMetricsByDate(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        return communeMetricRepository.findAllByMetricDateOrderByCommuneAsc(date);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = "commune")
    @ResponseBody
    public List<Metric> getMetricsByCommuneAndDate(@RequestParam("commune") String commune) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        calendar.setTime(now);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return metricRepository.findAllByCommune_NameAndMetricDateBetweenOrderByCategory(
                commune, calendar.getTime(), now);
    }

    /**
     * Get total amount of occurrences by commune and a date.
     * @return Amount of occurrences of a commune on an specific day.
     */
    @RequestMapping(
            method = RequestMethod.GET, params = {"commune", "date"})
    @ResponseBody
    public int getTotalOccurrencesByCommuneAndDate(
            @RequestParam("commune") String commune,
            @RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        CommuneMetric metric = communeMetricRepository.findByMetricDateAndCommune_Name(date, commune);
        return metric.getCount();
    }

    /**
     * Get total amount of occurrences by commune between days.
     * @return Amount of occurrences of a commune beetween two dates.
     */
    @RequestMapping(
            method = RequestMethod.GET, params = {"commune", "from", "to"})
    @ResponseBody
    public int getTotalOccurrencesByCommuneAndDates(
            @RequestParam("commune") String commune,
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        List<CommuneMetric> metrics= communeMetricRepository.findByCommune_NameAndMetricDateBetweenOrderByMetricDateDesc(commune,from,to);
        int totalOccurrences = 0;
        for (CommuneMetric metric : metrics) {
            totalOccurrences = metric.getCount()+totalOccurrences;
        }
        return totalOccurrences;
    }

    /**
     * Get total amount of occurrences by category on a commune.
     * @return Amount of occurrences of a commune on an specific category.
     */
    @RequestMapping(
            method = RequestMethod.GET, params = {"category", "commune"})
    @ResponseBody
    public int getTotalOccurrencesByCategoryAndCommune(
            @RequestParam("category") String category,
            @RequestParam("commune") String commune) {
        Metric metric = metricRepository.findByCategory_KeyAndCommune_Name(category,commune);
        return metric.getCount();
    }
}
