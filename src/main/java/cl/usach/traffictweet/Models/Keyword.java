package cl.usach.traffictweet.Models;

import javax.persistence.*;
import java.sql.Timestamp;

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
    private Category category;

    @Column(name="created_at", nullable=false)
    private Timestamp createdAt;

    @Column(name="updated_at", nullable=false)
    private Timestamp updatedAt;

    public Keyword() {
    }

    public Keyword(String name, Timestamp createdAt, Timestamp updatedAt){
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
