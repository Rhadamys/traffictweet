package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.CommuneMetricRepository;
import cl.usach.traffictweet.utils.Month;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Column(name = "metric_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date metricDate;

    public CommuneMetric() { }

    public CommuneMetric(Commune commune, Date metricDate) {
        this.commune = commune;
        this.metricDate = metricDate;
        this.count = 0;
    }

    public Commune getcommune() {
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
            CommuneMetricRepository CommuneMetricRepository,
            Commune commune) {
        Date today = new Date();

        CommuneMetric metric = CommuneMetricRepository
                .findByMetricDateAndCommune(today, commune);

        if(metric == null) metric = new CommuneMetric(commune, today);
        metric.incrementCount();

        CommuneMetricRepository.save(metric);
    }
}
