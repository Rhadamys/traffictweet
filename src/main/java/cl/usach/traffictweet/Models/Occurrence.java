package cl.usach.traffictweet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "occurrences")
@NamedNativeQueries({
		@NamedNativeQuery(name = "Occurrence.findAll", query = "SELECT o FROM Occurrence o")})
public class Occurrence {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "image", nullable = false)
	private String image;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "occurrence_date", nullable = false)
	private Date date;

	@ManyToOne
	@JoinColumn(name = "commune_id")
	private Commune commune;

	@ManyToMany(cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "categories_occurrences",
			joinColumns = @JoinColumn(name = "occurrence_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
	private Set<Category> categories;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@PrePersist
	void onCreate() {
		this.createdAt = new Date();
	}

	public Occurrence() { }

	public Occurrence(
			String username,
			String image,
			String text,
			Date date,
			Commune commune) {
		this.username = username;
		this.image = image;
		this.content = text;
		this.date = date;
		this.commune = commune;
		this.categories = new HashSet<>();
	}

	public int getId() { return id;	}
	public String getUsername() { return username; }
	public String getImage() { return image; }
	public String getContent() { return content; }
	public Date getDate() { return date; }
	public Commune getCommune() { return commune; }
	public Date getCreatedAt() { return createdAt; }
	public Set<Category> getCategories() { return categories; }
	public void addCategory(Category category) {
		this.categories.add(category);
	}
}

