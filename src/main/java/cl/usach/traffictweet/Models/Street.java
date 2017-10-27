package cl.usach.traffictweet.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;


@Entity
@Table(name = "streets")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Street.findAll", query = "SELECT s FROM Street s")})
public class Street {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = false, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "commune_id")
    @JsonIgnore
    private Commune commune;

    @Column(name="created_at", nullable=false)
    private Timestamp createdAt;

    @Column(name="updated_at", nullable=false)
    private Timestamp updatedAt;

    public Street() {
    }

    public Street(String name, Timestamp createdAt, Timestamp updatedAt, Commune commune){
        this.name=name;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
        this.commune=commune;

    }

    public Commune getCommune() {
        return commune;
    }

    public void setCommune(Commune commune) {
        this.commune = commune;
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

