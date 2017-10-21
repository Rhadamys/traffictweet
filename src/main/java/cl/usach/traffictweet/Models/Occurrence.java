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

	public Occurrence() {
	}

	public Occurrence(String username, long account_id, String image, String location, String content, Timestamp createdAt, Timestamp updatedAt) {
		this.username = username;
		this.account_id = account_id;
		this.image = image;
		this.location = location;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName(){
		return username;
	}
	public void setName(String name) {
		this.username = name;
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

	public Commune getCommune() {
		return commune;
	}

	public void setCommune(Commune commune) {
		this.commune = commune;
	}

	public Set<Category> getCategories() {
        return categories;
    }
	public void addCategory(Category category) {
		this.categories.add(category);
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

