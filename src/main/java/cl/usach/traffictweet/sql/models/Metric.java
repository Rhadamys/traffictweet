package cl.usach.traffictweet.sql.models;

import cl.usach.traffictweet.sql.repositories.MetricRepository;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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

    @Column(name = "metric_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date metricDate;

    public Metric() { }

    public Metric(Category category,
                  Commune commune,
                  Date metricDate) {
        this.category = category;
        this.commune = commune;
        this.metricDate = metricDate;
        this.count = 0;
    }

    public Metric(Category category, int count) {
        this.category = category;
        this.commune = null;
        this.metricDate = null;
        this.count = count;
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

    public Date getMetricDate() {
        return metricDate;
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
        Date today = new Date();

        Metric metric = metricRepository
                .findByMetricDateAndCategoryAndCommune(today, category, commune);

        if(metric == null) metric = new Metric(category, commune, today);
        metric.incrementCount();

        metricRepository.save(metric);
    }
}
