package cl.usach.traffictweet.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "communes")
@NamedQuery(name = "Commune.findAll", query = "SELECT c FROM Commune c")
public class Commune {
	@Id
	@Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long communeId;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@OneToMany(mappedBy = "commune", cascade = CascadeType.ALL)
	private Set<Occurrence> occurrences;

	public Commune() {
	}

	public long getCommuneId(){
		return communeId;
	}
	
	public void setCommuneId(long communeId){
		this.communeId = communeId;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Set<Occurrence> getOccurrences(){
		return occurrences;
	}

	public void setOccurrences(Set<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }
	
}