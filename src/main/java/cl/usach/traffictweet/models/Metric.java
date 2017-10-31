package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.MetricRepository;
import cl.usach.traffictweet.utils.Month;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "metrics")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Metric.findAll", query = "SELECT m FROM Metric m")})
public class Metric {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "commune_id")
    private Commune commune;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "day", nullable = false)
    private int day;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "month", nullable = false)
    private Month month;

    @Column(name = "year", nullable = false)
    private int year;

    public Metric() { }

    public Metric(Category category,
                  Commune commune,
                  int day,
                  Month month,
                  int year) {
        this.category = category;
        this.commune = commune;
        this.day = day;
        this.month = month;
        this.year = year;
        this.count = 0;
    }

    public Category getCategory() {
        return category;
    }

    public Commune getCommune() {
        return commune;
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
            MetricRepository metricRepository,
            Category category,
            Commune commune) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Month month = Month.values()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);

        Metric metric = metricRepository.findByDayAndMonthAndYearAndCategoryAndCommune(
                day, month, year, category, commune);

        if(metric == null) metric = new Metric(category, commune, day, month, year);
        metric.incrementCount();

        metricRepository.save(metric);
    }
}
