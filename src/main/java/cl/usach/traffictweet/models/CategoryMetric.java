package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.CategoryMetricRepository;
import cl.usach.traffictweet.repositories.MetricRepository;
import cl.usach.traffictweet.utils.Month;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "categories_metrics")
@NamedNativeQueries({
        @NamedNativeQuery(name = "CategoryMetric.findAll", query = "SELECT c FROM CategoryMetric c")})
public class CategoryMetric {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "day", nullable = false)
    private int day;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "month", nullable = false)
    private Month month;

    @Column(name = "year", nullable = false)
    private int year;

    public CategoryMetric() { }

    public CategoryMetric(Category category,
                          int day,
                          Month month,
                          int year) {
        this.category = category;
        this.day = day;
        this.month = month;
        this.year = year;
        this.count = 0;
    }

    public Category getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }

    public int getDay() {
        return day;
    }

    public Month getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void incrementCount() {
        this.count++;
    }

    public void increaseCount(int inc) {
        this.count += inc;
    }

    public static void update(
            CategoryMetricRepository categoryMetricRepository,
            Category category) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Month month = Month.values()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);

        CategoryMetric metric = categoryMetricRepository.findByDayAndMonthAndYearAndCategory(
                day, month, year, category);

        if(metric == null) metric = new CategoryMetric(category, day, month, year);
        metric.incrementCount();

        categoryMetricRepository.save(metric);
    }
}
