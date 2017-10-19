package cl.usach.traffictweet.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "occurrences")
@NamedNativeQueries({
		@NamedNativeQuery(name = "Occurrence.findAll", query = "SELECT o FROM Occurrence o")})
public class Occurrence {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "content", nullable = false)
	private String content;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "commune_id")
	private Commune commune;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "categories_occurrences",
			joinColumns = @JoinColumn(name = "occurrence_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
	private Set<Category> categories; 

	public Occurrence() {
	}

    public long getOccurrenceId(){
		return id;
	}
	public void setOccurrenceId(long occurrenceId){
		this.id = occurrenceId;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLocation(){
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public String getContent(){
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

    public Commune getCommunes() {
        return commune;
    }
	public void setCommunes(Commune commune) {
        this.commune = commune;
    }

    public Set<Category> getCategories() {
        return categories;
    }
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

}

