package cl.usach.traffictweet.sql.services;

import cl.usach.traffictweet.sql.models.*;
import cl.usach.traffictweet.sql.repositories.CategoryMetricRepository;
import cl.usach.traffictweet.sql.repositories.CommuneMetricRepository;
import cl.usach.traffictweet.sql.repositories.MetricRepository;
import cl.usach.traffictweet.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
            method = RequestMethod.GET,
            params = { "from", "to" })
    @ResponseBody
    public HashMap<String, Object> getMetricsDashboard(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        final Date dateTo = Util.getDateEnd(to);

        HashMap<String, Object> dashboard = new HashMap<>();
        List<HashMap<String, Object>> communeMetrics = new ArrayList<>();
        Map<Commune, Integer> communeSums = getCommuneMetricsSum(from, dateTo);
        communeSums.forEach((commune, comCount) -> {
            HashMap<String, Object> communeMetric = new HashMap<>();
            communeMetric.put("commune", commune.getName());
            communeMetric.put("count", comCount);

            List<HashMap<String, Object>> categoryMetrics = new ArrayList<>();
            Map<Category, Integer> categorySums = getMetricsByCommuneSum(commune.getName(), from, dateTo);
            categorySums.forEach((category, catCount) -> {
                categoryMetrics.add(getCategoryMetricMap(category, catCount));
            });

            communeMetric.put("categories", categoryMetrics);
            communeMetrics.add(communeMetric);
        });

        List<HashMap<String, Object>> categoryMetrics = new ArrayList<>();
        Map<Category, Integer> categorySums = getCategoryMetricsSum(from, dateTo);
        categorySums.forEach((category, catCount) -> {
            categoryMetrics.add(getCategoryMetricMap(category, catCount));
        });

        dashboard.put("communeMetrics", communeMetrics);
        dashboard.put("categoryMetrics", categoryMetrics);
        return dashboard;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = { "commune", "from", "to" })
    @ResponseBody
    public List<Metric> getMetricsByCommuneAndBetweenDates(
            @RequestParam("commune") String commune,
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        to = Util.getDateEnd(to);
        List<Metric> result = new ArrayList<>();
        Map<Category, Integer> sums = getMetricsByCommuneSum(commune, from, to);
        sums.forEach((key, value) -> result.add(new Metric(key, value)));
        return result;
    }

    @RequestMapping(
            value = "/categories",
            method = RequestMethod.GET,
            params = { "from", "to" })
    @ResponseBody
    public List<CategoryMetric> getMetricsByCategoriesAndBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to){
        to = Util.getDateEnd(to);
        List<CategoryMetric> result = new ArrayList<>();
        Map<Category, Integer> sums = getCategoryMetricsSum(from, to);
        sums.forEach((key, value) -> result.add(new CategoryMetric(key, value)));
        return result;
    }

    @RequestMapping(
            value = "/communes",
            method = RequestMethod.GET,
            params = { "from", "to" })
    @ResponseBody
    public List<CommuneMetric> getMetricsByCommunesAndBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to){
        to = Util.getDateEnd(to);
        List<CommuneMetric> result = new ArrayList<>();
        Map<Commune, Integer> sums = getCommuneMetricsSum(from, to);
        sums.forEach((key, value) -> result.add(new CommuneMetric(key, value)));

        return result;
    }

    private Map<Category, Integer> getMetricsByCommuneSum(String commune, Date from, Date to) {
        List<Metric> metrics = metricRepository.findAllByCommune_NameAndMetricDateBetweenOrderByCategory(
                commune, from,to);
        return metrics.stream().collect(
                Collectors.groupingBy(
                        Metric::getCategory,
                        Collectors.summingInt(Metric::getCount)));
    }
    private Map<Category, Integer> getCategoryMetricsSum(Date from, Date to) {
        List<CategoryMetric> metrics = categoryMetricRepository.findByMetricDateBetweenOrderByCategoryAsc(from, to);
        return metrics.stream().collect(
                Collectors.groupingBy(
                        CategoryMetric::getCategory,
                        Collectors.summingInt(CategoryMetric::getCount)));
    }
    private Map<Commune, Integer> getCommuneMetricsSum(Date from, Date to) {
        List<CommuneMetric> metrics= communeMetricRepository.findByMetricDateBetweenOrderByCommuneAsc(from, to);
        return metrics.stream().collect(
                Collectors.groupingBy(
                        CommuneMetric::getCommune,
                        Collectors.summingInt(CommuneMetric::getCount)));
    }
    private HashMap<String, Object> getCategoryMetricMap(Category category,int count) {
        HashMap<String, Object> categoryMetric = new HashMap<>();
        categoryMetric.put("category", category.getName());
        categoryMetric.put("count", count);
        return categoryMetric;
    }
}
