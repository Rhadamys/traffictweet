package cl.usach.traffictweet.Models;

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

    @Column(name = "category_key", unique = true, nullable = false)
    private String key;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Set<Occurrence> occurrences;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private Set<Keyword> keywords;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = new Date();
    }

    public Category() { }

    public Category(String name) {
        this.key = Util.clean(name);
        this.name = name;
        this.occurrences = new HashSet<>();
        this.keywords = new HashSet<>();
    }

    public int getId() {
        return id;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public Set<Occurrence> getOccurrences() {
        return occurrences;
    }
    public void addOccurrence(Occurrence occurrence) {
        this.occurrences.add(occurrence);
    }
    public Set<Keyword> getKeywords() {
        return keywords;
    }
    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
    }
    public Date getCreatedAt() {
        return createdAt;
    }
}