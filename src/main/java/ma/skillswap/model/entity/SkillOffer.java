package ma.skillswap.model.entity;

import jakarta.persistence.*;
import ma.skillswap.model.entity.enums.Level;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skill_offers")
@NamedQueries({
        @NamedQuery(name = "SkillOffer.findAll", query = "SELECT so FROM SkillOffer so ORDER BY so.datePublication DESC"),
        @NamedQuery(name = "SkillOffer.findByUser", query = "SELECT so FROM SkillOffer so WHERE so.user.id = :userId"),
        @NamedQuery(name = "SkillOffer.findBySkill", query = "SELECT so FROM SkillOffer so WHERE so.skill.id = :skillId"),
        @NamedQuery(name = "SkillOffer.findByCategory", query = "SELECT so FROM SkillOffer so WHERE so.skill.category.id = :categoryId"),
        @NamedQuery(name = "SkillOffer.search", query = "SELECT so FROM SkillOffer so WHERE LOWER(so.descriptionOffre) LIKE LOWER(:keyword) OR LOWER(so.skill.titreCompetence) LIKE LOWER(:keyword)")
})
public class SkillOffer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description_offre", columnDefinition = "TEXT", nullable = false)
    private String descriptionOffre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Level niveau;

    @Column(name = "date_publication", nullable = false)
    private LocalDate datePublication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @OneToMany(mappedBy = "skillOffer", cascade = CascadeType.ALL)
    private List<SwapRequest> swapRequests = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (datePublication == null) {
            datePublication = LocalDate.now();
        }
    }

    // Constructors
    public SkillOffer() {}

    public SkillOffer(String descriptionOffre, Level niveau, User user, Skill skill) {
        this.descriptionOffre = descriptionOffre;
        this.niveau = niveau;
        this.user = user;
        this.skill = skill;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescriptionOffre() { return descriptionOffre; }
    public void setDescriptionOffre(String descriptionOffre) { this.descriptionOffre = descriptionOffre; }

    public Level getNiveau() { return niveau; }
    public void setNiveau(Level niveau) { this.niveau = niveau; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public List<SwapRequest> getSwapRequests() { return swapRequests; }
    public void setSwapRequests(List<SwapRequest> swapRequests) { this.swapRequests = swapRequests; }
}