package cl.usach.traffictweet.sql.services;

import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.CategoryMetric;
import cl.usach.traffictweet.sql.models.CommuneMetric;
import cl.usach.traffictweet.sql.models.Metric;
import cl.usach.traffictweet.sql.repositories.CategoryMetricRepository;
import cl.usach.traffictweet.sql.repositories.CommuneMetricRepository;
import cl.usach.traffictweet.sql.repositories.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

        return getMetricsByCommuneAndBetweenDates(commune, calendar.getTime(), now);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = {"commune", "date"})
    @ResponseBody
    public List<Metric> getMetricsByCommuneAndDate(
            @RequestParam("commune") String commune,
            @RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return getMetricsByCommuneAndBetweenDates(commune, calendar.getTime(), date);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = {"commune", "from", "to"})
    @ResponseBody
    public List<Metric> getMetricsByCommuneAndBetweenDates(
            @RequestParam("commune") String commune,
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        calendar.setTime(to);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        return metricRepository.findAllByCommune_NameAndMetricDateBetweenOrderByCategory(
                commune, from, calendar.getTime());
    }

    @RequestMapping(
            value = "/categories",
            method = RequestMethod.GET,
            params = {"from","to"})
    @ResponseBody
    public Map<String, Integer> getMetricsByCategoriesAndBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        calendar.setTime(to);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        List<CategoryMetric> metrics= categoryMetricRepository.findByMetricDateBetweenOrderByCategoryAsc(from,calendar.getTime());

       Map<String, Integer> sums = metrics.stream().collect(Collectors.groupingBy(CategoryMetric::getCategoryKey, Collectors.summingInt(CategoryMetric::getCount)));

        System.out.println(sums.toString());
        /*int totalOccurrences = 0;

        for (CategoryMetric metric : metrics) {
            totalOccurrences = metric.getCount()+totalOccurrences;
        }

        return totalOccurrences*/;
        return sums;
    }

}
