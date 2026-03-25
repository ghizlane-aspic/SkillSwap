package ma.skillswap.model.entity;

import jakarta.persistence.*;
import ma.skillswap.model.entity.enums.SwapStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "swap_requests")
@NamedQueries({
        @NamedQuery(name = "SwapRequest.findByRequester", query = "SELECT sr FROM SwapRequest sr WHERE sr.requester.id = :userId ORDER BY sr.dateDemande DESC"),
        @NamedQuery(name = "SwapRequest.findByProvider", query = "SELECT sr FROM SwapRequest sr WHERE sr.provider.id = :userId ORDER BY sr.dateDemande DESC"),
        @NamedQuery(name = "SwapRequest.findByStatus", query = "SELECT sr FROM SwapRequest sr WHERE sr.statut = :status"),
        @NamedQuery(name = "SwapRequest.countByUser", query = "SELECT COUNT(sr) FROM SwapRequest sr WHERE (sr.requester.id = :userId OR sr.provider.id = :userId) AND sr.statut = :status")
})
public class SwapRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SwapStatus statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_offer_id", nullable = false)
    private SkillOffer skillOffer;

    @OneToOne(mappedBy = "swapRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    @OneToMany(mappedBy = "swapRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateEnvoi ASC")
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (dateDemande == null) {
            dateDemande = LocalDate.now();
        }
        if (statut == null) {
            statut = SwapStatus.PENDING;
        }
    }

    // Constructors
    public SwapRequest() {}

    public SwapRequest(User requester, User provider, SkillOffer skillOffer) {
        this.requester = requester;
        this.provider = provider;
        this.skillOffer = skillOffer;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }

    public SwapStatus getStatut() { return statut; }
    public void setStatut(SwapStatus statut) { this.statut = statut; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public User getProvider() { return provider; }
    public void setProvider(User provider) { this.provider = provider; }

    public SkillOffer getSkillOffer() { return skillOffer; }
    public void setSkillOffer(SkillOffer skillOffer) { this.skillOffer = skillOffer; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
}