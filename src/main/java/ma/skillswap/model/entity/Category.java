package ma.skillswap.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@NamedQueries({
        @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c ORDER BY c.nomCategorie"),
        @NamedQuery(name = "Category.findByName", query = "SELECT c FROM Category c WHERE c.nomCategorie = :name")
})
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_categorie", nullable = false, unique = true, length = 100)
    private String nomCategorie;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();

    // Constructors
    public Category() {}

    public Category(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomCategorie() { return nomCategorie; }
    public void setNomCategorie(String nomCategorie) { this.nomCategorie = nomCategorie; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
}