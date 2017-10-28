package cl.usach.traffictweet.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Occurrence> occurrences;

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
		this.occurrences = new HashSet<>();
	}

	public int getId() {
		return id;
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
	public Date getCreatedAt() {
		return createdAt;
	}
}