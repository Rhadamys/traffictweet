package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.CommuneMetricRepository;
import cl.usach.traffictweet.utils.Month;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "communes_metrics")
@NamedNativeQueries({
        @NamedNativeQuery(name = "CommuneMetric.findAll", query = "SELECT c FROM CommuneMetric c")})
public class CommuneMetric {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    public CommuneMetric() { }

    public CommuneMetric(Commune commune,
                         int day,
                         Month month,
                         int year) {
        this.commune = commune;
        this.day = day;
        this.month = month;
        this.year = year;
        this.count = 0;
    }

    public Commune getcommune() {
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
            CommuneMetricRepository CommuneMetricRepository,
            Commune commune) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Month month = Month.values()[calendar.get(Calendar.MONTH)];
        int year = calendar.get(Calendar.YEAR);

        CommuneMetric metric = CommuneMetricRepository.findByDayAndMonthAndYearAndCommune(
                day, month, year, commune);

        if(metric == null) metric = new CommuneMetric(commune, day, month, year);
        metric.incrementCount();

        CommuneMetricRepository.save(metric);
    }
}
