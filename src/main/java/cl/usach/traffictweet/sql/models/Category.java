package cl.usach.traffictweet.sql.models;

import cl.usach.traffictweet.utils.Util;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Category.findAll", query = "SELECT c FROM Category c")}
)
public class Category {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Set<Keyword> keywords;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Set<Metric> metrics;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Set<CategoryMetric> categoryMetrics;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = new Date();
    }

    public Category() { }

    public Category(String name) {
        this.name = name;
        this.keywords = new HashSet<>();
        this.metrics = new HashSet<>();
        this.categoryMetrics = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Keyword> getKeywords() {
        return keywords;
    }

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
    }

    public Set<Metric> getMetrics() {
        return metrics;
    }

    public Set<CategoryMetric> getCategoryMetrics() {
        return categoryMetrics;
    }
}