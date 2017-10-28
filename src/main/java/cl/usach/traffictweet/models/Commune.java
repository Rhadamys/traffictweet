package cl.usach.traffictweet.models;

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

	@Column(name = "x", nullable = false)
	private double x;

	@Column(name = "y", nullable = false)
	private double y;

	@Column(name = "z", nullable = false)
	private int z;

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private Set<Occurrence> occurrences;

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	private Set<Street> streets;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@PrePersist
	void onCreate() {
		this.createdAt = new Date();
	}

	public Commune() { }

	public Commune(String name,
				   String x,
				   String y,
				   String z) {
		this.name = name;
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
		this.z = Integer.parseInt(z);
		this.occurrences = new HashSet<>();
	}

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	public Set<Occurrence> getOccurrences() {
		return occurrences;
	}
	public void addOccurrence(Occurrence occurrence) {
		this.occurrences.add(occurrence);
	}
	public Set<Street> getStreets() {
		return streets;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
}