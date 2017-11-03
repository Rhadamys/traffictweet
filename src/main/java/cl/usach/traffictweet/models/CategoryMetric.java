package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.CategoryMetricRepository;
import cl.usach.traffictweet.utils.Month;
import org.springframework.format.annotation.DateTimeFormat;

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

    @Column(name = "metric_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date metricDate;

    public CategoryMetric() { }

    public CategoryMetric(Category category, Date metricDate) {
        this.category = category;
        this.metricDate = metricDate;
        this.count = 0;
    }

    public Category getCategory() {
        return category;
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
            CategoryMetricRepository categoryMetricRepository,
            Category category) {
        Date today = new Date();

        CategoryMetric metric = categoryMetricRepository
                .findByMetricDateAndCategory(today, category);

        if(metric == null) metric = new CategoryMetric(category, today);
        metric.incrementCount();

        categoryMetricRepository.save(metric);
    }
}
