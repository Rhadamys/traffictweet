package cl.usach.traffictweet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "streets")
@NamedNativeQueries({
        @NamedNativeQuery(name = "Street.findAll", query = "SELECT s FROM Street s")})
public class Street {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "commune_id")
    @JsonIgnore
    private Commune commune;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = new Date();
    }

    public Street() { }

    public Street(String name, Commune commune){
        this.name = name;
        this.commune = commune;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Commune getCommune() {
        return commune;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
}

