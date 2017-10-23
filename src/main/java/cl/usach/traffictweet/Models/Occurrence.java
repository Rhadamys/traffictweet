package cl.usach.traffictweet.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
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

	@Column(name = "account_id", nullable = false)
	private long account_id;

	@Column(name = "image", nullable = false)
	private String image;

	@Column(name = "location", nullable = false)
	private String location;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "url", nullable = false)
	private String url;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "commune_id")
	@JsonIgnore
	private Commune commune;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "categories_occurrences",
			joinColumns = @JoinColumn(name = "occurrence_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
	@JsonIgnore
	private Set<Category> categories;

	@Column(name="created_at", nullable=false)
	private Timestamp createdAt;

	@Column(name="updated_at", nullable=false)
	private Timestamp updatedAt;

	public Occurrence() {}

	public Occurrence(String username,
					  long account_id,
					  String image,
					  String location,
					  String content,
					  String url,
					  Timestamp createdAt,
					  Timestamp updatedAt) {
		this.username = username;
		this.account_id = account_id;
		this.image = image;
		this.location = location;
		this.content = content;
		this.url = url;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getId() { return id;	}
	public String getUsername() { return username; }
	public long getAccount_id() { return account_id; }
	public String getImage() { return image; }
	public String getLocation() { return location; }
	public String getContent() { return content; }
	public String getUrl() { return url; }
	public Commune getCommune() { return commune; }
	public Timestamp getCreatedAt() { return createdAt; }
	public Timestamp getUpdatedAt() { return updatedAt; }
	public Set<Category> getCategories() { return categories; }
	public void addCategory(Category category) {
		this.categories.add(category);
	}
}

