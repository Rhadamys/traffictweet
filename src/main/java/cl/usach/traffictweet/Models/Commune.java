package cl.usach.traffictweet.Models;

import javax.persistence.*;
import java.sql.Timestamp;
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
	private Set<Occurrence> occurrences;

	@Column(name="created_at", nullable=false)
	private Timestamp createdAt;

	@Column(name="updated_at", nullable=false)
	private Timestamp updatedAt;

	public Commune() {
	}

	public Commune(String name, Timestamp createdAt, Timestamp updatedAt){
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

	public void setOccurrences(Set<Occurrence> occurrences) {
		this.occurrences = occurrences;
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