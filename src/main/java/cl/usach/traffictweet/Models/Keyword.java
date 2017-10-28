package cl.usach.traffictweet.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "keywords")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Keyword.findAll", query = "SELECT k FROM Keyword k")})
public class Keyword {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = new Date();
    }

    public Keyword() { }

    public Keyword(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Category getCategory() {
        return category;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
}
