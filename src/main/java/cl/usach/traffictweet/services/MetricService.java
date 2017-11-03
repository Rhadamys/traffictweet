package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.CategoryMetric;
import cl.usach.traffictweet.models.CommuneMetric;
import cl.usach.traffictweet.models.Metric;
import cl.usach.traffictweet.repositories.CategoryMetricRepository;
import cl.usach.traffictweet.repositories.CommuneMetricRepository;
import cl.usach.traffictweet.repositories.MetricRepository;
import cl.usach.traffictweet.utils.Month;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    /**
     * Get all occurrence by commune.
     * @return All occurrences by commune.
     */
    @RequestMapping(
            value = "/todayCommunes",
            method = RequestMethod.GET)
    @ResponseBody
    public List<CommuneMetric> getAllByCommune() {
        return communeMetricRepository.findAllByMetricDateOrderByCommuneAsc(new Date());
    }

    /**
     * Get total amount of occurrences of a commune.
     * @return Amount of occurrences of a commune.
     */
    @RequestMapping(
            method = RequestMethod.GET, params = "commune")
    @ResponseBody
    public int getTotalOccurrencesByCommune(@RequestParam("commune") String commune) {
        CommuneMetric metric = communeMetricRepository.findByCommune_Name(commune);
        return metric.getCount();
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

    @RequestMapping(
            value = "/today",
            method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryMetric> getAll() {
        return categoryMetricRepository.findAllByMetricDateOrderByCategoryAsc(new Date());
    }

    @RequestMapping(
            method = RequestMethod.GET, params = "date")
    @ResponseBody
    public List<CategoryMetric> getAll(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        return categoryMetricRepository.findAllByMetricDateOrderByCategoryAsc(date);
    }
}
