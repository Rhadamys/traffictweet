package cl.usach.traffictweet.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
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

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "categories",
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Occurrence> occurrences;

    @OneToMany(fetch = FetchType.EAGER, mappedBy= "category", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Keyword> keywords;

    @Column(name="created_at", nullable=false)
    private Timestamp createdAt;

    @Column(name="updated_at", nullable=false)
    private Timestamp updatedAt;


    public Category(){

    }

    public Category(String name,Timestamp createdAt, Timestamp updatedAt) {
        this.name=name;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setKeywords(Set<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}