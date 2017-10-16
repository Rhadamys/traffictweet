package cl.usach.traffictweet.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "keywords")
@NamedQuery(name = "Keyword.findAll", query = "SELECT k FROM Keyword k")
public class Keyword {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long keywordId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Keyword() {
    }

    public long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
