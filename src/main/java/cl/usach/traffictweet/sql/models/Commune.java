package cl.usach.traffictweet.sql.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "communes")
@NamedNativeQueries({
		@NamedNativeQuery(name = "Commune.findAll", query = "SELECT c FROM Commune c")})
public class Commune {
	@Id
	@Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@ManyToMany
	@JoinTable(name = "adjacent_communes",
			joinColumns = @JoinColumn(name = "commune_id", referencedColumnName = "id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "adjacent_id", referencedColumnName = "id", nullable = false))
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private Set<Commune> adjacentCommunes;

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private Set<Street> streets;

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private Set<Metric> metrics;

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private Set<CommuneMetric> communeMetrics;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@PrePersist
	void onCreate() {
		this.createdAt = new Date();
	}

	public Commune() { }

	public Commune(String name) {
		this.name = name;
		this.adjacentCommunes = new HashSet<>();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Commune> getAdjacentCommunes() {
		return adjacentCommunes;
	}

	public void addAdjacentCommune(Commune adjacent) {
		this.adjacentCommunes.add(adjacent);
	}

	public Set<Street> getStreets() {
		return streets;
	}

	public Set<Metric> getMetrics() {
		return metrics;
	}
}