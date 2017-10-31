package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.CategoryMetric;
import cl.usach.traffictweet.models.Metric;
import cl.usach.traffictweet.repositories.CategoryMetricRepository;
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

    @RequestMapping(
            value = "/today",
            method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryMetric> getAll() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Month month = Month.values()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        return categoryMetricRepository.findAllByDayAndMonthAndYearOrderByCategoryAsc(day, month, year);
    }

    @RequestMapping(
            method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryMetric> getAll(@RequestParam("date") @DateTimeFormat(pattern="yyyy-MM-dd") Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Month month = Month.values()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);
        return categoryMetricRepository.findAllByDayAndMonthAndYearOrderByCategoryAsc(day, month, year);
    }
}
