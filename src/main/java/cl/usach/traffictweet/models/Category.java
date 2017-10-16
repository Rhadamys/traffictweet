package cl.usach.traffictweet.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "categories")
@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c")
public class Category {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Occurrence> occurrences;
    
    public Category() {

    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
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

    public void setOccurrences(Set<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }
}