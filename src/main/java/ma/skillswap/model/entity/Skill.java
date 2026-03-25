package ma.skillswap.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
@NamedQueries({
        @NamedQuery(name = "Skill.findAll", query = "SELECT s FROM Skill s ORDER BY s.titreCompetence"),
        @NamedQuery(name = "Skill.findByCategory", query = "SELECT s FROM Skill s WHERE s.category.id = :categoryId"),
        @NamedQuery(name = "Skill.findByTitle", query = "SELECT s FROM Skill s WHERE LOWER(s.titreCompetence) LIKE LOWER(:title)")
})
public class Skill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titre_competence", nullable = false, length = 150)
    private String titreCompetence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL)
    private List<SkillOffer> skillOffers = new ArrayList<>();

    // Constructors
    public Skill() {}

    public Skill(String titreCompetence, Category category) {
        this.titreCompetence = titreCompetence;
        this.category = category;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitreCompetence() { return titreCompetence; }
    public void setTitreCompetence(String titreCompetence) { this.titreCompetence = titreCompetence; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<SkillOffer> getSkillOffers() { return skillOffers; }
    public void setSkillOffers(List<SkillOffer> skillOffers) { this.skillOffers = skillOffers; }
}