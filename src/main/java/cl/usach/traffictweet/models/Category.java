package cl.usach.traffictweet.models;

import javax.persistence.*;
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
    private long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Occurrence> occurrences;
    public Category(){

    }
    public Category(String name) {
        this.name=name;
    }

    public long getCategoryId() {
        return id;
    }

    public void setCategoryId(long categoryId) {
        this.id = categoryId;
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